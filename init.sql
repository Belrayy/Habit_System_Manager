DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- The password : "admin123"
INSERT INTO users (username, password_hash, email, first_name, last_name)
SELECT 'admin', '$2a$12$qL3B6z7x8p9o0i1u2y3z4A5B6C7D8E9F0G1H2I3J4K5L6M7N8O9P0Q1R',
       'admin@example.com', 'Admin', 'User'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');