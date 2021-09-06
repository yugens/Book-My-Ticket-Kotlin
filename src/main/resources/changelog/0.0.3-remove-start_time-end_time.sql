ALTER TABLE movies
    ADD COLUMN duration_in_minutes INTEGER;

UPDATE movies
    SET duration_in_minutes= EXTRACT(EPOCH FROM (end_time - start_time))/60;

ALTER TABLE movies
    DROP COLUMN start_time;

ALTER TABLE movies
    DROP COLUMN end_time;

ALTER TABLE movies
   ALTER COLUMN duration_in_minutes SET NOT NULL;
