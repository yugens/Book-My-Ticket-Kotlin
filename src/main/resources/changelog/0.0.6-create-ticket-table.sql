CREATE TABLE tickets(
    ticket_number SERIAL PRIMARY KEY,
    show_id INT,
    user_id INT,
        CONSTRAINT fk_show
        FOREIGN KEY(show_id)
        REFERENCES shows(id),
        CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);
