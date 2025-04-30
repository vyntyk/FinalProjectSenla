--liquibase formatted sql

--changeset vyntyk:1-create-users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE
);
--rollback DROP TABLE users;

--changeset vyntyk:2-create-roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);
--rollback DROP TABLE roles;

--changeset vyntyk:3-create-users_roles
CREATE TABLE IF NOT EXISTS users_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);
--rollback DROP TABLE users_roles;

--changeset vyntyk:4-add-fk-users_roles
ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;
--rollback
    ALTER TABLE users_roles DROP CONSTRAINT fk_users_roles_user;
    ALTER TABLE users_roles DROP CONSTRAINT fk_users_roles_role;

--changeset vyntyk:5-insert-default-roles
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('USER')  ON CONFLICT DO NOTHING;
--rollback DELETE FROM roles WHERE name IN ('ADMIN','USER');

--changeset vyntyk:6-create-categories
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
--rollback DROP TABLE categories;

--changeset vyntyk:7-create-stores
CREATE TABLE IF NOT EXISTS stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL
);
--rollback DROP TABLE stores;

--changeset vyntyk:8-init-admin-user
INSERT INTO users (username, password, email)
    VALUES ('admin', '$2a$10$examplebcrypthashhere', 'admin@example.com')
    ON CONFLICT (username) DO UPDATE SET password=EXCLUDED.password, email=EXCLUDED.email;
--rollback DELETE FROM users WHERE username = 'admin';
