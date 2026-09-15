use std::collections::VecDeque;

use crate::models::security_event::SecurityEvent;

pub enum PatternDetection {
    BruteForce {
        ip: String,
        event_count: usize,
    },
}

pub fn detect_brute_force(
    events: &VecDeque<SecurityEvent>,
) -> Option<PatternDetection> {
    const BRUTE_FORCE_THRESHOLD: usize = 5;

    let mut login_failed = events
        .iter()
        .filter(|event| event.event_type == "LOGIN_FAILED");

    let count = login_failed.clone().count();
    let ip = login_failed.next().map(|event| event.ip.clone());

    match (ip, count >= BRUTE_FORCE_THRESHOLD) {
        (Some(ip), true) => Some(PatternDetection::BruteForce {
            ip,
            event_count: count,
        }),
        _ => None,
    }
}