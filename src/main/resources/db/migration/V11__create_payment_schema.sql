CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    provider VARCHAR(32) NOT NULL,
    provider_order_id VARCHAR(128) UNIQUE,
    provider_payment_id VARCHAR(128) UNIQUE,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(24) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES customer_orders (id),
    CONSTRAINT ck_payments_amount CHECK (amount > 0),
    CONSTRAINT ck_payments_status CHECK (status IN ('CREATED', 'AUTHORIZED', 'CAPTURED', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED'))
);

CREATE TABLE payment_refunds (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    provider_refund_id VARCHAR(128) UNIQUE,
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(24) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_payment_refunds_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
    CONSTRAINT ck_payment_refunds_amount CHECK (amount > 0),
    CONSTRAINT ck_payment_refunds_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_payments_order_status ON payments (order_id, status);
