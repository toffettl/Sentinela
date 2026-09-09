use crate::models::security_event::SecurityEvent;
use std::net::IpAddr;

#[derive(Debug)]
pub enum ValidationError {
    MissingEventType,
    MissingSource,
    MissingUser,
    InvalidIp,
    MissingAsset,
}

pub fn validate_event(event: &SecurityEvent) -> Result<(), ValidationError> {
    if event.event_type.trim().is_empty() {
        return Err(ValidationError::MissingEventType);
    }

    if event.source.trim().is_empty() {
        return Err(ValidationError::MissingSource);
    }

    if event.user.trim().is_empty() {
        return Err(ValidationError::MissingUser);
    }

    if event.ip.parse::<IpAddr>().is_err() {
        return Err(ValidationError::InvalidIp);
    }

    if event.asset.trim().is_empty() {
        return Err(ValidationError::MissingAsset);
    }

    Ok(())
}
