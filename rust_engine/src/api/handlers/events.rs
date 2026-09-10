use crate::{
    models::security_event::SecurityEvent,
    processing::{
        filtering::filter_event, normalization::normalize_event, validation::validate_event,
    },
};
use axum::{http::StatusCode, Json};

pub async fn post_events(Json(event): Json<SecurityEvent>) -> StatusCode {
    match validate_event(&event) {
        Ok(()) => {
            tracing::info!(id = %event.id, "Evento recebido");
            let normalized_event = normalize_event(event);

            tracing::info!(id = %normalized_event.id, "Evento normalizado");

            match filter_event(normalized_event) {
                Some(_) => {
                    tracing::info!("Evento aceito");
                    StatusCode::CREATED
                }
                None => {
                    tracing::info!("Evento descartado pelo filtro");
                    StatusCode::NO_CONTENT
                }
            }
        }
        Err(error) => {
            tracing::warn!("Validação falhou: {:?}", error);
            StatusCode::BAD_REQUEST
        }
    }
}
