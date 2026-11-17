CREATE TABLE customer_orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    delivery_address_id UUID NOT NULL,
    status VARCHAR(24) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer_orders_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_customer_orders_address FOREIGN KEY (delivery_address_id) REFERENCES delivery_addresses (id),
    CONSTRAINT ck_customer_orders_status CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT ck_customer_orders_subtotal CHECK (subtotal >= 0)
);

CREATE TABLE customer_order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    variant_id UUID NOT NULL,
    sku_snapshot VARCHAR(100) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    stock_reservation_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_customer_order_items_order FOREIGN KEY (order_id) REFERENCES customer_orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_order_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants (id),
    CONSTRAINT fk_customer_order_items_reservation FOREIGN KEY (stock_reservation_id) REFERENCES stock_reservations (id),
    CONSTRAINT ck_customer_order_items_price CHECK (unit_price >= 0 AND line_total >= 0),
    CONSTRAINT ck_customer_order_items_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_customer_orders_user_created ON customer_orders (user_id, created_at DESC);
CREATE INDEX idx_customer_orders_status ON customer_orders (status);
