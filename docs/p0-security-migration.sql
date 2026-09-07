-- One-time migration for databases created before Lynqo's token rotation model.
-- This intentionally invalidates every legacy login session.

DELETE FROM token;

ALTER TABLE token
    MODIFY token VARCHAR(36) NOT NULL,
    MODIFY token_type VARCHAR(16) NOT NULL,
    MODIFY revoked TINYINT(1) NOT NULL DEFAULT 0,
    MODIFY expired TINYINT(1) NOT NULL DEFAULT 0,
    MODIFY user_id INT NOT NULL,
    ADD CONSTRAINT uq_token_jti UNIQUE (token);
