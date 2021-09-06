INSERT INTO users(name, email)
VALUES ( :name, :email)
returning *;
