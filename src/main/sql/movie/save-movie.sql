INSERT INTO movies(title, duration_in_minutes, theatre_id)
VALUES (:title, :duration_in_minutes, :theatreId)
returning *;
