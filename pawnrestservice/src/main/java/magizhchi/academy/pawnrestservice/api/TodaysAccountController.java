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

            // ── Balance settings ─────────────────────────────────────────────
            addBalanceSettings(result, companyId, targetDate);

            // ── Operations (exact desktop query order) ───────────────────────
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

            // actualBalance = preActualBalance + (totalCredit - totalDebit)  [exact desktop formula]
            double preActual = toDouble(result.get("preActualBalance"));
            double actualBalance = preActual + (totalCredit - totalDebit);
            result.put("actualBalance", actualBalance);

            double availableBalance = toDouble(result.get("availableBalance"));
            result.put("deficit", availableBalance - actualBalance);

            // ── Account status (desktop save-mode logic) ──────────────────────
            // L row TODAYS_DATE = last saved/closed date.
            // Save Mode ON  → dpTodaysDate = L_DATE + 1  → requestedDate > L_DATE  → YET TO CLOSE
            // Save Mode OFF → dpTodaysDate <= L_DATE                               → CLOSED
            result.put("accountStatus", resolveAccountStatus(companyId, targetDate));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("TodaysAccount error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Account status (desktop save-mode logic) ──────────────────────────────
    // Mirrors: tgOff.isSelected() →
    //   allow = isFirstDateIsLesserOrEqualToSecondDate(selectedDate, L_row.TODAYS_DATE)
    // In Save Mode ON the date shown = L_DATE + 1 day (getNextDateWithFormatted).
    // So: requestedDate > L_TODAYS_DATE  →  YET TO CLOSE
    //     requestedDate <= L_TODAYS_DATE →  CLOSED

    private String resolveAccountStatus(String companyId, String targetDate) {
        try {
            String sql = """
                    SELECT TODAYS_DATE::date
                    FROM COMPANY_TODAYS_ACCOUNT
                    WHERE COMPANY_ID = ? AND REF_MARK = 'L'
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId);
            if (rows.isEmpty()) return "YET TO CLOSE";
            Object lDate = rows.get(0).get("todays_date");
            if (lDate == null) return "YET TO CLOSE";
            LocalDate lastClosedDate = LocalDate.parse(lDate.toString().substring(0, 10));
            LocalDate requested      = LocalDate.parse(targetDate);
            return requested.isAfter(lastClosedDate) ? "YET TO CLOSE" : "CLOSED";
        } catch (Exception e) {
            log.warn("resolveAccountStatus: {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    // ── Balance settings ──────────────────────────────────────────────────────
    // Exact port of getTodaysAccountSettingsValues() + getAvailableAmount()

    private void addBalanceSettings(Map<String, Object> result, String companyId, String date) {
        try {
            // Try exact date first (desktop: WHERE TODAYS_DATE = ? with java.sql.Date)
            String sql = """
                    SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT,
                           PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT,
                           TODAYS_AVAILABLE_AMOUNT, TODAYS_DEFICIT_AMOUNT, PRE_NOTE, TODAYS_NOTE
                    FROM COMPANY_TODAYS_ACCOUNT
                    WHERE COMPANY_ID = ? AND TODAYS_DATE::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, date);

            if (rows.isEmpty()) {
                // Desktop fallback: WHERE REF_MARK = 'L'
                String sql2 = """
                        SELECT PRE_DATE, PRE_ACTUAL_AMOUNT, PRE_AVAILABLE_AMOUNT,
                               PRE_DEFICIT_AMOUNT, TODAYS_DATE, TODAYS_ACTUAL_AMOUNT,
                               TODAYS_AVAILABLE_AMOUNT, TODAYS_DEFICIT_AMOUNT, PRE_NOTE, TODAYS_NOTE
                        FROM COMPANY_TODAYS_ACCOUNT
                        WHERE COMPANY_ID = ? AND REF_MARK = 'L'
                        """;
                rows = jdbc.queryForList(sql2, companyId);
            }

            if (!rows.isEmpty()) {
                Map<String, Object> r = rows.get(0);
                result.put("preDate",             str(r.get("PRE_DATE")));
                result.put("preActualBalance",    toDouble(r.get("PRE_ACTUAL_AMOUNT")));
                result.put("preAvailableBalance", toDouble(r.get("PRE_AVAILABLE_AMOUNT")));
                result.put("preDeficit",          toDouble(r.get("PRE_DEFICIT_AMOUNT")));
                result.put("preNote",             str(r.get("PRE_NOTE")));
                result.put("todaysNote",          str(r.get("TODAYS_NOTE")));
            } else {
                result.put("preDate", ""); result.put("preActualBalance", 0.0);
                result.put("preAvailableBalance", 0.0); result.put("preDeficit", 0.0);
                result.put("preNote", ""); result.put("todaysNote", "");
            }

            // Available balance: desktop reads from company_todays_account_available_amount
            // (getAvailableAmount() — separate table, not the column in company_todays_account)
            try {
                String availSql = """
                        SELECT todays_available_amount
                        FROM company_todays_account_available_amount
                        WHERE COMPANY_ID = ? AND todays_date::date = ?::date
                        """;
                List<Map<String, Object>> availRows = jdbc.queryForList(availSql, companyId, date);
                result.put("availableBalance",
                        availRows.isEmpty() ? 0.0
                                : toDouble(availRows.get(0).get("todays_available_amount")));
            } catch (Exception ae) {
                log.warn("Available amount lookup failed: {}", ae.getMessage());
                result.put("availableBalance", 0.0);
            }

        } catch (Exception e) {
            log.warn("Balance settings error: {}", e.getMessage());
            result.put("preDate", ""); result.put("preActualBalance", 0.0);
            result.put("preAvailableBalance", 0.0); result.put("preDeficit", 0.0);
            result.put("preNote", ""); result.put("availableBalance", 0.0);
            result.put("todaysNote", "");
        }
    }

    // ── Bill Opening (exact desktop getBillOpeningAccountValues SQL) ───────────

    private Map<String, Object> billOpening(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT COUNT(BILL_NUMBER) BILL_COUNT,
                           SUM(AMOUNT) DEBIT,
                           SUM(AMOUNT - GIVEN_AMOUNT) CREDIT,
                           concat(' (  Amt: ', sum(COALESCE(AMOUNT,0)),
                                  ' , Intr: ', sum(OPEN_TAKEN_AMOUNT - document_charge),
                                  ' , Doc: ', sum(document_charge), '  )' ,
                                  '  ( RB: ', COUNT(CASE WHEN REBILLED_FROM IS NOT NULL THEN REBILLED_FROM END),
                                  ', NB: ', COUNT(CASE WHEN REBILLED_FROM IS NULL THEN BILL_NUMBER END), '  )') credit_combo
                    FROM COMPANY_BILLING
                    WHERE COMPANY_ID = ?
                      AND STATUS NOT IN ('CANCELED')
                      AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE
                      AND OPENING_DATE::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " BILL OPENING", rows, "BILL_COUNT", "DEBIT", "CREDIT", "credit_combo");
        } catch (Exception e) {
            log.warn("billOpening {}: {}", materialType, e.getMessage());
            return emptyRow(materialType + " BILL OPENING");
        }
    }

    // ── Bill Advance (exact desktop getBillAdvanceAmountAccountValues SQL) ─────

    private Map<String, Object> billAdvance(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT count(bill_number) bill_count, 0 debit, sum(paid_amount) credit,
                           0 credit_combo
                    FROM COMPANY_ADVANCE_AMOUNT
                    WHERE company_id = ?
                      AND jewel_material_type = ?::material_type
                      AND paid_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " ADVANCE AMOUNT", rows, "bill_count", "debit", "credit", "credit_combo");
        } catch (Exception e) {
            log.warn("billAdvance {}: {}", materialType, e.getMessage());
            return emptyRow(materialType + " ADVANCE AMOUNT");
        }
    }

    // ── Bill Closing (exact desktop getBillClosingAccountValues SQL) ───────────

    private Map<String, Object> billClosing(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT count(bill_number) bill_count, 0 debit, sum(got_amount) credit,
                           concat(' ( Amt: ', sum(COALESCE(AMOUNT,0)),
                                  ' , Intr: ', sum(COALESCE(close_taken_amount,0)),
                                  ' , Fine: ', sum(COALESCE(total_other_charges,0)),
                                  ' , Less: ', sum(COALESCE(discount_amount,0)),
                                  ' , Adv Amt: ', sum(COALESCE(total_advance_amount_paid,0)), '  )') credit_combo
                    FROM company_billing
                    WHERE company_id = ?
                      AND jewel_material_type = ?::material_type
                      AND closing_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow(materialType + " BILL CLOSING", rows, "bill_count", "debit", "credit", "credit_combo");
        } catch (Exception e) {
            log.warn("billClosing {}: {}", materialType, e.getMessage());
            return emptyRow(materialType + " BILL CLOSING");
        }
    }

    // ── Repledge Opening (exact desktop getReBillOpeningAccountValues SQL) ─────

    private Map<String, Object> repledgeOpening(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT count(repledge_id) bill_count,
                           sum(amount - got_amount) debit,
                           sum(amount) credit,
                           concat(' (  Amt: ', sum(COALESCE(amount,0)),
                                  ' , Intr: ', sum(COALESCE(OPEN_TAKEN_AMOUNT - document_charge,0)),
                                  ' , Doc: ', sum(COALESCE(document_charge,0)), '  )') credit_combo
                    FROM repledge_billing
                    WHERE company_id = ?
                      AND jewel_material_type = ?::material_type
                      AND opening_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow("REPLEDGE BILL OPENING", rows, "bill_count", "debit", "credit", "credit_combo");
        } catch (Exception e) {
            log.warn("repledgeOpening: {}", e.getMessage());
            return emptyRow("REPLEDGE BILL OPENING");
        }
    }

    // ── Repledge Closing (exact desktop getReBillClosingAccountValues SQL) ─────

    private Map<String, Object> repledgeClosing(String companyId, String date, String materialType) {
        try {
            String sql = """
                    SELECT count(repledge_id) bill_count,
                           sum(given_amount) debit,
                           0 credit,
                           concat(' (  Amt: ', sum(COALESCE(amount,0)),
                                  ' , Intr: ', sum(COALESCE(close_taken_amount,0)), '  )') credit_combo
                    FROM repledge_billing
                    WHERE company_id = ?
                      AND jewel_material_type = ?::material_type
                      AND closing_date::date = ?::date
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, materialType, date);
            return buildRow("REPLEDGE BILL CLOSING", rows, "bill_count", "debit", "credit", "credit_combo");
        } catch (Exception e) {
            log.warn("repledgeClosing: {}", e.getMessage());
            return emptyRow("REPLEDGE BILL CLOSING");
        }
    }

    // ── Expenses (exact desktop getAllExpensesAccountValues SQL) ──────────────

    private Map<String, Object> expenses(String companyId, String date) {
        try {
            String sql = """
                    SELECT SUM(expense_count), sum(debit), sum(credit), 0 credit_combo
                    FROM (
                        SELECT count(employee_id) expense_count, sum(debitted_amount) debit, 0 credit, 0 credit_combo
                        FROM employee_daily_allowance_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM employee_advance_amount_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM employee_salary_amount_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM employee_other_amount_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM company_bill_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM company_other_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM repledge_bill_debit WHERE company_id = ? AND debitted_date::date = ?::date
                        UNION ALL
                        SELECT count(id), sum(debitted_amount), 0, 0
                        FROM repledge_other_debit WHERE company_id = ? AND debitted_date::date = ?::date
                    ) all_expenses
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql,
                    companyId, date, companyId, date, companyId, date, companyId, date,
                    companyId, date, companyId, date, companyId, date, companyId, date);
            return buildRow("EXPENSES", rows, "sum", "sum", "sum", "credit_combo");
        } catch (Exception e) {
            log.warn("expenses: {}", e.getMessage());
            return emptyRow("EXPENSES");
        }
    }

    // ── Incomes (exact desktop getAllIncomeAccountValues SQL) ─────────────────

    private Map<String, Object> incomes(String companyId, String date) {
        try {
            String sql = """
                    SELECT SUM(income_count), sum(debit), sum(credit), 0 credit_combo
                    FROM (
                        SELECT count(id) income_count, 0 debit, sum(credit_amount) credit, 0 credit_combo
                        FROM employee_advance_amount_credit WHERE company_id = ? AND credited_date::date = ?::date
                        UNION ALL
                        SELECT count(id), 0, sum(credited_amount), 0
                        FROM employee_other_amount_credit WHERE company_id = ? AND credited_date::date = ?::date
                        UNION ALL
                        SELECT count(id), 0, sum(credited_amount), 0
                        FROM company_bill_credit WHERE company_id = ? AND credited_date::date = ?::date
                        UNION ALL
                        SELECT count(id), 0, sum(credited_amount), 0
                        FROM company_other_credit WHERE company_id = ? AND credited_date::date = ?::date
                        UNION ALL
                        SELECT count(id), 0, sum(credited_amount), 0
                        FROM repledge_bill_credit WHERE company_id = ? AND credited_date::date = ?::date
                        UNION ALL
                        SELECT count(id), 0, sum(credited_amount), 0
                        FROM repledge_other_credit WHERE company_id = ? AND credited_date::date = ?::date
                    ) all_incomes
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql,
                    companyId, date, companyId, date, companyId, date,
                    companyId, date, companyId, date, companyId, date);
            return buildRow("INCOMES", rows, "sum", "sum", "sum", "credit_combo");
        } catch (Exception e) {
            log.warn("incomes: {}", e.getMessage());
            return emptyRow("INCOMES");
        }
    }

    // ── Detail endpoint ───────────────────────────────────────────────────────

    @GetMapping("/details")
    public ResponseEntity<?> details(
            @RequestParam String companyId,
            @RequestParam(required = false) String date,
            @RequestParam String type) {
        try {
            String targetDate = (date != null && !date.isBlank()) ? date : LocalDate.now().toString();
            return switch (type.toUpperCase()) {
                case "GOLD_OPENING"     -> ResponseEntity.ok(billOpeningDetail(companyId, targetDate, "GOLD"));
                case "SILVER_OPENING"   -> ResponseEntity.ok(billOpeningDetail(companyId, targetDate, "SILVER"));
                case "GOLD_CLOSING"     -> ResponseEntity.ok(billClosingDetail(companyId, targetDate, "GOLD"));
                case "SILVER_CLOSING"   -> ResponseEntity.ok(billClosingDetail(companyId, targetDate, "SILVER"));
                case "GOLD_ADVANCE"     -> ResponseEntity.ok(billAdvanceDetail(companyId, targetDate, "GOLD"));
                case "SILVER_ADVANCE"   -> ResponseEntity.ok(billAdvanceDetail(companyId, targetDate, "SILVER"));
                case "REPLEDGE_OPENING" -> ResponseEntity.ok(repledgeOpeningDetail(companyId, targetDate));
                case "REPLEDGE_CLOSING" -> ResponseEntity.ok(repledgeClosingDetail(companyId, targetDate));
                case "EXPENSES"         -> ResponseEntity.ok(expensesDetail(companyId, targetDate));
                case "INCOMES"          -> ResponseEntity.ok(incomesDetail(companyId, targetDate));
                default -> ResponseEntity.badRequest().body(Map.of("error", "Unknown type: " + type));
            };
        } catch (Exception e) {
            log.error("Details error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Bill Opening Detail (exact desktop getBillOpeningTableValue SQL) ───────

    private Map<String, Object> billOpeningDetail(String companyId, String date, String mat) {
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO,
                       BILL_NUMBER, OPENING_DATE, CUSTOMER_NAME, ITEMS,
                       AMOUNT, TOGIVE_AMOUNT, GIVEN_AMOUNT, STATUS,
                       CREATED_USER_ID, INTEREST, DOCUMENT_CHARGE,
                       to_char(created_date, 'dd-MM-YY / HH24:MI:ss') created_ts
                FROM COMPANY_BILLING
                WHERE COMPANY_ID = ?
                  AND STATUS NOT IN ('CANCELED')
                  AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE
                  AND OPENING_DATE::date = ?::date
                ORDER BY TO_NUMBER(REGEXP_REPLACE(COALESCE(BILL_NUMBER,'0'), '[^0-9]*', '0'), '9999999999')
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql, companyId, mat, date)) {
                rows.add(List.of(
                        str(r.get("slno")), str(r.get("bill_number")),
                        str(r.get("opening_date")), str(r.get("customer_name")),
                        str(r.get("items")), fmt(r.get("amount")),
                        fmt(r.get("togive_amount")), fmt(r.get("given_amount")),
                        str(r.get("status")), str(r.get("created_user_id")),
                        str(r.get("interest")), str(r.get("document_charge")),
                        str(r.get("created_ts"))));
            }
        } catch (Exception e) { log.warn("billOpeningDetail: {}", e.getMessage()); }
        return detail(mat + " Bill Opening",
                List.of("Sl","Bill No","Date","Customer","Items","Amount","To Give","Given","Status","User","Interest","Doc Charge","Created"), rows);
    }

    // ── Bill Closing Detail (exact desktop getBillClosingTableValue SQL) ───────

    private Map<String, Object> billClosingDetail(String companyId, String date, String mat) {
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO,
                       BILL_NUMBER, CLOSING_DATE, CUSTOMER_NAME, ITEMS,
                       AMOUNT, TOGET_AMOUNT, GOT_AMOUNT, STATUS,
                       to_char(closed_date, 'dd-MM-YY / HH24:MI:ss') closed_ts,
                       CLOSED_USER_ID, INTEREST, close_taken_amount, total_advance_amount_paid
                FROM COMPANY_BILLING
                WHERE COMPANY_ID = ?
                  AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE
                  AND CLOSING_DATE::date = ?::date
                ORDER BY TO_NUMBER(REGEXP_REPLACE(COALESCE(BILL_NUMBER,'0'), '[^0-9]*', '0'), '9999999999')
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql, companyId, mat, date)) {
                rows.add(List.of(
                        str(r.get("slno")), str(r.get("bill_number")),
                        str(r.get("closing_date")), str(r.get("customer_name")),
                        str(r.get("items")), fmt(r.get("amount")),
                        fmt(r.get("toget_amount")), fmt(r.get("got_amount")),
                        str(r.get("status")), str(r.get("closed_ts")),
                        str(r.get("closed_user_id")), str(r.get("interest")),
                        fmt(r.get("close_taken_amount")), fmt(r.get("total_advance_amount_paid"))));
            }
        } catch (Exception e) { log.warn("billClosingDetail: {}", e.getMessage()); }
        return detail(mat + " Bill Closing",
                List.of("Sl","Bill No","Date","Customer","Items","Amount","To Get","Got","Status","Closed At","User","Interest","Close Interest","Adv Paid"), rows);
    }

    // ── Bill Advance Detail (exact desktop getBillAdvanceAmountTableValue SQL) ─

    private Map<String, Object> billAdvanceDetail(String companyId, String date, String mat) {
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY TO_NUMBER(REGEXP_REPLACE(AA.BILL_NUMBER, '\\D', '', 'g'), '9999999999')) AS SLNO,
                       AA.PAID_DATE, AA.BILL_NUMBER, CB.STATUS,
                       AA.BILL_AMOUNT, AA.PAID_AMOUNT, AA.TOTAL_AMOUNT, AA.USER_ID
                FROM COMPANY_ADVANCE_AMOUNT AA, COMPANY_BILLING CB
                WHERE AA.BILL_NUMBER = CB.BILL_NUMBER
                  AND AA.JEWEL_MATERIAL_TYPE = CB.JEWEL_MATERIAL_TYPE
                  AND AA.COMPANY_ID = CB.COMPANY_ID
                  AND AA.COMPANY_ID = ?
                  AND AA.JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE
                  AND AA.PAID_DATE::date = ?::date
                ORDER BY TO_NUMBER(REGEXP_REPLACE(COALESCE(AA.BILL_NUMBER,'0'), '[^0-9]*', '0'), '9999999999')
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql, companyId, mat, date)) {
                rows.add(List.of(
                        str(r.get("slno")), str(r.get("paid_date")),
                        str(r.get("bill_number")), str(r.get("status")),
                        fmt(r.get("bill_amount")), fmt(r.get("paid_amount")),
                        fmt(r.get("total_amount")), str(r.get("user_id"))));
            }
        } catch (Exception e) { log.warn("billAdvanceDetail: {}", e.getMessage()); }
        return detail(mat + " Advance Amount",
                List.of("Sl","Date","Bill No","Status","Bill Amt","Paid Amt","Total","User"), rows);
    }

    // ── Repledge Opening Detail (exact desktop getReBillOpeningTableValue SQL) ─

    private Map<String, Object> repledgeOpeningDetail(String companyId, String date) {
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY REPLEDGE_BILL_ID) AS SLNO,
                       REPLEDGE_BILL_ID, OPENING_DATE, STATUS, REPLEDGE_NAME,
                       REPLEDGE_BILL_NUMBER, COMPANY_BILL_NUMBER,
                       AMOUNT, TOGET_AMOUNT, GOT_AMOUNT, CREATED_USER_ID, open_taken_amount
                FROM REPLEDGE_BILLING
                WHERE COMPANY_ID = ?
                  AND JEWEL_MATERIAL_TYPE = 'GOLD'::MATERIAL_TYPE
                  AND OPENING_DATE::date = ?::date
                ORDER BY REPLEDGE_NAME
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql, companyId, date)) {
                rows.add(List.of(
                        str(r.get("slno")), str(r.get("repledge_bill_id")),
                        str(r.get("opening_date")), str(r.get("status")),
                        str(r.get("repledge_name")), str(r.get("repledge_bill_number")),
                        str(r.get("company_bill_number")), fmt(r.get("amount")),
                        fmt(r.get("toget_amount")), fmt(r.get("got_amount")),
                        str(r.get("created_user_id")), fmt(r.get("open_taken_amount"))));
            }
        } catch (Exception e) { log.warn("repledgeOpeningDetail: {}", e.getMessage()); }
        return detail("Repledge Bill Opening",
                List.of("Sl","ID","Date","Status","Name","Repledge Bill","Company Bill","Amount","To Get","Got","User","Interest"), rows);
    }

    // ── Repledge Closing Detail (exact desktop getReBillClosingTableValue SQL) ─

    private Map<String, Object> repledgeClosingDetail(String companyId, String date) {
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY REPLEDGE_BILL_ID) AS SLNO,
                       REPLEDGE_BILL_ID, CLOSING_DATE, STATUS, REPLEDGE_NAME,
                       REPLEDGE_BILL_NUMBER, COMPANY_BILL_NUMBER,
                       AMOUNT, TOGIVE_AMOUNT, GIVEN_AMOUNT, CREATED_USER_ID, close_taken_amount
                FROM REPLEDGE_BILLING
                WHERE COMPANY_ID = ?
                  AND JEWEL_MATERIAL_TYPE = 'GOLD'::MATERIAL_TYPE
                  AND CLOSING_DATE::date = ?::date
                ORDER BY REPLEDGE_NAME
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql, companyId, date)) {
                rows.add(List.of(
                        str(r.get("slno")), str(r.get("repledge_bill_id")),
                        str(r.get("closing_date")), str(r.get("status")),
                        str(r.get("repledge_name")), str(r.get("repledge_bill_number")),
                        str(r.get("company_bill_number")), fmt(r.get("amount")),
                        fmt(r.get("togive_amount")), fmt(r.get("given_amount")),
                        str(r.get("created_user_id")), fmt(r.get("close_taken_amount"))));
            }
        } catch (Exception e) { log.warn("repledgeClosingDetail: {}", e.getMessage()); }
        return detail("Repledge Bill Closing",
                List.of("Sl","ID","Date","Status","Name","Repledge Bill","Company Bill","Amount","To Give","Given","User","Interest"), rows);
    }

    // ── Expenses Detail (exact desktop getExpenseTableValue SQL) ─────────────

    private Map<String, Object> expensesDetail(String companyId, String date) {
        String sql = """
                SELECT debitted_date, id, 'EMPLOYEE DAILY ALLOWANCE' AS type,
                       employee_name AS description, debitted_amount, user_id
                FROM employee_daily_allowance_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'EMPLOYEE ADVANCE AMOUNT',
                       concat('Salary Advance Amount - ', employee_name, ' - ', reason),
                       debitted_amount, user_id
                FROM employee_advance_amount_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'EMPLOYEE SALARY AMOUNT',
                       concat(employee_id, ' - ', employee_name), debitted_amount, user_id
                FROM employee_salary_amount_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'EMPLOYEE OTHER AMOUNT',
                       concat(employee_name, ' - ', debitted_reason), debitted_amount, user_id
                FROM employee_other_amount_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'COMPANY BILL',
                       concat(jewel_material_type, ' - ', bill_number), debitted_amount, user_id
                FROM company_bill_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'COMPANY OTHER AMOUNT',
                       concat(expense_or_asset, ' - ', expense_type, ' - ', name, ' - ', reason),
                       debitted_amount, user_id
                FROM company_other_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'REPLEDGE BILL',
                       concat('CompBillNo: ', bill_number, ' - ', repledge_name),
                       debitted_amount, user_id
                FROM repledge_bill_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                UNION ALL
                SELECT debitted_date, id, 'REPLEDGE OTHER AMOUNT',
                       concat(repledge_name, ' - ', reason), debitted_amount, user_id
                FROM repledge_other_debit
                WHERE company_id = ? AND debitted_date::date = ?::date
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql,
                    companyId, date, companyId, date, companyId, date, companyId, date,
                    companyId, date, companyId, date, companyId, date, companyId, date)) {
                rows.add(List.of(
                        str(r.get("debitted_date")), str(r.get("id")),
                        str(r.get("type")), str(r.get("description")),
                        fmt(r.get("debitted_amount")), str(r.get("user_id"))));
            }
        } catch (Exception e) { log.warn("expensesDetail: {}", e.getMessage()); }
        return detail("Expenses", List.of("Date","ID","Type","Description","Amount","User"), rows);
    }

    // ── Incomes Detail (exact desktop getIncomeTableValue SQL) ────────────────

    private Map<String, Object> incomesDetail(String companyId, String date) {
        String sql = """
                SELECT credited_date, id, 'EMPLOYEE ADVANCE AMOUNT' AS type,
                       employee_name AS description, credit_amount AS amount, user_id
                FROM employee_advance_amount_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                UNION ALL
                SELECT credited_date, id, 'EMPLOYEE OTHER AMOUNT',
                       concat(employee_name, ' - ', credited_reason), credited_amount, user_id
                FROM employee_other_amount_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                UNION ALL
                SELECT credited_date, id, 'COMPANY BILL',
                       concat(jewel_material_type, ' - ', bill_number), credited_amount, user_id
                FROM company_bill_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                UNION ALL
                SELECT credited_date, id, 'COMPANY OTHER',
                       concat(income_or_liability, ' - ', expense_type, ' - ', name, ' - ', reason),
                       credited_amount, user_id
                FROM company_other_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                UNION ALL
                SELECT credited_date, id, 'REPLEDGE BILL',
                       concat(' CompBillNo: ', bill_number, ' - ', repledge_name),
                       credited_amount, user_id
                FROM repledge_bill_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                UNION ALL
                SELECT credited_date, id, 'REPLEDGE OTHER',
                       concat(repledge_name, ' - ', reason), credited_amount, user_id
                FROM repledge_other_credit
                WHERE company_id = ? AND credited_date::date = ?::date
                """;
        List<List<String>> rows = new ArrayList<>();
        try {
            for (Map<String, Object> r : jdbc.queryForList(sql,
                    companyId, date, companyId, date, companyId, date,
                    companyId, date, companyId, date, companyId, date)) {
                rows.add(List.of(
                        str(r.get("credited_date")), str(r.get("id")),
                        str(r.get("type")), str(r.get("description")),
                        fmt(r.get("amount")), str(r.get("user_id"))));
            }
        } catch (Exception e) { log.warn("incomesDetail: {}", e.getMessage()); }
        return detail("Incomes", List.of("Date","ID","Type","Description","Amount","User"), rows);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> buildRow(String name, List<Map<String, Object>> rows,
            String countCol, String debitCol, String creditCol, String detailCol) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name);
        if (rows.isEmpty() || rows.get(0).get(countCol) == null) {
            row.put("count", 0); row.put("debit", 0.0); row.put("credit", 0.0); row.put("detail", "");
            return row;
        }
        Map<String, Object> r = rows.get(0);
        row.put("count",  toLong(r.get(countCol)));
        row.put("debit",  toDouble(r.get(debitCol)));
        row.put("credit", toDouble(r.get(creditCol)));
        row.put("detail", str(r.get(detailCol)));
        return row;
    }

    private Map<String, Object> emptyRow(String name) {
        return Map.of("name", name, "count", 0L, "debit", 0.0, "credit", 0.0, "detail", "");
    }

    private Map<String, Object> detail(String title, List<String> headers, List<List<String>> rows) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("title", title);
        m.put("headers", headers);
        m.put("rows", rows);
        m.put("count", rows.size());
        return m;
    }

    private String fmt(Object v) {
        if (v == null) return "0";
        double d = toDouble(v);
        if (d == Math.floor(d)) return String.valueOf((long) d);
        return String.format("%.2f", d);
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

    private String str(Object v) { return v == null ? "" : v.toString().trim(); }
}
