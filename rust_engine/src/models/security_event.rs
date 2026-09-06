use chrono::{DateTime, Utc};
use serde::Deserialize;

#[derive(Deserialize)]
pub struct SecurityEvent {
    pub id: uuid::Uuid,
    pub event_type: String,
    pub timestamp: DateTime<Utc>,
    pub source: String,
    pub user: String,
    pub ip: String,
    pub asset: String,
}
