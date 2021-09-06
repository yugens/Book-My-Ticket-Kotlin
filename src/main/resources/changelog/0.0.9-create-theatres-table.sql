CREATE TABLE theatres (
 id SERIAL PRIMARY KEY,
 name VARCHAR NOT NULL UNIQUE
);

INSERT INTO theatres
(name) VALUES
('Medly');
