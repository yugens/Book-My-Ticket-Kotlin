SELECT t.ticket_number, th.name as theatre_name , m.title as movie_name,s.start_time,m.duration_in_minutes , u.name as user_name
FROM tickets t INNER JOIN shows s ON t.show_id =s.id INNER Join movies m on s.movie_id = m.id INNER join theatres th On m.theatre_id=th.id
inner join users u on t.user_id =u.id
where t.ticket_number =:ticket_number
