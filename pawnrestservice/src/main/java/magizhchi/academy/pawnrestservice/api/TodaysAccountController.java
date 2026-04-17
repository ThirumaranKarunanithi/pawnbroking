package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/todays-account")
public class TodaysAccountController {

    private static final Logger log = LoggerFactory.getLogger(TodaysAccountController.class);
    private final JdbcTemplate jdbc;

    public TodaysAccountController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * GET /api/todays-account?companyId=CMP1&date=yyyy-MM-dd
     */
    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam String companyId,
            @RequestParam(required = false) String date) {
        try {
            String targetDate = (date != null && !date.isBlank()) ? date : LocalDate.now().toString();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("companyId", companyId);
            result.put("date", targetDate);

            // ── Previous day & today balance settings ────────────────────────
            addBalanceSettings(result, companyId, targetDate);

            // ── Operations ───────────────────────────────────────────────────
            List<Map<String, Object>> operations = new ArrayList<>();

            operations.add(billOpening(companyId, targetDate, "GOLD"));
            operations.add(billAdvance(companyId, targetDate, "GOLD"));
            operations.add(billClosing(companyId, targetDate, "GOLD"));
            operations.add(billOpening(companyId, targetDate, "SILVER"));
            operations.add(billAdvance(companyId, targetDate, "SILVER"));
            operations.add(billClosing(companyId, targetDate, "SILVER"));
            operations.add(repledgeOpening(companyId, targetDate, "GOLD"));
            operations.add(repledgeClosing(companyId, targetDate, "GOLD"));
            operations.add(expenses(companyId, targetDate));
            operations.add(incomes(companyId, targetDate));

            result.put("operations", operations);

            // ── Grand totals ─────────────────────────────────────────────────
            double totalDebit  = operations.stream().mapToDouble(o -> toDouble(o.get("debit"))).sum();
            double totalCredit = operations.stream().mapToDouble(o -> toDouble(o.get("credit"))).sum();
            result.put("totalDebit",  totalDebit);
            result.put("totalCredit", totalCredit);

            // Actual balance = pre actual + (credit - debit)
            double preActual = toDouble(result.get("preActualBalance"));
            double actualBalance = preActual + (totalCredit - totalDebit);
            result.put("actualBalance", actualBalance);

            double availableBalance = toDouble(result.get("availableBalance"));
            result.put("deficit", availableBalance - actualBalance);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("TodaysAccount error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Balance settings ──────────────────────────────────────────────────────

    private void addBalanceSettings(Map<String, Object> result, String companyId, String date) {
        try {
            String sql = """
                    SELECT pre_date, pre_actual_amount, pre_available_amount, pre_deficit_amount,
                           todays_date, todays_actual_amount, todays_available_amount,
                           todays_deficit_amount, pre_note, todays_note
                    FROM company_todays_account
                    WHERE company_id = ? AND todays_date::text = ?
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, date);
            if (!rows.isEmpty()) {
                Map<String, Object> r = rows.get(0);
                result.put("preDate",             str(r.get("pre_date")));
                result.put("preActualBalance",    toDouble(r.get("pre_actual_amount")));
                result.put("preAvailableBalance", toDouble(r.get("pre_available_amount")));
                result.put("preDeficit",          toDouble(r.get("pre_deficit_amount")));
                result.put("preNote",             str(r.get("pre_note")));
                result.put("availableBalance",    toDouble(r.get("todays_available_amount")));
                result.put("todaysNote",          str(r.get("todays_note")));
            } else {
                // Try the REF_MARK = 'L' fallback
                String sql2 = """
                        SELECT pre_date, pre_actual_amount, pre_available_amount, pre_deficit_amount,
                               todays_date, todays_actual_amount, todays_available_amount,
                               todays_deficit_amount, pre_note, todays_note
                        FROM company_todays_account
                        WHERE company_id = ? AND ref_mark = 'L'
                        """;
                List<Map<String, Object>> rows2 = jdbc.queryForList(sql2, companyId);
                if (!rows2.isEmpty()) {
                    Map<String, Object> r = rows2.get(0);
                    result.put("preDate",             str(r.get("pre_date")));
                    result.put("preActualBalance",    toDouble(r.get("pre_actual_amount")));
                    result.put("preAvailableBalance", toDouble(r.get("pre_available_amount")));
                    result.put("preDeficit",          toDouble(r.get("pre_deficit_amount")));
                    result.put("preNote",             str(r.get("pre_note")));
                    result.put("availableBalance",    toDouble(r.get("todays_available_amount")));
                    result.put("todaysNote",          str(r.get("todays_note")));
                } else {
                    result.put("preDate", ""); result.put("preActualBalance", 0.0);
                    result.put("preAvailableBalance", 0.0); result.put("preDeficit", 0.0);
                    result.put("preNote", ""); result.put("availableBalance", 0.0);
                    result.put("todaysNote", "");
                }
            }
        } catch (Exception e) {
            log.warn("Balance settings not found: {}", e.getMessage());
            result.put("preDate", ""); result.put("preActualBalance", 0.0);
            result.put("preAvailableBalance", 0.0); result.put("preDeficit", 0.0);
            result.put("preNote", ""); result.put("availableBalance", 0.0);
            result.put("todaysNote", "");
        }
    }

    // ── Bill Opening ──────────────────────────────────────────────────────────

    private Map<String, Object> billOpening(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(bill_number) bill_count, COALESCE(SUM(amount),0) debit,
                           COALESCE(SUM(amount - given_amount),0) credit,
                           concat('( Amt: ', COALESCE(sum(amount),0),
                                  ' , Intr: ', COALESCE(sum(open_taken_amount - document_charge),0),
                                  ' , Doc: ', COALESCE(sum(document_charge),0),
                                  ' )( RB: ', COUNT(CASE WHEN rebilled_from IS NOT NULL THEN 1 END),
                                  ', NB: ', COUNT(CASE WHEN rebilled_from IS NULL THEN 1 END), ')') credit_combo
                    FROM company_billing
                    WHERE company_id = ? AND status::text NOT IN ('CANCELED')
                      AND jewel_material_type::text = ? AND opening_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " BILL OPENING", rows);
        } catch (Exception e) {
            return emptyRow(materialType + " BILL OPENING");
        }
    }

    // ── Bill Advance ──────────────────────────────────────────────────────────

    private Map<String, Object> billAdvance(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(bill_number) bill_count, 0 debit, COALESCE(SUM(paid_amount),0) credit,
                           '' credit_combo
                    FROM company_advance_amount
                    WHERE company_id = ? AND jewel_material_type::text = ? AND paid_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " ADVANCE AMOUNT", rows);
        } catch (Exception e) {
            return emptyRow(materialType + " ADVANCE AMOUNT");
        }
    }

    // ── Bill Closing ──────────────────────────────────────────────────────────

    private Map<String, Object> billClosing(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(bill_number) bill_count, 0 debit, COALESCE(SUM(got_amount),0) credit,
                           concat('( Amt: ', COALESCE(sum(amount),0),
                                  ' , Intr: ', COALESCE(sum(close_taken_amount),0),
                                  ' , Fine: ', COALESCE(sum(total_other_charges),0),
                                  ' , Less: ', COALESCE(sum(discount_amount),0),
                                  ' , Adv: ', COALESCE(sum(total_advance_amount_paid),0), ')') credit_combo
                    FROM company_billing
                    WHERE company_id = ? AND jewel_material_type::text = ? AND closing_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " BILL CLOSING", rows);
        } catch (Exception e) {
            return emptyRow(materialType + " BILL CLOSING");
        }
    }

    // ── Repledge Opening ──────────────────────────────────────────────────────

    private Map<String, Object> repledgeOpening(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(repledge_id) bill_count,
                           COALESCE(SUM(amount - got_amount),0) debit,
                           COALESCE(SUM(amount),0) credit,
                           concat('( Amt: ', COALESCE(sum(amount),0),
                                  ' , Intr: ', COALESCE(sum(open_taken_amount - document_charge),0),
                                  ' , Doc: ', COALESCE(sum(document_charge),0), ')') credit_combo
                    FROM repledge_billing
                    WHERE company_id = ? AND jewel_material_type::text = ? AND opening_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow("REPLEDGE BILL OPENING", rows);
        } catch (Exception e) {
            return emptyRow("REPLEDGE BILL OPENING");
        }
    }

    // ── Repledge Closing ──────────────────────────────────────────────────────

    private Map<String, Object> repledgeClosing(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(repledge_id) bill_count,
                           COALESCE(SUM(given_amount),0) debit,
                           0 credit,
                           concat('( Amt: ', COALESCE(sum(amount),0),
                                  ' , Intr: ', COALESCE(sum(close_taken_amount),0), ')') credit_combo
                    FROM repledge_billing
                    WHERE company_id = ? AND jewel_material_type::text = ? AND closing_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow("REPLEDGE BILL CLOSING", rows);
        } catch (Exception e) {
            return emptyRow("REPLEDGE BILL CLOSING");
        }
    }

    // ── Expenses ──────────────────────────────────────────────────────────────

    private Map<String, Object> expenses(String companyId, String date) {
        try {
            String sql = """
                    SELECT COALESCE(SUM(expense_count),0) bill_count,
                           COALESCE(SUM(debit),0) debit, 0 credit, '' credit_combo
                    FROM (
                      SELECT COUNT(employee_id) expense_count, SUM(debitted_amount) debit FROM employee_daily_allowance_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM employee_advance_amount_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM employee_salary_amount_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM employee_other_amount_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM company_bill_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM company_other_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM repledge_bill_debit WHERE company_id=? AND debitted_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(debitted_amount) FROM repledge_other_debit WHERE company_id=? AND debitted_date::date=?::date
                    ) all_expenses
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql,
                companyId, date, companyId, date, companyId, date, companyId, date,
                companyId, date, companyId, date, companyId, date, companyId, date);
            return buildRow("EXPENSES", rows);
        } catch (Exception e) {
            log.warn("Expenses query error: {}", e.getMessage());
            return emptyRow("EXPENSES");
        }
    }

    // ── Incomes ───────────────────────────────────────────────────────────────

    private Map<String, Object> incomes(String companyId, String date) {
        try {
            String sql = """
                    SELECT COALESCE(SUM(income_count),0) bill_count,
                           0 debit, COALESCE(SUM(credit),0) credit, '' credit_combo
                    FROM (
                      SELECT COUNT(id) income_count, SUM(credit_amount) credit FROM employee_advance_amount_credit WHERE company_id=? AND credited_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(credited_amount) FROM employee_other_amount_credit WHERE company_id=? AND credited_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(credited_amount) FROM company_bill_credit WHERE company_id=? AND credited_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(credited_amount) FROM company_other_credit WHERE company_id=? AND credited_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(credited_amount) FROM repledge_bill_credit WHERE company_id=? AND credited_date::date=?::date
                      UNION ALL
                      SELECT COUNT(id), SUM(credited_amount) FROM repledge_other_credit WHERE company_id=? AND credited_date::date=?::date
                    ) all_incomes
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql,
                companyId, date, companyId, date, companyId, date,
                companyId, date, companyId, date, companyId, date);
            return buildRow("INCOMES", rows);
        } catch (Exception e) {
            log.warn("Incomes query error: {}", e.getMessage());
            return emptyRow("INCOMES");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> buildRow(String name, List<Map<String, Object>> rows) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name);
        if (rows.isEmpty()) { row.put("count", 0); row.put("debit", 0.0); row.put("credit", 0.0); row.put("detail", ""); return row; }
        Map<String, Object> r = rows.get(0);
        row.put("count",  toLong(r.get("bill_count")));
        row.put("debit",  toDouble(r.get("debit")));
        row.put("credit", toDouble(r.get("credit")));
        row.put("detail", str(r.get("credit_combo")));
        return row;
    }

    private Map<String, Object> emptyRow(String name) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name); row.put("count", 0); row.put("debit", 0.0); row.put("credit", 0.0); row.put("detail", "");
        return row;
    }

    private double toDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return 0.0; }
    }

    private long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return 0L; }
    }

    private String str(Object v) { return v == null ? "" : v.toString(); }
}
