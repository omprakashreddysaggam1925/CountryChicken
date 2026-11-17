CREATE TABLE delivery_zones (
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    postal_code_prefix VARCHAR(20) NOT NULL,
    shipping_fee NUMERIC(12, 2) NOT NULL,
    estimated_delivery_days INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_delivery_zones_fee CHECK (shipping_fee >= 0),
    CONSTRAINT ck_delivery_zones_days CHECK (estimated_delivery_days > 0)
);

CREATE TABLE shipments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    delivery_zone_id UUID NOT NULL,
    tracking_number VARCHAR(80) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    shipping_fee NUMERIC(12, 2) NOT NULL,
    estimated_delivery_at TIMESTAMP WITH TIME ZONE NOT NULL,
    shipped_at TIMESTAMP WITH TIME ZONE,
    delivered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_shipments_order FOREIGN KEY (order_id) REFERENCES customer_orders (id),
    CONSTRAINT fk_shipments_delivery_zone FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zones (id),
    CONSTRAINT ck_shipments_fee CHECK (shipping_fee >= 0),
    CONSTRAINT ck_shipments_status CHECK (status IN ('CREATED', 'PROCESSING', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'))
);

CREATE INDEX idx_delivery_zones_postal_prefix ON delivery_zones (postal_code_prefix, active);
CREATE INDEX idx_shipments_status ON shipments (status);
