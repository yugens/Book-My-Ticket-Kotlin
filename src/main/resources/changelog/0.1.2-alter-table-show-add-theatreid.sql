ALTER table shows add column theatre_id INT;
alter table shows add CONSTRAINT fk_theatre
            FOREIGN KEY(theatre_id)
            REFERENCES theatres(id)
            on delete cascade
            on update cascade;
