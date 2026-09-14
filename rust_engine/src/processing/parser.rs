use crate::models::security_event::SecurityEvent;

pub fn parse_line(line: &str) -> Result<SecurityEvent, ParseError> {
    serde_json::from_str::<SecurityEvent>(line).map_err(|cause| ParseError::InvalidJson {
        line: line.to_string(),
        cause,
    })
}

#[derive(Debug)]
pub enum ParseError {
    InvalidJson {
        line: String,
        cause: serde_json::Error,
    },
}

