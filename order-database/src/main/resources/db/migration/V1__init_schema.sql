CREATE TABLE orders (
    id            UUID PRIMARY KEY,
    customer_id   UUID NOT NULL,
    status        VARCHAR(50) NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL
);

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
