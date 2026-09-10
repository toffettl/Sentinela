use crate::models::security_event::SecurityEvent;

pub fn normalize_event(mut event: SecurityEvent) -> SecurityEvent {
    event.event_type = event.event_type.trim().to_uppercase();
    event.source = event.source.trim().to_string();
    event.user = event.user.trim().to_string();
    event.ip = event.ip.trim().to_string();
    event.asset = event.asset.trim().to_string();

    event
}
