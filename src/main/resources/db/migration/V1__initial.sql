CREATE TABLE products
(
    id             UUID PRIMARY KEY,
    price_amount   DECIMAL(19, 2) NOT NULL,
    price_currency VARCHAR(3)     NOT NULL
);

INSERT INTO products (id, price_amount, price_currency)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 100.00, 'USD');