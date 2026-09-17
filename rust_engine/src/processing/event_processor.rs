use tokio::sync::mpsc::Receiver;

use crate::communication::java_client::JavaClient;
use crate::models::detection::DetectionRequest;
use crate::processing::correlation::CorrelationState;
use crate::processing::filtering::filter_event;
use crate::processing::normalization::normalize_event;
use crate::processing::parser::{parse_line, ParseError};
use crate::processing::pattern_detection::{detect_brute_force, PatternDetection};
use crate::processing::validation::validate_event;

pub async fn process_events(
    mut receiver: Receiver<String>,
    mut state: CorrelationState,
    java_client: JavaClient,
) {
    while let Some(line) = receiver.recv().await {
        match parse_line(&line) {
            Err(ParseError::InvalidJson { line, cause }) => {
                tracing::warn!(line, error = %cause, "Falha ao fazer parse");
            }
            Ok(event) => match validate_event(&event) {
                Err(error) => {
                    tracing::warn!(id = %event.id, "Evento inválido: {:?}", error);
                }
                Ok(()) => {
                    let event = normalize_event(event);
                    let event_id = event.id;

                    match filter_event(event) {
                        Some(event) => {
                            let ip = event.ip.clone();
                            tracing::info!(id = %event_id, "Evento aceito");

                            match java_client.send_event(&event).await {
                                Ok(()) => {
                                    tracing::info!(id = %event_id, "Evento enviado para o Java")
                                }
                                Err(error) => {
                                    tracing::warn!(id = %event_id, error = %error, "Falha ao enviar evento para o Java")
                                }
                            }

                            state.add_event(event);

                            match state.events_for_ip(&ip) {
                                Some(events) => {
                                    let detected = detect_brute_force(events);
                                    let report = state.report_brute_force(&ip, detected.is_some());

                                    match detected {
                                        Some(PatternDetection::BruteForce {
                                            ip,
                                            event_count,
                                            event_ids,
                                        }) if report => {
                                            let detection = DetectionRequest {
                                                pattern: "BRUTE_FORCE".to_string(),
                                                ip,
                                                event_count,
                                                event_ids,
                                            };

                                            match java_client.send_detection(&detection).await {
                                                Ok(()) => {
                                                    tracing::info!(
                                                        pattern = %detection.pattern,
                                                        ip = %detection.ip,
                                                        event_count = detection.event_count,
                                                        "BRUTE_FORCE detectado e enviado para o Java"
                                                    )
                                                }
                                                Err(error) => {
                                                    tracing::error!(
                                                        pattern = %detection.pattern,
                                                        ip = %detection.ip,
                                                        error = %error,
                                                        "Falha ao enviar detecção BRUTE_FORCE para o Java"
                                                    )
                                                }
                                            }
                                        }
                                        Some(_) => {
                                            tracing::info!(
                                                ip = %ip,
                                                "Episódio BRUTE_FORCE já reportado para IP {}",
                                                ip
                                            )
                                        }
                                        None => tracing::info!(
                                            ip = %ip,
                                            "Nenhum padrão suspeito para IP {}",
                                            ip
                                        ),
                                    }
                                }
                                None => tracing::info!(
                                    ip = %ip,
                                    "Nenhum evento encontrado para IP {}",
                                    ip
                                ),
                            }
                        }
                        None => {
                            tracing::info!(id = %event_id, "Evento descartado");
                        }
                    }
                }
            },
        }
    }
}
