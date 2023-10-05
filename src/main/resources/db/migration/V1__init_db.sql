CREATE TABLE IF NOT EXISTS Image
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    url       VARCHAR(2048) NOT NULL,
    public_id VARCHAR(255)  NOT NULL
);