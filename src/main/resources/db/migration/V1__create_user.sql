CREATE TABLE IF NOT EXISTS user (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    salt VARCHAR(256) NOT NULL,
    email VARCHAR(256),
    first_name VARCHAR(256),
    last_name VARCHAR(256)
);

CREATE UNIQUE INDEX idx_user_username ON user (username);
