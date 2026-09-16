CREATE TABLE incident_notes (
    id          BIGSERIAL PRIMARY KEY,
    incident_id BIGINT    NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    content     TEXT      NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
