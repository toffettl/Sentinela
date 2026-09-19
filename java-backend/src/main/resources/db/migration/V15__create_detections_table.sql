CREATE TABLE detections (
    id BIGSERIAL PRIMARY KEY,
    pattern VARCHAR(50) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    event_count INTEGER NOT NULL,
    event_ids TEXT NOT NULL,
    risk_points INTEGER NOT NULL
);
