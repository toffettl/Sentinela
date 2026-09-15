use tokio::sync::mpsc::Receiver;

use crate::processing::correlation::CorrelationState;
use crate::processing::filtering::filter_event;
use crate::processing::normalization::normalize_event;
use crate::processing::parser::parse_line;
use crate::processing::pattern_detection::{detect_brute_force, PatternDetection};
use crate::processing::validation::validate_event;

pub async fn process_events(mut receiver: Receiver<String>, mut state: CorrelationState) {
    while let Some(line) = receiver.recv().await {
        match parse_line(&line) {
            Err(error) => {
                tracing::warn!("Falha ao fazer parse: {:?}", error);
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
                            state.add_event(event);

                            match state.events_for_ip(&ip) {
                                Some(events) => match detect_brute_force(events) {
                                    Some(PatternDetection::BruteForce { ip, event_count }) => {
                                        tracing::warn!(
                                            ip = %ip,
                                            event_count,
                                            "BRUTE_FORCE detectado na origem"
                                        )
                                    }
                                    None => tracing::info!(
                                        ip = %ip,
                                        "Nenhum padrão suspeito para IP {}",
                                        ip
                                    ),
                                },
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
