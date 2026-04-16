-- =============================================================================
-- STEP 1: VERIFY CURRENT STATE & BACKUP COUNTS
-- Sales Management System — Table Consolidation Migration
-- Run this BEFORE any changes. Confirm record counts and relationships.
-- =============================================================================

-- ── 1. Count records in each table ───────────────────────────────────────────
SELECT 'enquiry_request' AS table_name, COUNT(*) AS record_count FROM enquiry_request
UNION ALL
SELECT 'sales_followup',               COUNT(*)                  FROM sales_followup
UNION ALL
SELECT 'admission_request',            COUNT(*)                  FROM admission_request;

-- ── 2. Show the columns that currently exist on enquiry_request ───────────────
SELECT column_name, data_type, character_maximum_length
FROM   information_schema.columns
WHERE  table_schema = 'public'
  AND  table_name   = 'enquiry_request'
ORDER  BY ordinal_position;

-- ── 3. Show the columns on sales_followup ────────────────────────────────────
SELECT column_name, data_type, character_maximum_length
FROM   information_schema.columns
WHERE  table_schema = 'public'
  AND  table_name   = 'sales_followup'
ORDER  BY ordinal_position;

-- ── 4. Orphan check — followups with no matching enquiry ─────────────────────
SELECT sf.followup_id, sf.enquiry_id
FROM   sales_followup sf
LEFT   JOIN enquiry_request er ON er.id = sf.enquiry_id
WHERE  er.id IS NULL;

-- ── 5. Enquiries with multiple followup rows (1:N check) ─────────────────────
SELECT enquiry_id, COUNT(*) AS followup_count
FROM   sales_followup
GROUP  BY enquiry_id
HAVING COUNT(*) > 1
ORDER  BY followup_count DESC;

-- ─────────────────────────────────────────────────────────────────────────────
-- If query 5 returns rows: those enquiries have multiple followups.
-- The migration keeps only the LATEST followup (highest followup_id).
-- Older rows are preserved in the sales_followup table which is NOT dropped
-- automatically — only after manual DBA sign-off (see step 3).
-- ─────────────────────────────────────────────────────────────────────────────

-- ── 6. Full backup snapshot (copy tables to backup relations) ─────────────────
CREATE TABLE IF NOT EXISTS _bkp_enquiry_request   AS SELECT * FROM enquiry_request   WITH NO DATA;
INSERT INTO _bkp_enquiry_request   SELECT * FROM enquiry_request;

CREATE TABLE IF NOT EXISTS _bkp_sales_followup    AS SELECT * FROM sales_followup    WITH NO DATA;
INSERT INTO _bkp_sales_followup    SELECT * FROM sales_followup;

CREATE TABLE IF NOT EXISTS _bkp_admission_request AS SELECT * FROM admission_request WITH NO DATA;
INSERT INTO _bkp_admission_request SELECT * FROM admission_request;

-- Verify backup counts match originals
SELECT 'enquiry_request (backup)'   AS check_table, COUNT(*) FROM _bkp_enquiry_request
UNION ALL
SELECT 'sales_followup (backup)',    COUNT(*) FROM _bkp_sales_followup
UNION ALL
SELECT 'admission_request (backup)', COUNT(*) FROM _bkp_admission_request;
