# Operations Runbook

## Required production configuration

Set `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`,
`JWT_SECRET`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, and
`RAZORPAY_WEBHOOK_SECRET` through the deployment secret manager. `JWT_SECRET`
must be a Base64-encoded key of at least 256 bits. No production credential
has a source-controlled fallback.

## Health and startup

Use `/actuator/health/liveness` for process health and
`/actuator/health/readiness` for traffic readiness. Health details are never
returned to unauthenticated callers. Migrations run at startup and Hibernate
uses `ddl-auto=validate`; a migration failure must stop deployment.

## Database backup and restore

Schedule encrypted PostgreSQL backups outside the application container. A
logical backup can be created with:

```text
pg_dump --format=custom --file=countrychicken.dump "$DB_URL"
```

Restore into a maintenance database with `pg_restore`, then run the
application against the restored database and verify Flyway plus the health
readiness endpoint before switching traffic. Perform restore rehearsals at
least monthly and retain backups according to the hosting policy.

## Deployment checklist

1. Run the CI `verify` job with Docker available for Testcontainers.
2. Apply the production environment and validate secret presence.
3. Deploy one instance and wait for readiness.
4. Confirm Flyway migration status and application error rate.
5. Shift traffic gradually and retain the previous image for rollback.

Never log `.env`, JWT secrets, payment credentials, access tokens, or database
connection strings containing passwords.
