CREATE TABLE shows(
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMPTZ NOT NULL,
    movie_id INT,
        CONSTRAINT fk_movie
        FOREIGN KEY(movie_id)
        REFERENCES movies(id)
        on delete cascade
        on update cascade
);
