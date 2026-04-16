-- =============================================================================
-- ROLLBACK SCRIPT — Revert enquiry_request to its original structure
-- Sales Management System — Table Consolidation Migration
--
-- Use this if something goes wrong BEFORE Step 3 (cleanup).
-- It removes the new columns and restores data from backup tables.
-- The sales_followup table is NOT affected (it still has all original data).
--
-- Prerequisites:
--   • Backup tables created by Step 1 (_bkp_enquiry_request, etc.) must exist.
--   • Run this BEFORE dropping the sales_followup table (Step 3).
-- =============================================================================

BEGIN;

-- ── 1. Restore enquiry_request from backup ────────────────────────────────────
TRUNCATE TABLE enquiry_request RESTART IDENTITY CASCADE;

INSERT INTO enquiry_request
SELECT * FROM _bkp_enquiry_request;

-- ── 2. Drop the new followup columns (added in Step 2) ───────────────────────
-- These were added by the migration and must be removed on rollback.

ALTER TABLE enquiry_request
    DROP COLUMN IF EXISTS today_followup_notes,
    DROP COLUMN IF EXISTS next_followup_date,
    DROP COLUMN IF EXISTS next_followup_timing,
    DROP COLUMN IF EXISTS last_followup_date,
    DROP COLUMN IF EXISTS last_followup_timing,
    DROP COLUMN IF EXISTS last_followup_notes;

-- ── 3. Verify original column set is back ────────────────────────────────────
SELECT column_name
FROM   information_schema.columns
WHERE  table_schema = 'public'
  AND  table_name   = 'enquiry_request'
ORDER  BY ordinal_position;

-- ── 4. Verify counts match backup ────────────────────────────────────────────
SELECT COUNT(*) AS enquiry_count_after_rollback FROM enquiry_request;
SELECT COUNT(*) AS backup_count                 FROM _bkp_enquiry_request;

COMMIT;

-- ── 5. After rollback: redeploy the ORIGINAL saleswebservice JAR ──────────────
-- git checkout <previous-tag> && mvn clean package
-- then restart the application service.

-- ── 6. Verify sales_followup table is intact ────────────────────────────────
SELECT COUNT(*) AS followup_rows_intact FROM sales_followup;
