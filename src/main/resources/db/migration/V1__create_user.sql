CREATE TABLE IF NOT EXISTS "USER" (
    id UUID PRIMARY KEY NOT NULL,
    username VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    salt VARCHAR NOT NULL,
    email VARCHAR,
    first_name VARCHAR,
    last_name VARCHAR
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username ON "USER" (username);
