INSERT INTO tickets(show_id, user_id, theatre_id)
VALUES ( :showId, :userId, :theatreId)
returning *;
