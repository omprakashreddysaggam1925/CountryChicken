# Country Chicken Development Roadmap

## Objective

Build a secure, modular, production-ready backend for customer and administrator portals supporting catalog management, checkout, payments, inventory, promotions, referrals, rewards, shipping, notifications, and reporting.

## Delivery approach

The project is a modular monolith. Work proceeds in small, dependency-aware tasks. Each task must be implemented, tested, documented, and reflected in `PROJECT_PROGRESS.md` before the next dependent task begins.

## Phases

### Phase 0 — Documentation and governance

Create the project contract, architecture, database model, API conventions, business rules, technical decisions, and progress tracker. This phase prevents inconsistent design decisions during implementation.

### Phase 1 — Platform foundation

Add dependency management, environment profiles, PostgreSQL connectivity, Flyway migrations, common responses, validation, exception handling, logging, audit primitives, OpenAPI, and Docker development support.

### Phase 2 — Identity and security

Implement customer registration, authentication, logout, contact verification, profiles, addresses, account status, password handling, JWT access tokens, rotating refresh tokens, and security filters.

### Phase 3 — Dynamic RBAC

Implement permission, role, user-role, and role-permission entities and administrative APIs. Enforce permissions at the API/service boundary and audit sensitive changes.

### Phase 4 — Product catalog

Implement categories, products, variants, images, prices, availability, product lifecycle, search, filtering, sorting, pagination, and administrative catalog APIs.

### Phase 5 — Inventory

Implement stock ledgers, adjustments, reservations, release/commit operations, optimistic locking, low-stock thresholds, and stock movement history.

### Phase 6 — Customer commerce

Implement delivery addresses, persistent carts, cart items, server-side totals, stock checks, and preliminary promotion evaluation.

### Phase 7 — Orders

Implement checkout, order snapshots, order status history, cancellation, returns, invoices, and customer/admin order APIs.

### Phase 8 — Payments

Implement a payment-provider abstraction, cash on delivery, online payment initiation, signed webhooks, payment status transitions, refunds, and idempotency.

### Phase 9 — Shipping

Implement delivery zones, address validation, fee calculation, shipment creation, tracking, estimated delivery, and delivery status transitions.

### Phase 10 — Discounts and coupons

Implement percentage/fixed discounts, product/category/cart rules, coupon codes, first-order rules, date windows, limits, stacking, exclusivity, and usage ledgers.

### Phase 11 — Referrals and rewards

Implement referral codes, qualifying events, fraud/duplication protection, reward rules, point balances, transaction ledger, redemption limits, expiration, and compensating transactions.

### Phase 12 — Notifications

Implement email and SMS provider abstractions, templates, delivery tracking, retry policy, and event-driven order/payment/shipping/reward notifications.

### Phase 13 — Reviews and wishlist

Implement product reviews, moderation, rating aggregation, wishlist management, and authorization checks.

### Phase 14 — Reporting and administration

Implement dashboard metrics, operational reports, exports, configuration APIs, audit search, and administrator views of business activity.

### Phase 15 — Production hardening

Complete security review, performance and concurrency testing, observability, CI/CD, backups, deployment documentation, migration rehearsal, rate limiting, and operational runbooks.

## Definition of done for every task

1. Requirements and dependencies are understood.
2. Implementation is limited to the task scope.
3. Unit/integration tests cover the behavior.
4. Existing tests and the application build pass.
5. API, database, or business-rule documentation is updated.
6. Progress status, notes, and remaining work are recorded.
