CREATE TABLE assets (
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    hostname          VARCHAR(255) NOT NULL,
    ip                VARCHAR(45)  NOT NULL,
    operating_system  VARCHAR(100),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
