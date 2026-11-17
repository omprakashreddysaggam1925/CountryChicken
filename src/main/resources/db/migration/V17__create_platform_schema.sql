CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    channel VARCHAR(16) NOT NULL,
    notification_type VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    status VARCHAR(16) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_notifications_channel CHECK (channel IN ('EMAIL', 'SMS')),
    CONSTRAINT ck_notifications_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE TABLE product_reviews (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uq_reviews_product_user UNIQUE (product_id, user_id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT ck_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE wishlists (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_wishlists_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uq_wishlists_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_notifications_user_status ON notifications (user_id, status, created_at DESC);
CREATE INDEX idx_reviews_product_status ON product_reviews (product_id, status);
CREATE INDEX idx_wishlists_user_created ON wishlists (user_id, created_at DESC);
