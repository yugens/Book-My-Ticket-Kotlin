ALTER TABLE shows
    ADD COLUMN total_seats INTEGER;

UPDATE shows
    SET total_seats=100;

ALTER TABLE shows
    ALTER COLUMN total_seats SET NOT NULL;
