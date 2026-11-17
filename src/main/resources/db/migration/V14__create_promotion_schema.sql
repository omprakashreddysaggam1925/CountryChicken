CREATE TABLE discount_coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    discount_type VARCHAR(16) NOT NULL,
    discount_value NUMERIC(12, 2) NOT NULL,
    minimum_subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0,
    maximum_discount NUMERIC(12, 2),
    starts_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ends_at TIMESTAMP WITH TIME ZONE NOT NULL,
    usage_limit INTEGER,
    per_customer_limit INTEGER NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_discount_coupons_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED')),
    CONSTRAINT ck_discount_coupons_value CHECK (discount_value > 0),
    CONSTRAINT ck_discount_coupons_minimum CHECK (minimum_subtotal >= 0),
    CONSTRAINT ck_discount_coupons_maximum CHECK (maximum_discount IS NULL OR maximum_discount > 0),
    CONSTRAINT ck_discount_coupons_window CHECK (ends_at > starts_at),
    CONSTRAINT ck_discount_coupons_limits CHECK ((usage_limit IS NULL OR usage_limit > 0) AND per_customer_limit > 0)
);

CREATE TABLE coupon_redemptions (
    id UUID PRIMARY KEY,
    coupon_id UUID NOT NULL,
    user_id UUID NOT NULL,
    order_id UUID NOT NULL UNIQUE,
    discount_amount NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_coupon_redemptions_coupon FOREIGN KEY (coupon_id) REFERENCES discount_coupons (id),
    CONSTRAINT fk_coupon_redemptions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_coupon_redemptions_order FOREIGN KEY (order_id) REFERENCES customer_orders (id),
    CONSTRAINT ck_coupon_redemptions_amount CHECK (discount_amount > 0),
    CONSTRAINT uk_coupon_redemptions_coupon_user_order UNIQUE (coupon_id, user_id, order_id)
);

CREATE INDEX idx_discount_coupons_active_window ON discount_coupons (code, active, starts_at, ends_at);
CREATE INDEX idx_coupon_redemptions_coupon_user ON coupon_redemptions (coupon_id, user_id);
