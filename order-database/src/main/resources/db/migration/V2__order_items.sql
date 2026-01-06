CREATE TABLE order_items (
    id           UUID PRIMARY KEY,
    order_id     UUID NOT NULL,
    product_id   UUID NOT NULL,
    quantity     INTEGER NOT NULL,
    unit_amount  NUMERIC(19,4) NOT NULL,
    currency     CHAR(3) NOT NULL,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id
    ON order_items(order_id);
