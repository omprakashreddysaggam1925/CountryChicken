ALTER TABLE customer_orders
    ADD COLUMN shipping_fee NUMERIC(12, 2) NOT NULL DEFAULT 0;

ALTER TABLE customer_orders
    ADD CONSTRAINT ck_customer_orders_shipping_fee CHECK (shipping_fee >= 0);
