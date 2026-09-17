use std::collections::{HashMap, HashSet, VecDeque};

use crate::models::security_event::SecurityEvent;

pub struct CorrelationState {
    events_by_ip: HashMap<String, VecDeque<SecurityEvent>>,
    flagged_brute_force: HashSet<String>,
}

impl CorrelationState {
    pub fn new() -> Self {
        Self {
            events_by_ip: HashMap::new(),
            flagged_brute_force: HashSet::new(),
        }
    }

    pub fn add_event(&mut self, event: SecurityEvent) {
        let events = self
            .events_by_ip
            .entry(event.ip.clone())
            .or_default();
        let newest = event.timestamp;
        events.push_back(event);

        const WINDOW: chrono::Duration = chrono::Duration::seconds(60);

        while events.front().is_some_and(|oldest| {
            newest - oldest.timestamp > WINDOW
        }) {
            events.pop_front();
        }
    }

    pub fn events_for_ip(&self, ip: &str) -> Option<&VecDeque<SecurityEvent>> {
        self.events_by_ip.get(ip)
    }

    pub fn report_brute_force(&mut self, ip: &str, detected: bool) -> bool {
        if detected {
            if self.flagged_brute_force.contains(ip) {
                return false;
            }
            self.flagged_brute_force.insert(ip.to_string());
            true
        } else {
            self.flagged_brute_force.remove(ip);
            false
        }
    }
}
