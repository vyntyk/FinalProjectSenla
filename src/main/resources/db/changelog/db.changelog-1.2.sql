--liquibase formatted sql

--changeset vyntyk:1-update-stores-table
ALTER TABLE stores
    ALTER COLUMN address DROP NOT NULL;
--rollback ALTER TABLE stores ALTER COLUMN address SET NOT NULL;

--changeset vyntyk:2-update-products-table
-- Remove price and stock columns if they exist
ALTER TABLE products
    DROP COLUMN IF EXISTS price,
    DROP COLUMN IF EXISTS stock;
-- Add category column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                  WHERE table_name='products' AND column_name='category') THEN
        ALTER TABLE products ADD COLUMN category VARCHAR(100);
    END IF;
END; $$
--rollback ALTER TABLE products ADD COLUMN price NUMERIC(10,2) NOT NULL DEFAULT 0;
--rollback ALTER TABLE products ADD COLUMN stock INT NOT NULL DEFAULT 0;
--rollback ALTER TABLE products DROP COLUMN IF EXISTS category;

--changeset vyntyk:3-create-price-history-table
CREATE TABLE IF NOT EXISTS price_history (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    store_id INT NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);
--rollback DROP TABLE price_history;

--changeset vyntyk:4-fix-orders-table
-- Drop the orders table if it exists (it has incorrect structure)
DROP TABLE IF EXISTS orders CASCADE;
-- Drop the order_items table if it exists
DROP TABLE IF EXISTS order_items CASCADE;
--rollback CREATE TABLE IF NOT EXISTS orders (
--rollback     id SERIAL PRIMARY KEY,
--rollback     user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
--rollback     product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
--rollback     quantity INT NOT NULL,
--rollback     status VARCHAR(50) NOT NULL
--rollback );
--rollback CREATE TABLE IF NOT EXISTS order_items (
--rollback     id SERIAL PRIMARY KEY,
--rollback     order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
--rollback     product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
--rollback     quantity INT NOT NULL
--rollback );

--changeset vyntyk:5-insert-admin-role-relation
-- Add admin user to ADMIN role if not already assigned
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM users_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);
--rollback DELETE FROM users_roles WHERE user_id IN (SELECT id FROM users WHERE username = 'admin') AND role_id IN (SELECT id FROM roles WHERE name = 'ADMIN');
