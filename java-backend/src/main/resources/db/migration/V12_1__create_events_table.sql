CREATE TABLE events (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(50)  NOT NULL,
    timestamp   TIMESTAMP    NOT NULL,
    source      VARCHAR(255) NOT NULL,
    ip          VARCHAR(45)  NOT NULL,
    user_id     BIGINT       NOT NULL REFERENCES users(id),
    asset_id    BIGINT       NOT NULL REFERENCES assets(id)
);
