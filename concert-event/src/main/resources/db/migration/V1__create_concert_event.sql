CREATE TABLE IF NOT EXISTS concert_event (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name               TEXT    NOT NULL,
    description        TEXT    NOT NULL,
    available_tickets  INTEGER NOT NULL,
    ticket_price_cents INTEGER NOT NULL   -- price stored in cents to avoid floating point issues
);

CREATE TABLE IF NOT EXISTS event_artist (
    event_id  UUID NOT NULL REFERENCES concert_event(id) ON DELETE CASCADE,
    artist_id UUID,
    PRIMARY KEY (event_id, artist_id)
);

CREATE TABLE IF NOT EXISTS event_category (
    event_id    UUID NOT NULL REFERENCES concert_event(id)    ON DELETE CASCADE,
    category_id UUID,
    PRIMARY KEY (event_id, category_id)
);