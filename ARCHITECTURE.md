# Country Chicken Architecture

## Architectural style

Country Chicken uses a modular monolith with feature-based package ownership. This provides transactional consistency and simple deployment while keeping module boundaries clear enough for future extraction if scale requires it.

## Package structure

```text
com.countrychicken
├── config
├── common
│   ├── audit
│   ├── constants
│   ├── enums
│   ├── exception
│   ├── response
│   └── utils
├── security
├── user
├── role
├── permission
├── product
├── category
├── inventory
├── cart
├── order
├── payment
├── shipping
├── referral
├── rewards
├── discounts
├── coupon
├── notification
├── review
├── wishlist
├── reporting
└── audit
```

Each feature owns its controller, service, repository, entity, DTO, mapper, validator, and module-specific exception classes as needed. Controllers accept and return DTOs; entities do not cross the API boundary.

## Layer responsibilities

- **Controller:** HTTP mapping, authentication context access, validation handoff, and response mapping.
- **Service:** Business workflows, transaction boundaries, authorization checks, and coordination with other modules.
- **Repository:** Persistence queries and aggregate access only.
- **Entity:** Persistence state and invariants that belong to the aggregate.
- **DTO:** External request and response contracts.
- **Mapper:** Explicit conversion between persistence and API models.
- **Validator:** Reusable input and business precondition validation.

## Module boundaries

Modules must not access another module's repositories directly. Cross-module operations use a public service contract or domain event. Shared code belongs in `common` only when it is genuinely cross-cutting and does not contain business ownership.

## Security boundaries

Customer and administrator APIs are separated by URL and permission requirements. Authentication identifies the user; database-driven authorities determine access. No business role may be encoded as the only source of authorization in Java code.

## Consistency model

Inventory, order, payment, and reward state transitions use database transactions, explicit state machines, optimistic locking where appropriate, and idempotency keys for retried external operations. External notifications and provider calls should be triggered through durable events or an outbox mechanism.

## API conventions

- Base path: `/api/v1`
- JSON request and response bodies
- DTO-based contracts
- Jakarta Bean Validation
- Consistent success/error response envelope
- Cursor or page-based pagination as appropriate
- ISO-8601 timestamps in UTC
- `BigDecimal` for monetary values

## Operational architecture

Local development uses Docker Compose for PostgreSQL and optional Redis. Deployments should support stateless application instances, managed PostgreSQL, object storage for media, centralized logs, metrics, health checks, and externalized secrets.
