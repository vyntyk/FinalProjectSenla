-- ...existing code...

-- Создание таблицы products, если не существует
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    stock INT NOT NULL
);

-- Создание таблицы orders, если не существует
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Создание таблицы order_items, если не существует
CREATE TABLE IF NOT EXISTS order_items (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL
);

-- Пример: добавление пользователей
INSERT INTO users (id, username, email, password) VALUES
  (101, 'testuser1', 'test1@example.com', 'password1'),
  (102, 'testuser2', 'test2@example.com', 'password2');

-- Пример: добавление продуктов
INSERT INTO products (id, name, description, price, stock) VALUES
  (201, 'Тестовый продукт 1', 'Описание продукта 1', 100.00, 50),
  (202, 'Тестовый продукт 2', 'Описание продукта 2', 200.00, 30);

-- Пример: добавление заказов
INSERT INTO orders (id, user_id, product_id, quantity, status) VALUES
  (301, 101, 201, 2, 'CREATED'),
  (302, 102, 202, 1, 'COMPLETED');

-- Пример: добавление связей заказ-продукт (если требуется)
INSERT INTO order_items (id, order_id, product_id, quantity) VALUES
  (401, 301, 201, 2),
  (402, 302, 202, 1);

-- ...existing code...

