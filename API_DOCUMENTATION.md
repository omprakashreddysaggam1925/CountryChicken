# Country Chicken API Contract

## General conventions

All endpoints are versioned under `/api/v1`. Requests and responses use JSON. Protected endpoints require a bearer access token. Administrative endpoints require the corresponding database-driven permission.

## Response envelope

```json
{
  "success": true,
  "data": {},
  "message": "Operation completed",
  "errors": [],
  "timestamp": "2026-11-17T00:00:00Z",
  "path": "/api/v1/products"
}
```

Validation errors return `success: false` with field-level error details. Internal exception details and secrets are never returned.

## Catalog APIs

- `GET /api/v1/categories`
- `GET /api/v1/products?search=...&page=0&size=20`
- `GET /api/v1/products/{id}`
- `GET/POST /api/v1/admin/categories`
- `PUT/DELETE /api/v1/admin/categories/{id}`
- `POST/PUT/DELETE /api/v1/admin/products/{id}`
- `POST /api/v1/admin/products/{id}/variants`

Public product reads expose only active products whose categories are active.
Administrative endpoints require the corresponding database-driven
`CATEGORY_*` or `PRODUCT_*` permission.

## Customer commerce APIs

All address and cart endpoints require an authenticated customer token. The
user identity comes from the token subject; client-supplied user IDs are not
accepted.

- `GET/POST /api/v1/account/addresses`
- `PUT/DELETE /api/v1/account/addresses/{id}`
- `POST /api/v1/account/addresses/{id}/default`
- `GET /api/v1/cart`
- `POST /api/v1/cart/items`
- `PUT/DELETE /api/v1/cart/items/{variantId}`

## Order APIs

- `POST /api/v1/orders/checkout`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{orderId}`
- `POST /api/v1/orders/{orderId}/cancel`

Checkout snapshots the product name, SKU, price, quantity, delivery address,
and stock reservation. It validates delivery coverage and calculates the
shipping fee server-side. It creates a `PENDING_PAYMENT` order and clears the
cart only after all requested lines have been successfully reserved. A
Checkout accepts an optional `couponCode`; eligible percentage or fixed
discounts are validated against the server-side merchandise subtotal and
recorded as an immutable order discount. A

Registration accepts an optional `referralCode`. Customers can view their
code and reward balance with `GET /api/v1/account/loyalty/referral` and
`GET /api/v1/account/loyalty/rewards`. The first paid order qualifies a
referral and awards fixed bonuses to both customers; full refunds create
compensating reward ledger entries. A
customer can only retrieve or cancel their own pending orders.

## Payment APIs

- `POST /api/v1/payments` — creates a Razorpay order; requires an
  `idempotencyKey` and an order owned by the authenticated customer.
- `POST /api/v1/payments/{paymentId}/refund` — creates a full or partial
  refund for a captured payment.
- `POST /api/v1/payments/webhooks/razorpay` — accepts only requests with a
  valid `X-Razorpay-Signature`.

Payment creation never accepts a client amount. The amount is read from the
server-side merchandise subtotal plus shipping fee. Successful capture commits reserved stock; failed
payments release reservations. Payment and refund provider IDs are persisted,
and repeated idempotency keys return the original payment record.

## Shipping APIs

- `GET /api/v1/shipping/quote?postalCode=...` — validates coverage and returns
  the matching zone fee and estimated delivery time.
- `GET /api/v1/shipping/orders/{orderId}` — returns shipment tracking for the
  authenticated customer's order.
- `GET/POST/PUT /api/v1/admin/shipping/zones` — manages postal-prefix delivery
  zones; requires `SHIPPING_READ`, `SHIPPING_CREATE`, or `SHIPPING_UPDATE`.
- `POST /api/v1/admin/shipping/shipments/{shipmentId}/status` — advances
  shipment status; requires `SHIPPING_UPDATE`.

Verified payment capture creates one shipment per order, selects the most
specific active postal-code prefix, stores the fee and estimated delivery
timestamp, and moves the order to `PROCESSING`. Shipment transitions are
forward-only through `PROCESSING`, `SHIPPED`, `OUT_FOR_DELIVERY`, and
`DELIVERED`.

Cart writes accept variant IDs and quantities only. The server verifies that
the variant and its product/category are active, checks current available
inventory, and calculates response prices from the current catalog price.

## Platform APIs

- `GET /api/v1/account/notifications` — authenticated notification history.
- `GET/POST /api/v1/products/{productId}/reviews` — approved reviews and
  authenticated review submission; new reviews are moderated before publication.
- `GET /api/v1/products/{productId}/reviews/summary` — approved average rating.
- `PUT /api/v1/admin/reviews/{reviewId}/status?status=APPROVED` — moderation,
  requiring `REVIEW_MODERATE`.
- `GET/POST/DELETE /api/v1/account/wishlist/{productId}` — customer wishlist
  management.
- `GET /api/v1/admin/reports/dashboard` — operational counts, requiring
  `REPORT_READ`.

Notifications are persisted before delivery through a provider abstraction.
The default provider logs delivery, allowing email/SMS adapters to be added
without changing notification workflows.

## Inventory APIs

Inventory administration requires `INVENTORY_READ` for reads and
`INVENTORY_UPDATE` for state changes:

- `GET /api/v1/admin/inventory/{variantId}`
- `GET /api/v1/admin/inventory/low-stock`
- `GET /api/v1/admin/inventory/{variantId}/movements`
- `POST /api/v1/admin/inventory/{variantId}/adjustments`
- `POST /api/v1/admin/inventory/{variantId}/reservations`
- `POST /api/v1/admin/inventory/reservations/{reservationId}/release`
- `POST /api/v1/admin/inventory/reservations/{reservationId}/commit`

Reservation operations lock the variant inventory row and record compensating
movement entries for expiry, release, and commit.

## Initial endpoint groups

| Group | Representative endpoints |
|---|---|
| Authentication | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout` |
| Customer account | `GET /users/me`, `PATCH /users/me`, `GET/POST/PUT/DELETE /account/addresses` |
| RBAC administration | `GET/POST/PATCH/DELETE /roles`, `GET/POST/PATCH/DELETE /permissions`, role assignment endpoints |
| Catalog | `GET /products`, `GET /products/{id}`, admin product/category CRUD |
| Inventory | Admin adjustment, reservation, movement, and low-stock endpoints |
| Cart | `GET /cart`, `POST /cart/items`, `PUT /cart/items/{variantId}`, `DELETE /cart/items/{variantId}` |
| Orders | `POST /orders/checkout`, `GET /orders`, `GET /orders/{id}`, cancellation endpoints |
| Payments | Razorpay initiation, status, refunds, and signed webhook endpoints |
| Shipping | Delivery zones, fees, shipment tracking, and status endpoints |
| Promotions | Campaign, discount rule, coupon, and redemption administration |
| Loyalty | Referral code, referral status, reward balance, ledger, and redemption endpoints |
| Operations | Notifications, reports, audit search, and platform configuration |

## Pagination, filtering, and sorting

Collection endpoints support `page`, `size`, `sort`, and documented filters. The server validates allowed sort fields and caps page size.

## Idempotency

Checkout, payment initiation, refund, and webhook processing accept or derive an idempotency key. Repeated requests return the original result without creating duplicate financial, order, inventory, or reward records.

## Implemented endpoints

### `POST /api/v1/auth/register`

Creates a customer account in `PENDING_VERIFICATION` status. The server normalizes the email, hashes the password, validates uniqueness, and never returns the password or password hash.

### `POST /api/v1/auth/verify-email/{userId}`

Accepts the one-time verification token sent to the registered email address. A valid, unexpired token marks the email as verified and activates the account.

### `POST /api/v1/auth/login`

Authenticates an active, verified account and returns a short-lived signed bearer access token plus a one-time-use refresh token. Invalid credentials and inactive accounts return `401 Unauthorized`.

### `GET /api/v1/users/me`

Returns the authenticated user's safe profile projection. The access token subject supplies the user identity; client-supplied user IDs are not accepted.

### `PATCH /api/v1/users/me`

Updates the authenticated user's mobile number and display name fields. Email identity and account status cannot be changed through this endpoint.

### `PATCH /api/v1/admin/users/{userId}/status`

Changes a user's account status. Requires the database-driven `USER_UPDATE` permission. Deactivated users cannot be reactivated.

### Implemented RBAC endpoints

- `GET /api/v1/admin/roles` — requires `ROLE_READ`
- `POST /api/v1/admin/roles` — requires `ROLE_CREATE`
- `PUT /api/v1/admin/roles/{roleId}/permissions` — requires `ROLE_UPDATE`
- `DELETE /api/v1/admin/roles/{roleId}` — requires `ROLE_DELETE`
- `GET /api/v1/admin/permissions` — requires `PERMISSION_READ`
- `POST /api/v1/admin/permissions` — requires `PERMISSION_CREATE`
- `DELETE /api/v1/admin/permissions/{permissionId}` — requires `PERMISSION_DELETE`
- `PUT /api/v1/admin/users/{userId}/roles` — requires `USER_UPDATE`

JWT requests load current permission names from PostgreSQL, and inactive users receive no authorities. Deactivated users cannot be reactivated.

## API documentation lifecycle

OpenAPI annotations and examples are updated with each endpoint task. Breaking changes require a versioning decision and an entry in `TECHNICAL_DECISIONS.md`.
