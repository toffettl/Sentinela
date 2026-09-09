use crate::{models::security_event::SecurityEvent, processing::validation::validate_event};
use axum::{http::StatusCode, Json};

pub async fn post_events(Json(event): Json<SecurityEvent>) -> StatusCode {
    match validate_event(&event) {
        Ok(()) => {
            tracing::info!("Creating event: {}", event.id);
            StatusCode::CREATED
        }
        Err(error) => {
            tracing::warn!("Validação falhou: {:?}", error);
            StatusCode::BAD_REQUEST
        }
    }
}
