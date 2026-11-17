# Country Chicken Business Rules

## Catalog and stock

- Category and product slugs are normalized and unique.
- Variant SKUs are normalized and globally unique.
- Variant weight must be positive and price cannot be negative.
- Catalog status does not represent stock quantity; inventory owns stock truth.
- Available stock equals on-hand stock minus active reservations.
- Stock adjustments cannot reduce on-hand stock below reserved stock.
- Reservation, release, expiry, and commit transitions are transactional and
  append movement records rather than deleting history.
- Concurrent reservation attempts serialize on the inventory row and cannot
  reserve more than available stock.

- Only active products and active variants can be purchased.
- Orders cannot exceed available or reservable stock.
- Product prices and availability are recalculated server-side.
- Stock reservations expire and must be released.
- Successful fulfillment commits stock; cancellation, failed payment, or eligible return releases or restores stock according to policy.

## Orders and payments

- Checkout creates an idempotent order attempt.
- Order totals are calculated from server-owned prices, promotions, shipping, tax, and rewards.
- Order items preserve the purchased pricing snapshot.
- Payment state transitions are validated and cannot move backward arbitrarily.
- Webhooks must be authenticated, deduplicated, and safe to retry.
- Refunds require an auditable payment transaction and cannot exceed the captured amount.

## Shipping

- Delivery coverage is selected from the most specific active postal-code prefix.
- Shipping fees and estimated delivery time are calculated server-side.
- A paid order receives at most one shipment and a unique tracking number.
- Shipment statuses move forward only; delivered shipments cannot be changed.
- Customers can view shipment details only for their own orders.

## Discounts and coupons

- Discounts may be percentage-based or fixed amount.
- Rules can target products, categories, carts, or customer eligibility.
- Minimum purchase, date range, per-customer usage, overall usage, and maximum discount constraints are enforced server-side.
- Exclusive campaigns cannot be combined with incompatible discounts.
- Coupon redemption is recorded atomically with order completion.
- Expired, inactive, exhausted, or unauthorized coupons are rejected.
- Coupon discounts are capped at the merchandise subtotal and optionally at a configured maximum.
- Coupon redemption is limited globally and per customer, then recorded in an immutable redemption ledger.

## Referrals

- Each customer has a unique referral code.
- Self-referrals, duplicate referrals, and repeated qualifying rewards are rejected.
- Referral rewards are issued only after the configured qualifying event.
- Reversals create compensating referral/reward records.

## Rewards

- Points are recorded in an append-only ledger.
- Customers cannot redeem more than their available balance.
- Administrators configure earning rates, minimum redemption, maximum order percentage, and expiration.
- Points are earned only for eligible completed orders.
- Cancellations and refunds reverse eligible earned or redeemed points.
- A referral qualifies only after the referred customer's first paid order.
- Referral and order points are issued idempotently and full refunds create negative compensating entries.

## Security and administration

- Every protected operation requires authentication and the required permission.
- Administrative changes to users, roles, permissions, prices, inventory, discounts, rewards, payments, and configuration are audited.
- Sensitive credentials and tokens are never logged or returned.
- Rate limits apply to authentication, verification, coupon abuse, and payment-sensitive operations.

## Platform features

- A customer can submit at most one review per product.
- New reviews start as `PENDING`; only approved reviews affect public listings
  and rating averages.
- Wishlist membership is unique per customer/product and can only be changed by
  the owning customer.
- Notifications are durable records with channel and delivery status; provider
  failures must not erase the notification history.
