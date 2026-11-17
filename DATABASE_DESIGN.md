# Country Chicken Database Design

## Design principles

- PostgreSQL is the system of record.
- Schema changes are applied only through Flyway migrations.
- UUIDs are preferred for externally visible identifiers.
- Monetary values use fixed-precision numeric columns.
- All important state changes have timestamps and, where needed, actor references.
- Business history is append-only where reversal or auditability matters.

## Identity and authorization

```text
users
user_credentials
user_verifications
refresh_tokens
roles
permissions
user_roles
role_permissions
audit_events
```

The initial catalog schema is implemented by `V6__create_catalog_schema.sql`.
It provides normalized `categories`, `products`, and `product_variants` tables,
including category hierarchy, unique slugs/SKUs, fixed-precision prices, status
constraints, and shared audit/version columns. Inventory quantities remain
separate and are owned by the inventory module.

`permissions` are unique resource-operation capabilities. `roles` are configurable records. Many-to-many join tables connect users to roles and roles to permissions. Refresh tokens are stored as hashes and can be revoked independently.

## Catalog and inventory

```text
categories
products
product_variants
product_images
inventory_items
inventory_movements
stock_reservations
```

Products may have multiple sellable variants such as weight. Inventory is tracked per variant. Movement records capture additions, deductions, releases, returns, and administrative adjustments. Reservations have an expiry and status.

`V7__create_inventory_schema.sql` and `V8__add_inventory_movement_audit_columns.sql`
implement these inventory tables. `inventory_items` stores on-hand, reserved, and
low-stock threshold values per variant. `inventory_movements` is append-only, and
`stock_reservations` tracks active, released, committed, and expired holds.

## Commerce and fulfillment

```text
delivery_addresses
shopping_carts
cart_items
orders
order_items
order_status_history
payments
payment_transactions
refunds
shipments
delivery_zones
```

Order items contain immutable snapshots of the purchased product, variant, price, tax, discounts, and quantity. Payment and shipment state histories must not overwrite the financial or operational audit trail.

`V9__create_commerce_schema.sql` implements customer-owned delivery addresses,
one persistent shopping cart per user, and unique cart items per variant.
Address ownership is enforced in repository lookups. Cart prices are stored as
snapshots for change tracking but are recalculated from the active catalog
variant whenever a cart is returned or changed.

`V10__create_order_schema.sql` implements customer orders and immutable order
line snapshots. Checkout links each line to a stock reservation, uses fixed
precision monetary values, and starts orders in `PENDING_PAYMENT` for the
payment module to advance later.

`V11__create_payment_schema.sql` implements provider-neutral payment and
refund records. Razorpay IDs are unique, idempotency keys are unique, and
payment status changes are driven by verified provider webhooks rather than
client input. `V12__create_shipping_schema.sql` implements postal-prefix
delivery zones and one shipment per order, including tracking, fee, estimated
delivery, and forward-only operational status fields.
`V13__add_order_shipping_fee.sql` adds the server-calculated shipping fee to
order totals so payment initiation charges merchandise subtotal plus shipping.
`V14__create_promotion_schema.sql` adds coupon definitions and redemption
ledger records. `V15__add_order_discount.sql` snapshots the applied coupon
code and discount amount on each order.
`V16__create_loyalty_schema.sql` adds referral codes, qualifying referral
records, reward accounts, and an append-only reward transaction ledger.

## Promotions and loyalty

```text
discount_campaigns
discount_rules
coupons
coupon_redemptions
referral_codes
referrals
reward_accounts
reward_rules
reward_transactions
```

Discount usage and reward transactions are ledger records. Balances must not be trusted from client input. Refunds and cancellations create compensating entries rather than deleting historical transactions.

## Required constraints and indexes

- Unique email, mobile number, referral code, coupon code, SKU, and external payment transaction ID.
- Foreign keys for every relationship.
- Check constraints for non-negative quantity, price, and points.
- Index active product/category lookups, order customer/status/date, payment provider references, reservation expiry, and ledger account/date.
- Version columns for concurrent inventory, cart, and aggregate updates.

## Migration sequencing

1. Identity and audit tables (`V1__create_identity_and_audit_schema.sql`).
2. Catalog and inventory tables.
3. Addresses, carts, and orders.
4. Payments and shipping.
5. Discounts and coupons.
6. Referrals and rewards.
7. Reviews, wishlist, notifications, and reporting projections.

Migration V17 adds `notifications`, `product_reviews`, and `wishlists`. Reviews
are unique per customer/product and use a moderation status; notification
records retain channel, delivery status, attempts, and timestamps. Reporting
currently reads transactional aggregates for dashboard metrics.

The initial migration creates users, roles, permissions, their assignment tables, verification tokens, refresh tokens, and audit events. Feature migrations must add tables in dependency order and must not modify applied migrations.
