-- liquibase formatted sql

-- changeset fsalyakhov:1
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20),
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(10) NOT NULL,
    image VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE ads (
    id SERIAL PRIMARY KEY,
    image VARCHAR(100),
    price INT NOT NULL,
    title VARCHAR(50) NOT NULL,
    author INT NOT NULL,
    description VARCHAR(64),
    CONSTRAINT ad_author
    FOREIGN KEY (author) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    author_image VARCHAR(50),
    author_first_name VARCHAR(20),
    created_at BIGINT NOT NULL,
    text VARCHAR(64) NOT NULL,
    author INT NOT NULL,
    CONSTRAINT com_author
    FOREIGN KEY (author) REFERENCES users (id) ON DELETE CASCADE
);