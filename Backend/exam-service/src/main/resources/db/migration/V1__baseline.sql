-- V1 baseline for identity-service (spec §8 automated migrations).
-- This file is intentionally a no-op marker. The existing schema, originally created by
-- Hibernate ddl-auto=update, is treated as Flyway baseline V0; new schema changes
-- land in V2__, V3__, ... migrations going forward.
-- Idempotent statement so Flyway has something to run.
SELECT 1;
