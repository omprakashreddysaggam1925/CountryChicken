CREATE TABLE referral_codes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    code VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_referral_codes_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE referrals (
    id UUID PRIMARY KEY,
    referral_code_id UUID NOT NULL,
    referrer_user_id UUID NOT NULL,
    referred_user_id UUID NOT NULL UNIQUE,
    status VARCHAR(24) NOT NULL,
    qualifying_order_id UUID UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_referrals_code FOREIGN KEY (referral_code_id) REFERENCES referral_codes (id),
    CONSTRAINT fk_referrals_referrer FOREIGN KEY (referrer_user_id) REFERENCES users (id),
    CONSTRAINT fk_referrals_referred FOREIGN KEY (referred_user_id) REFERENCES users (id),
    CONSTRAINT fk_referrals_order FOREIGN KEY (qualifying_order_id) REFERENCES customer_orders (id),
    CONSTRAINT ck_referrals_status CHECK (status IN ('PENDING', 'QUALIFIED', 'REVERSED')),
    CONSTRAINT ck_referrals_not_self CHECK (referrer_user_id <> referred_user_id)
);

CREATE TABLE reward_accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance_points BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reward_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_reward_accounts_balance CHECK (balance_points >= 0)
);

CREATE TABLE reward_transactions (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    transaction_type VARCHAR(32) NOT NULL,
    points BIGINT NOT NULL,
    reference_key VARCHAR(128) NOT NULL UNIQUE,
    order_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reward_transactions_account FOREIGN KEY (account_id) REFERENCES reward_accounts (id),
    CONSTRAINT fk_reward_transactions_order FOREIGN KEY (order_id) REFERENCES customer_orders (id),
    CONSTRAINT ck_reward_transactions_points CHECK (points <> 0),
    CONSTRAINT ck_reward_transactions_type CHECK (transaction_type IN ('ORDER_EARNED', 'REFERRAL_BONUS', 'REWARD_REDEMPTION', 'REFUND_REVERSAL'))
);

CREATE INDEX idx_referrals_referrer_status ON referrals (referrer_user_id, status);
CREATE INDEX idx_reward_transactions_account_created ON reward_transactions (account_id, created_at DESC);
