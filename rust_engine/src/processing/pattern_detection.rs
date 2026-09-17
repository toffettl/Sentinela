use std::collections::VecDeque;

use crate::models::security_event::SecurityEvent;

pub enum PatternDetection {
    BruteForce {
        ip: String,
        event_count: usize,
        event_ids: Vec<uuid::Uuid>,
    },
}

pub fn detect_brute_force(
    events: &VecDeque<SecurityEvent>,
) -> Option<PatternDetection> {
    const BRUTE_FORCE_THRESHOLD: usize = 5;

    let login_failed = events
        .iter()
        .filter(|event| event.event_type == "LOGIN_FAILED");

    let count = login_failed.clone().count();
    let ip = login_failed.clone().next().map(|event| event.ip.clone());
    let event_ids = login_failed.map(|event| event.id).collect();

    match (ip, count >= BRUTE_FORCE_THRESHOLD) {
        (Some(ip), true) => Some(PatternDetection::BruteForce {
            ip,
            event_count: count,
            event_ids,
        }),
        _ => None,
    }
}