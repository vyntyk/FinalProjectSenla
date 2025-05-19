--liquibase formatted sql

--changeset vyntyk:1.1-create-products
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL
);
--rollback DROP TABLE products;

--changeset vyntyk:1.1-create-orders
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL
);
--rollback DROP TABLE orders;

--changeset vyntyk:1.1-create-order-items
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL
);
--rollback DROP TABLE order_items;

--changeset vyntyk:1.1-insert-users
INSERT INTO users (id, username, email, password) VALUES
  (101, 'testuser1', 'test1@example.com', 'password1'),
  (102, 'testuser2', 'test2@example.com', 'password2')
ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM users WHERE id IN (101, 102);

--changeset vyntyk:1.1-insert-products
INSERT INTO products (id, name, description, category) VALUES
  (201, 'Тестовый продукт 1', 'Описание продукта 1', 'Электроника'),
  (202, 'Тестовый продукт 2', 'Описание продукта 2', 'Одежда'),
  (203, 'Тестовый продукт 3', 'Описание продукта 3', 'Продукты')
ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM products WHERE id IN (201, 202, 203);

--changeset vyntyk:1.1-insert-orders
INSERT INTO orders (id, user_id, product_id, quantity, status) VALUES
  (301, 101, 201, 2, 'CREATED'),
  (302, 102, 202, 1, 'COMPLETED');
--rollback DELETE FROM orders WHERE id IN (301, 302);

--changeset vyntyk:1.1-insert-order-items
INSERT INTO order_items (id, order_id, product_id, quantity) VALUES
  (401, 301, 201, 2),
  (402, 302, 202, 1);
--rollback DELETE FROM order_items WHERE id IN (401, 402);

--changeset vyntyk:1.1-insert-roles
INSERT INTO roles (id, name) VALUES
  (1, 'ADMIN'),
  (2, 'USER')
ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM roles WHERE id IN (1, 2);

--changeset vyntyk:1.1-insert-users-roles
INSERT INTO users_roles (user_id, role_id) VALUES
  (101, 2),
  (102, 2),
  (101, 1)
ON CONFLICT DO NOTHING;
--rollback DELETE FROM users_roles WHERE user_id IN (101, 102) AND role_id IN (1, 2);

--changeset vyntyk:1.1-insert-categories
INSERT INTO categories (id, name) VALUES
  (1, 'Электроника'),
  (2, 'Одежда'),
  (3, 'Продукты')
ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM categories WHERE id IN (1, 2, 3);

--changeset vyntyk:1.1-insert-stores
INSERT INTO stores (id, name, address) VALUES
  (1, 'Магазин №1', 'ул. Ленина, 1'),
  (2, 'Магазин №2', 'ул. Победы, 10')
ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM stores WHERE id IN (1, 2);