-- =============================================================================
-- STEP 2: MIGRATE DATA — Merge sales_followup → enquiry_request
-- Sales Management System — Table Consolidation Migration
--
-- IMPORTANT: Run inside a single transaction so it can be rolled back cleanly.
-- Execute migration_step1_verify_and_backup.sql and confirm counts FIRST.
-- =============================================================================

BEGIN;

-- ── A. Add new followup columns to enquiry_request ───────────────────────────
-- NOTE: Hibernate (ddl-auto=update) will also add these columns on the NEXT
-- application startup. Running this script BEFORE deployment ensures the
-- data migration happens before the application reads these columns.

ALTER TABLE enquiry_request
    ADD COLUMN IF NOT EXISTS today_followup_notes TEXT,
    ADD COLUMN IF NOT EXISTS next_followup_date   VARCHAR(50),
    ADD COLUMN IF NOT EXISTS next_followup_timing VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_followup_date   VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_followup_timing VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_followup_notes  TEXT;

-- ── B. Copy followup data — one row per enquiry (latest followup wins) ────────
-- Uses a subquery to select the highest followup_id per enquiry_id, ensuring
-- that if an enquiry had multiple followup rows only the most recent is kept.

WITH latest_followup AS (
    SELECT DISTINCT ON (enquiry_id)
           enquiry_id,
           today_followup_notes,
           next_followup_date,
           next_followup_timing,
           followup_status,
           last_followup_date,
           last_followup_timing,
           last_followup_notes
    FROM   sales_followup
    ORDER  BY enquiry_id, followup_id DESC   -- highest followup_id = latest
)
UPDATE enquiry_request er
SET
    today_followup_notes = lf.today_followup_notes,
    next_followup_date   = lf.next_followup_date,
    next_followup_timing = lf.next_followup_timing,
    followup_status      = COALESCE(lf.followup_status, er.followup_status),
    last_followup_date   = lf.last_followup_date,
    last_followup_timing = lf.last_followup_timing,
    last_followup_notes  = lf.last_followup_notes
FROM latest_followup lf
WHERE er.id = lf.enquiry_id;

-- ── C. Verification — counts must match ──────────────────────────────────────
-- How many enquiries now have followup data?
SELECT COUNT(*) AS enquiries_with_followup_data
FROM   enquiry_request
WHERE  today_followup_notes IS NOT NULL
   OR  next_followup_date   IS NOT NULL
   OR  followup_status      IS NOT NULL;

-- How many distinct enquiry_ids existed in sales_followup?
SELECT COUNT(DISTINCT enquiry_id) AS distinct_enquiry_ids_in_followup
FROM   sales_followup;

-- ── D. Add performance indexes ────────────────────────────────────────────────
-- (Hibernate will also add idx_enq_next_followup_date and
--  idx_enq_followup_status on startup — these are extra safety indexes.)

CREATE INDEX IF NOT EXISTS idx_enq_next_followup_date
    ON enquiry_request (next_followup_date);

CREATE INDEX IF NOT EXISTS idx_enq_followup_status
    ON enquiry_request (followup_status);

-- ── E. Commit ─────────────────────────────────────────────────────────────────
COMMIT;

-- ─────────────────────────────────────────────────────────────────────────────
-- AFTER COMMITTING:
--   1. Deploy the new saleswebservice JAR (contains updated ENQEntity).
--   2. Hibernate ddl-auto=update will NOT add duplicate columns — safe to run.
--   3. Test all endpoints (see testing_checklist.md).
--   4. When confident, run migration_step3_cleanup.sql to drop sales_followup.
-- ─────────────────────────────────────────────────────────────────────────────
