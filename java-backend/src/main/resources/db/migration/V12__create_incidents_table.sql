CREATE TABLE incidents (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    severity        VARCHAR(20)  NOT NULL,
    risk_score      INTEGER      NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    user_involved   VARCHAR(255),
    ip_involved     VARCHAR(45),
    asset_involved  VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
