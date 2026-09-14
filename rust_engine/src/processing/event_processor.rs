use tokio::sync::mpsc::Receiver;

use crate::processing::filtering::filter_event;
use crate::processing::normalization::normalize_event;
use crate::processing::parser::parse_line;
use crate::processing::validation::validate_event;

pub async fn process_events(mut receiver: Receiver<String>) {
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
                        Some(_) => {
                            tracing::info!(id = %event_id, "Evento aceito");
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
