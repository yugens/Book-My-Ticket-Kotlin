ALTER TABLE movies
ADD COLUMN theatre_id INTEGER,
ADD CONSTRAINT fk_theatre
    FOREIGN KEY(theatre_id)
    REFERENCES theatres(id);

UPDATE movies
    SET theatre_id = 1;

ALTER TABLE movies
    ALTER COLUMN theatre_id SET NOT NULL;
