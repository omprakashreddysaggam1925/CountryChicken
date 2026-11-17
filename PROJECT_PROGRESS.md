# Country Chicken Project Progress

## Overall completion

**82%** — PostgreSQL-backed persistence, authentication, security hardening, dynamic RBAC, product catalog, inventory reservations, customer commerce, order checkout, Razorpay payments, shipping, discounts, coupons, referrals, rewards, notifications, reviews, wishlist, reporting, and production hardening baseline are implemented.

## Status definitions

- `NOT_STARTED` — No implementation has begun.
- `IN_PROGRESS` — Actively being implemented.
- `COMPLETED` — Implemented, tested, reviewed, and documented.
- `BLOCKED` — Cannot proceed; the reason is recorded.
- `NEEDS_REVIEW` — Implementation exists but requires review or additional verification.

## Milestones

| Phase | Milestone | Status |
|---|---|---|
| 0 | Documentation and governance | `COMPLETED` |
| 1 | Platform foundation | `COMPLETED` |
| 2 | Identity and security | `COMPLETED` |
| 3 | Dynamic RBAC | `COMPLETED` |
| 4 | Product catalog | `COMPLETED` |
| 5 | Inventory | `COMPLETED` |
| 6 | Customer commerce | `COMPLETED` |
| 7 | Orders | `COMPLETED` |
| 8 | Payments | `COMPLETED` |
| 9 | Shipping | `COMPLETED` |
| 10 | Discounts and coupons | `COMPLETED` |
| 11 | Referrals and rewards | `COMPLETED` |
| 12 | Notifications | `COMPLETED` |
| 13 | Reviews and wishlist | `COMPLETED` |
| 14 | Reporting and administration | `COMPLETED` |
| 15 | Production hardening | `COMPLETED` |

## Task tracker

| Task ID | Task | Status | Dependencies | Verification |
|---|---|---|---|---|
| `FOUNDATION-001` | Create project documentation and governance | `COMPLETED` | None | Required documents created and reviewed |
| `FOUNDATION-002` | Add dependency management and environment profiles | `COMPLETED` | `FOUNDATION-001` | Full Maven suite passed with PostgreSQL Testcontainers |
| `FOUNDATION-003` | Add database migrations and persistence foundation | `COMPLETED` | `FOUNDATION-002` | `mvnw.cmd test`; migration reviewed |
| `FOUNDATION-004` | Add common API, validation, exception, and audit foundations | `COMPLETED` | `FOUNDATION-002` | Unit test and compilation passed |
| `SECURITY-001` | Implement user identity and account lifecycle | `COMPLETED` | `FOUNDATION-003`, `FOUNDATION-004` | Full Maven suite passed with PostgreSQL Testcontainers |
| `RBAC-001` | Implement database-driven roles and permissions | `COMPLETED` | `SECURITY-001` | Full Maven suite passed with PostgreSQL Testcontainers; RBAC mappings and authority loading verified at context startup |
| `CATALOG-001` | Implement categories, products, and variants | `COMPLETED` | `FOUNDATION-003`, `RBAC-001` | PostgreSQL migration validation and full Maven suite |
| `INVENTORY-001` | Implement stock ledger and reservations | `COMPLETED` | `CATALOG-001` | PostgreSQL migration validation, inventory unit tests, and full Maven suite |
| `COMMERCE-001` | Implement addresses and shopping cart | `COMPLETED` | `SECURITY-001`, `CATALOG-001`, `INVENTORY-001` | Flyway migration, compile, cart unit test, and full Maven suite |
| `ORDER-001` | Implement checkout and order lifecycle | `COMPLETED` | `COMMERCE-001` | Flyway migration, compile, order unit tests, and full Maven suite |
| `PAYMENT-001` | Implement payment abstraction, webhooks, and refunds | `COMPLETED` | `ORDER-001` | Flyway migration, compile, webhook signature/idempotency tests, and full Maven suite |
| `SHIPPING-001` | Implement delivery zones and shipment tracking | `COMPLETED` | `ORDER-001`, `PAYMENT-001` | Flyway migration, shipping lifecycle tests, and full Maven suite |
| `PROMOTION-001` | Implement discounts and coupons | `COMPLETED` | `CATALOG-001`, `COMMERCE-001`, `ORDER-001`, `SHIPPING-001` | Flyway migrations, coupon pricing tests, and full Maven suite |
| `LOYALTY-001` | Implement referrals and reward ledger | `COMPLETED` | `SECURITY-001`, `ORDER-001`, `PROMOTION-001` | Flyway migration, reward account/ledger tests, and full Maven suite |
| `PLATFORM-001` | Implement notifications, reviews, wishlist, and reporting | `COMPLETED` | Relevant preceding modules | PostgreSQL migration validation, compilation, context-load verification |
| `HARDENING-001` | Production hardening and deployment readiness | `COMPLETED` | All implementation tasks | Full Maven suite, CI workflow, production operations runbook |

## Completed tasks

- `FOUNDATION-001` — Created `PROJECT_PROGRESS.md`, `PROJECT_ROADMAP.md`, `ARCHITECTURE.md`, `DATABASE_DESIGN.md`, `API_DOCUMENTATION.md`, `BUSINESS_RULES.md`, and `TECHNICAL_DECISIONS.md`.
- `FOUNDATION-002` — Added JPA, validation, security, actuator, Flyway, PostgreSQL, Testcontainers, and PostgreSQL test profiles; added Docker Compose support.
- `FOUNDATION-003` — Added the initial identity/RBAC/audit Flyway migration and reusable UUID/version/timestamp JPA base entity.
- `FOUNDATION-004` — Added the standard API response/error envelope, centralized exception handling, domain exceptions, and the audit event contract.
- `SECURITY-001` — Implemented registration, email verification, account activation, JWT login, refresh-token rotation, logout, profile management, and protected account status administration.
- `CATALOG-001` — Added normalized categories, products, product variants, validation, public active-product reads, and permission-protected administration APIs.
- `INVENTORY-001` — Added per-variant stock balances, append-only movement history, pessimistically locked reservations, expiry/release/commit transitions, low-stock reads, and protected inventory APIs.
- `COMMERCE-001` — Added customer-owned delivery addresses, persistent carts, current-price recalculation, active-catalog validation, and available-inventory checks.
- `ORDER-001` — Added transactional checkout, immutable order/item snapshots, inventory reservations, customer-scoped order reads, and pending-order cancellation.
- `PAYMENT-001` — Added provider-neutral payment records, Razorpay order/refund calls, idempotency enforcement, signed webhooks, and payment-driven reservation transitions.
- `SHIPPING-001` — Added postal-prefix delivery zones, shipping quotes, automatic post-payment shipment creation, tracking, estimated delivery, and protected status transitions.
- `PROMOTION-001` — Added fixed and percentage coupons, eligibility windows, minimums, caps, usage limits, redemption ledgers, and checkout/payment price integration.
- `LOYALTY-001` — Added referral codes, qualifying referral records, reward accounts, idempotent order/referral points, and compensating refund entries.
- `PLATFORM-001` — Added durable notification records and provider abstraction, moderated product reviews with rating summaries, customer wishlists, and admin dashboard metrics.
- `HARDENING-001` — Removed the local database-password fallback, hardened HTTP headers and health probes, added least-privilege CI, and documented production configuration, backups, deployment, and rollback operations.

## Blocked tasks

- No blocked tasks.

## Important implementation notes

- The repository is a Java 21, Spring Boot 4.1.1 Maven application.
- Inventory is isolated from cart and order workflows; those modules will consume the reservation contract in later phases.
- The backend will use a modular monolith and feature-based package ownership.
- Business roles and permissions must remain database-driven.
- Critical commerce operations must be transactional and idempotent.

## Known bugs and technical debt

- No known runtime bugs at the bootstrap stage.
- The generated starter `HELP.md` can be replaced with project-specific documentation after the foundation phase.
- Production dependencies, database configuration, and security configuration are not yet present.
- The local profile expects PostgreSQL at `localhost:5432` unless overridden with environment variables.
- The PostgreSQL migration is enabled for local, test, and production profiles. Automated tests require a working Docker runtime for the PostgreSQL Testcontainers instance.
- Docker Desktop on Windows requires `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine` and the test API compatibility configuration in `src/test/resources/docker-java.properties`.
- Refresh-token and verification-token tables were aligned with the shared JPA audit/version columns through Flyway migrations V2-V4.
- Security review completed: JWT secrets are mandatory and key strength is validated; refresh-token lookup uses the indexed digest.
- `RBAC-001` — Added dynamic role/permission administration, user role assignment, PostgreSQL join-table compatibility, and request-time database authority loading.
- Inventory stock rows are locked pessimistically during adjustments and reservation transitions.

## Latest completion report

| Field | Description |
|---|---|
| Task ID | `HARDENING-001` |
| Task | Production hardening and deployment readiness |
| Files | Security/configuration hardening, CI workflow, and `OPERATIONS.md` |
| Tests | Full Maven suite with PostgreSQL Testcontainers |
| Status | `COMPLETED` |
| Remaining work | Operational rate limiting and external notification providers require deployment-specific integration |
| Next task | Roadmap complete; continue with deployment-specific integrations |

## Update policy

This file is updated after every task verification. A task may only be marked `COMPLETED` after relevant tests pass and directly related documentation is updated.
