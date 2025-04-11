--liquibase formatted sql

--changeset vyntyk:1

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);
--rollback DROP TABLE users;

--changeset vyntyk:2
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
--rollback DROP TABLE categories;

--changeset vyntyk:3
CREATE TABLE stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL
);
--rollback DROP TABLE stores;

--changeset vyntyk:4
UPDATE public.users SET
username = 'user', password = '123', role = 'ADMIN' WHERE id = 1;
--rollback UPDATE public.users SET

