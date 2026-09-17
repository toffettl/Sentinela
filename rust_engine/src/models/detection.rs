use serde::Serialize;

#[derive(Serialize)]
pub struct DetectionRequest {
    pub pattern: String,
    pub ip: String,
    pub event_count: usize,
    pub event_ids: Vec<uuid::Uuid>,
}