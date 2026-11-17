ALTER TABLE customer_orders
    ADD COLUMN discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN coupon_code VARCHAR(64);

ALTER TABLE customer_orders
    ADD CONSTRAINT ck_customer_orders_discount_amount CHECK (discount_amount >= 0);
