ALTER table tickets add column theatre_id INT;
ALTER table tickets add CONSTRAINT fk_theatre
            FOREIGN KEY(theatre_id)
            REFERENCES theatres(id)
            on delete cascade
            on update cascade;
