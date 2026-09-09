use axum::{http::StatusCode, Json};
use crate::models::security_event::SecurityEvent;

pub async fn post_events(Json(event): Json<SecurityEvent>) -> StatusCode {
    tracing::info!("Creating event: {}", event.id);
    StatusCode::CREATED
}
