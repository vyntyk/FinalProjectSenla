INSERT INTO stores (name, address) VALUES
  ('Store A', 'Address A'),
  ('Store B', 'Address B');

INSERT INTO categories (name) VALUES
  ('Category A'),
  ('Category B');

INSERT INTO roles (name) VALUES
  ('ROLE_USER'),
  ('ROLE_ADMIN');

-- Password is 'password' encoded with BCrypt
INSERT INTO users (username, password, email) VALUES
  ('testuser', '$2a$10$eDhncK/4cNH/XZ0Z5JORYuXiuOTHjfQ4TV5RlubwxBnbY5PkIzrUi', 'test@example.com'),
  ('admin', '$2a$10$eDhncK/4cNH/XZ0Z5JORYuXiuOTHjfQ4TV5RlubwxBnbY5PkIzrUi', 'admin@example.com');

INSERT INTO users_roles (user_id, role_id) VALUES
  (1, 1), -- testuser has ROLE_USER role
  (2, 1), -- admin has ROLE_USER role
  (2, 2); -- admin also has ROLE_ADMIN role
