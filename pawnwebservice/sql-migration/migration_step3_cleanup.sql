-- =============================================================================
-- STEP 3: CLEANUP — Drop sales_followup table
-- Sales Management System — Table Consolidation Migration
--
-- Run this ONLY after:
--   ✅  Step 2 data migration completed successfully
--   ✅  New saleswebservice JAR deployed and running
--   ✅  All API endpoints tested and working
--   ✅  Desktop application tested and working
--   ✅  Backup tables _bkp_* confirmed good
--
-- NOTE: The _bkp_sales_followup table is kept as a safety net until
--       you are fully confident.  Drop it manually when ready.
-- =============================================================================

BEGIN;

-- Final count check before dropping
SELECT COUNT(*) AS remaining_rows_in_sales_followup FROM sales_followup;

-- Drop the now-redundant table
DROP TABLE IF EXISTS sales_followup CASCADE;

-- Verify the table is gone
SELECT table_name
FROM   information_schema.tables
WHERE  table_schema = 'public'
  AND  table_name   = 'sales_followup';
-- (should return 0 rows)

COMMIT;

-- ── Optional: drop backup tables once 100% confident ─────────────────────────
-- Uncomment when you are absolutely sure the migration is stable.

-- DROP TABLE IF EXISTS _bkp_enquiry_request;
-- DROP TABLE IF EXISTS _bkp_sales_followup;
-- DROP TABLE IF EXISTS _bkp_admission_request;
