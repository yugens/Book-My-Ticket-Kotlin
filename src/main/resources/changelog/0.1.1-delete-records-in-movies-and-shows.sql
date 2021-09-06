ALTER TABLE tickets
    DROP CONSTRAINT fk_show,
    ADD CONSTRAINT fk_show
        FOREIGN KEY(show_id)
        REFERENCES shows(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE tickets
    DROP CONSTRAINT fk_user,
    ADD CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE;

DELETE FROM movies;

DELETE FROM shows;
