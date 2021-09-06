INSERT INTO shows(start_time,movie_id,theatre_id, total_seats)
VALUES(:start_time,:movie_id,:theatre_id, :total_seats)
returning *;
