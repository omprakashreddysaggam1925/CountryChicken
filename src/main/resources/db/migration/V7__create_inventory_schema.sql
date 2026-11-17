CREATE TABLE inventory_items (
    id UUID PRIMARY KEY,
    variant_id UUID NOT NULL,
    on_hand INTEGER NOT NULL DEFAULT 0,
    reserved INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_inventory_items_variant UNIQUE (variant_id),
    CONSTRAINT fk_inventory_items_variant FOREIGN KEY (variant_id) REFERENCES product_variants (id),
    CONSTRAINT ck_inventory_items_on_hand CHECK (on_hand >= 0),
    CONSTRAINT ck_inventory_items_reserved CHECK (reserved >= 0 AND reserved <= on_hand),
    CONSTRAINT ck_inventory_items_threshold CHECK (low_stock_threshold >= 0)
);

CREATE TABLE inventory_movements (
    id UUID PRIMARY KEY,
    inventory_item_id UUID NOT NULL,
    movement_type VARCHAR(32) NOT NULL,
    quantity INTEGER NOT NULL,
    reference_id UUID,
    reason VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_inventory_movements_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items (id),
    CONSTRAINT ck_inventory_movements_quantity CHECK (quantity > 0),
    CONSTRAINT ck_inventory_movements_type CHECK (
        movement_type IN ('ADDITION', 'DEDUCTION', 'RETURN', 'ADJUSTMENT', 'RESERVATION', 'RELEASE', 'COMMIT')
    )
);

CREATE TABLE stock_reservations (
    id UUID PRIMARY KEY,
    inventory_item_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_stock_reservations_item FOREIGN KEY (inventory_item_id) REFERENCES inventory_items (id),
    CONSTRAINT ck_stock_reservations_quantity CHECK (quantity > 0),
    CONSTRAINT ck_stock_reservations_status CHECK (status IN ('ACTIVE', 'RELEASED', 'COMMITTED', 'EXPIRED'))
);

CREATE INDEX idx_inventory_movements_item_created ON inventory_movements (inventory_item_id, created_at);
CREATE INDEX idx_stock_reservations_item_status ON stock_reservations (inventory_item_id, status);
CREATE INDEX idx_stock_reservations_expiry ON stock_reservations (expires_at, status);
