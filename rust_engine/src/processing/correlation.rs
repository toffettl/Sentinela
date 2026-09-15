use std::collections::{HashMap, VecDeque};

use crate::models::security_event::SecurityEvent;

pub struct CorrelationState {
    events_by_ip: HashMap<String, VecDeque<SecurityEvent>>,
}

impl CorrelationState {
    pub fn new() -> Self {
        Self {
            events_by_ip: HashMap::new(),
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
}
