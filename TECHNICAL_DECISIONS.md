# Technical Decisions

## TD-001 — Modular monolith

**Decision:** Start with a modular monolith using feature-based packages.

**Reason:** The platform requires strong transactions across orders, payments, inventory, and rewards. A modular monolith preserves those boundaries without introducing distributed-system complexity prematurely.

## TD-002 — Java 21 and Spring Boot 4.1.1

**Decision:** Retain the repository's existing Java 21 and Spring Boot 4.1.1 baseline.

**Reason:** The current project compiles and its context test passes. Future dependencies must remain compatible with this baseline unless a documented upgrade is required.

## TD-003 — PostgreSQL with Flyway

**Decision:** Use PostgreSQL as the production relational database and Flyway for versioned migrations.

**Reason:** The domain requires relational integrity, transactional workflows, ledger history, constraints, and reliable reporting queries.

The test profile uses a PostgreSQL Testcontainers instance and runs Flyway migrations, ensuring tests use the same database engine and schema lifecycle as local and production environments. Local development uses the PostgreSQL service in `docker-compose.yml`; local and production credentials are supplied through environment variables.

On Windows with Docker Desktop, Testcontainers is configured to use the Docker Desktop Linux engine named pipe from `src/test/resources/testcontainers.properties`, matching the active Docker CLI context.

JWT signing secrets are mandatory environment configuration and are validated as Base64-encoded keys of at least 256 bits at startup. Refresh-token digests are looked up through the indexed database column instead of scanning all stored tokens.

## TD-004 — Database-driven RBAC

**Decision:** Roles and permissions are persisted and administered through APIs.

**Reason:** The requirement is dynamic authorization. Java enums or hardcoded business roles cannot be the source of truth.

## TD-005 — Ledger-based financial and reward history

**Decision:** Use append-only transaction/history records for payments, refunds, inventory movements, and reward points.

**Reason:** Corrections and reversals must remain auditable and reproducible instead of destroying historical facts.

## TD-006 — Server-owned checkout calculation

**Decision:** Recalculate prices, discounts, shipping, tax, stock, and rewards during checkout.

**Reason:** Client-submitted totals are untrusted and must not control financial or inventory outcomes.

## TD-007 — Idempotent critical operations

**Decision:** Checkout, payment operations, refunds, and webhooks use idempotency keys and unique provider references.

**Reason:** Retries are normal in distributed systems and must not duplicate orders, charges, refunds, reservations, or rewards.

Refresh tokens are generated with high entropy and stored as SHA-256 digests rather than BCrypt hashes, because refresh tokens are opaque cryptographic credentials rather than human passwords and may exceed BCrypt's input limit.

## Open decisions

- Select the first online payment provider and webhook signature scheme.
- Confirm tax calculation and invoice requirements.
- Confirm delivery geography, carrier integrations, and serviceability rules.
- Choose email/SMS providers and notification delivery guarantees.
- Define the initial reward and referral configuration values with business stakeholders.
