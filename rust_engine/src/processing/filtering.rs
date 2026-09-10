use crate::models::security_event::SecurityEvent;

pub fn filter_event(event: SecurityEvent) -> Option<SecurityEvent> {
    match event.event_type.as_str() {
        "LOGIN_FAILED" => Some(event),
        "LOGIN_SUCCESS" => Some(event),
        "SYSTEM_START" => Some(event),
        "HEARTBEAT" => None,
        "DEBUG" => None,
        _ => None,
    }
}
