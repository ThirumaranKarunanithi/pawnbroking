-- =============================================================================
-- POST-MIGRATION VERIFICATION QUERIES
-- Run after Step 2 + new deployment to confirm everything is healthy.
-- =============================================================================

-- ── 1. Table list (should show enquiry_request + admission_request) ───────────
SELECT table_name
FROM   information_schema.tables
WHERE  table_schema = 'public'
  AND  table_name   IN ('enquiry_request', 'sales_followup', 'admission_request')
ORDER  BY table_name;

-- ── 2. New column check ───────────────────────────────────────────────────────
SELECT column_name, data_type
FROM   information_schema.columns
WHERE  table_schema = 'public'
  AND  table_name   = 'enquiry_request'
  AND  column_name  IN (
          'today_followup_notes', 'next_followup_date', 'next_followup_timing',
          'last_followup_date',   'last_followup_timing','last_followup_notes',
          'followup_status')
ORDER  BY column_name;
-- Expected: 7 rows

-- ── 3. Record counts ──────────────────────────────────────────────────────────
SELECT COUNT(*) AS total_enquiries         FROM enquiry_request;
SELECT COUNT(*) AS enquiries_with_followup
FROM   enquiry_request
WHERE  next_followup_date IS NOT NULL
   OR  today_followup_notes IS NOT NULL;

-- ── 4. Sample — view merged data ─────────────────────────────────────────────
SELECT
    id,
    full_name,
    followup_status,
    today_followup_notes,
    next_followup_date,
    next_followup_timing,
    last_followup_date,
    last_followup_notes
FROM enquiry_request
WHERE today_followup_notes IS NOT NULL
LIMIT 10;

-- ── 5. Cross-check: rows in backup vs current ────────────────────────────────
SELECT
    (SELECT COUNT(*) FROM _bkp_enquiry_request) AS original_count,
    (SELECT COUNT(*) FROM enquiry_request)       AS current_count;
-- Both numbers must be equal.

-- ── 6. Indexes check ─────────────────────────────────────────────────────────
SELECT indexname, indexdef
FROM   pg_indexes
WHERE  tablename = 'enquiry_request'
ORDER  BY indexname;
