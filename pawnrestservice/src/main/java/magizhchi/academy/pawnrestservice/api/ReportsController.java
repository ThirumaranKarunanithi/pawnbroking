package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private static final Logger log = LoggerFactory.getLogger(ReportsController.class);
    private final JdbcTemplate jdbc;

    public ReportsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    // ── Monthly MIS ───────────────────────────────────────────────────────────

    /**
     * GET /api/reports/monthly?companyId=CMP1
     * Returns month-wise summary of bill openings, closings and stock
     */
    /**
     * GET /api/reports/monthly?companyId=CMP1
     *
     * Exact port of desktop ReportDBOperation.getCompMISValues():
     * 10 columns — month, pawnBills, pawnAmt, redeemBills, redeemAmt,
     *              profit, stockBills (cumulative), stockAmt (cumulative),
     *              earnedBills, earnedAmt
     * Ordered newest-first.
     */
    @GetMapping("/monthly")
    public ResponseEntity<?> monthly(@RequestParam String companyId) {
        try {
            // ── Exact desktop SQL (ported to PostgreSQL / Spring JDBC) ────────
            String sql = """
                SELECT
                    CASE child.mon
                        WHEN '01' THEN 'JAN-' || child.yyyy
                        WHEN '02' THEN 'FEB-' || child.yyyy
                        WHEN '03' THEN 'MAR-' || child.yyyy
                        WHEN '04' THEN 'APR-' || child.yyyy
                        WHEN '05' THEN 'MAY-' || child.yyyy
                        WHEN '06' THEN 'JUN-' || child.yyyy
                        WHEN '07' THEN 'JUL-' || child.yyyy
                        WHEN '08' THEN 'AUG-' || child.yyyy
                        WHEN '09' THEN 'SEP-' || child.yyyy
                        WHEN '10' THEN 'OCT-' || child.yyyy
                        WHEN '11' THEN 'NOV-' || child.yyyy
                        WHEN '12' THEN 'DEC-' || child.yyyy
                        ELSE '??'
                    END                                        AS month,
                    child.pawn_total_bill,
                    child.pawn_amount,
                    child.redeem_total_bills,
                    child.redeem_amt,
                    child.tot_profit,
                    child.total_stock_bills,
                    child.total_stock_amount,
                    (child.pawn_total_bill - child.redeem_total_bills) AS stock_bills_earned,
                    (child.pawn_amount     - child.redeem_amt)         AS stock_amount_earned
                FROM (
                    SELECT
                        mon, yyyy,
                        SUM(pawn_total_bill)    AS pawn_total_bill,
                        SUM(pawn_amount)        AS pawn_amount,
                        SUM(redeem_total_bills) AS redeem_total_bills,
                        SUM(redeem_amt)         AS redeem_amt,
                        SUM(interest)           AS tot_profit,
                        SUM(SUM(pawn_total_bill) - SUM(redeem_total_bills))
                            OVER (ORDER BY yyyy::int ASC, mon::int ASC)  AS total_stock_bills,
                        SUM(SUM(pawn_amount)     - SUM(redeem_amt))
                            OVER (ORDER BY yyyy::int ASC, mon::int ASC)  AS total_stock_amount
                    FROM (
                        -- Openings
                        SELECT TO_CHAR(opening_date,'MM')        AS mon,
                               EXTRACT(YEAR FROM opening_date)::text AS yyyy,
                               COUNT(bill_number)                AS pawn_total_bill,
                               COALESCE(SUM(amount), 0)          AS pawn_amount,
                               0 AS redeem_total_bills,
                               0 AS redeem_amt,
                               0 AS interest
                        FROM company_billing
                        WHERE status::text NOT IN ('CANCELED')
                          AND company_id = ?
                        GROUP BY 1, 2
                        UNION ALL
                        -- Closings (uses original pledge amount, matching desktop)
                        SELECT TO_CHAR(closing_date,'MM')        AS mon,
                               EXTRACT(YEAR FROM closing_date)::text AS yyyy,
                               0 AS pawn_total_bill,
                               0 AS pawn_amount,
                               COUNT(bill_number)                AS redeem_total_bills,
                               COALESCE(SUM(amount), 0)          AS redeem_amt,
                               0 AS interest
                        FROM company_billing
                        WHERE status::text IN (
                                'CLOSED','DELIVERED','REBILLED',
                                'REBILLED-ADDED','REBILLED-REMOVED','REBILLED-MULTIPLE')
                          AND company_id = ?
                        GROUP BY 1, 2
                        UNION ALL
                        -- Profit from daily account
                        SELECT TO_CHAR(todays_date,'MM')         AS mon,
                               EXTRACT(YEAR FROM todays_date)::text AS yyyy,
                               0 AS pawn_total_bill,
                               0 AS pawn_amount,
                               0 AS redeem_total_bills,
                               0 AS redeem_amt,
                               COALESCE(SUM(todays_pf_amount), 0) AS interest
                        FROM company_todays_account_available_amount
                        WHERE company_id = ?
                        GROUP BY 1, 2
                    ) chi
                    GROUP BY chi.mon, chi.yyyy
                ) child
                ORDER BY child.yyyy::int DESC, child.mon::int DESC
                """;

            List<Map<String, Object>> raw =
                    jdbc.queryForList(sql, companyId, companyId, companyId);

            // Map to camelCase response matching Android expectations
            List<Map<String, Object>> months = new ArrayList<>();
            for (Map<String, Object> r : raw) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("month",        r.get("month"));
                m.put("pawnBills",    toLong(r.get("pawn_total_bill")));
                m.put("pawnAmount",   toDouble(r.get("pawn_amount")));
                m.put("redeemBills",  toLong(r.get("redeem_total_bills")));
                m.put("redeemAmount", toDouble(r.get("redeem_amt")));
                m.put("profit",       toDouble(r.get("tot_profit")));
                m.put("stockBills",   toLong(r.get("total_stock_bills")));
                m.put("stockAmount",  toDouble(r.get("total_stock_amount")));
                m.put("earnedBills",  toLong(r.get("stock_bills_earned")));
                m.put("earnedAmount", toDouble(r.get("stock_amount_earned")));
                months.add(m);
            }

            return ResponseEntity.ok(Map.of("months", months, "total", months.size()));
        } catch (Exception e) {
            log.error("Monthly/MIS report error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Trial Balance ─────────────────────────────────────────────────────────

    /**
     * GET /api/reports/trial-balance?companyId=CMP1&from=yyyy-MM-dd&to=yyyy-MM-dd
     */
    @GetMapping("/trial-balance")
    public ResponseEntity<?> trialBalance(
            @RequestParam String companyId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        try {
            String f = (from != null && !from.isBlank()) ? from
                     : LocalDate.now().withDayOfMonth(1).toString();
            String t = (to != null && !to.isBlank()) ? to
                     : LocalDate.now().toString();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("from", f);
            result.put("to",   t);

            // ── INCOME ──────────────────────────────────────────────────────
            List<Map<String, Object>> income = new ArrayList<>();
            income.add(incomeLine("Gold Open Interest",    goldSilverInterest(companyId, f, t, "GOLD",   "opening_date", "open_taken_amount")));
            income.add(incomeLine("Silver Open Interest",  goldSilverInterest(companyId, f, t, "SILVER", "opening_date", "open_taken_amount")));
            income.add(incomeLine("Gold Close Interest",   goldSilverInterest(companyId, f, t, "GOLD",   "closing_date", "close_taken_amount")));
            income.add(incomeLine("Silver Close Interest", goldSilverInterest(companyId, f, t, "SILVER", "closing_date", "close_taken_amount")));
            income.add(incomeLine("Gold Other Charges",    goldSilverCharges(companyId, f, t, "GOLD")));
            income.add(incomeLine("Silver Other Charges",  goldSilverCharges(companyId, f, t, "SILVER")));
            income.add(incomeLine("Advance Amount",        advanceCredit(companyId, f, t)));
            income.add(incomeLine("Company Bill Credit",   tableSum(companyId, f, t, "company_bill_credit",   "credited_amount", "credited_date")));
            income.add(incomeLine("Company Other Credit",  tableSum(companyId, f, t, "company_other_credit",  "credited_amount", "credited_date")));
            income.add(incomeLine("Repledge Bill Credit",  tableSum(companyId, f, t, "repledge_bill_credit",  "credited_amount", "credited_date")));
            income.add(incomeLine("Repledge Other Credit", tableSum(companyId, f, t, "repledge_other_credit", "credited_amount", "credited_date")));
            double totalIncome = income.stream().mapToDouble(m -> toDouble(m.get("amount"))).sum();
            result.put("income", income);
            result.put("totalIncome", totalIncome);

            // ── EXPENSE ─────────────────────────────────────────────────────
            List<Map<String, Object>> expense = new ArrayList<>();
            expense.add(expLine("Emp Daily Allowance",    tableSum(companyId, f, t, "employee_daily_allowance_debit",  "debitted_amount", "debitted_date")));
            expense.add(expLine("Emp Advance Debit",      tableSum(companyId, f, t, "employee_advance_amount_debit",   "debitted_amount", "debitted_date")));
            expense.add(expLine("Emp Salary Debit",       tableSum(companyId, f, t, "employee_salary_amount_debit",    "debitted_amount", "debitted_date")));
            expense.add(expLine("Emp Other Debit",        tableSum(companyId, f, t, "employee_other_amount_debit",     "debitted_amount", "debitted_date")));
            expense.add(expLine("Company Bill Debit",     tableSum(companyId, f, t, "company_bill_debit",              "debitted_amount", "debitted_date")));
            expense.add(expLine("Company Other Debit",    tableSum(companyId, f, t, "company_other_debit",             "debitted_amount", "debitted_date")));
            expense.add(expLine("Repledge Bill Debit",    tableSum(companyId, f, t, "repledge_bill_debit",             "debitted_amount", "debitted_date")));
            expense.add(expLine("Repledge Other Debit",   tableSum(companyId, f, t, "repledge_other_debit",            "debitted_amount", "debitted_date")));
            expense.add(expLine("Gold Discount",          discount(companyId, f, t, "GOLD")));
            expense.add(expLine("Silver Discount",        discount(companyId, f, t, "SILVER")));
            double totalExpense = expense.stream().mapToDouble(m -> toDouble(m.get("amount"))).sum();
            result.put("expense", expense);
            result.put("totalExpense", totalExpense);

            // ── ASSET ────────────────────────────────────────────────────────
            List<Map<String, Object>> asset = new ArrayList<>();
            asset.add(assetLine("Gold Bill Open Capital",   capitalSum(companyId, f, t, "GOLD",   "opening_date")));
            asset.add(assetLine("Silver Bill Open Capital", capitalSum(companyId, f, t, "SILVER", "opening_date")));
            asset.add(assetLine("Gold Bill Close Capital",  capitalSum(companyId, f, t, "GOLD",   "closing_date")));
            asset.add(assetLine("Silver Bill Close Capital",capitalSum(companyId, f, t, "SILVER", "closing_date")));
            asset.add(assetLine("Rep Open Capital",         repCapital(companyId, f, t, "opening_date")));
            asset.add(assetLine("Rep Close Capital",        repCapital(companyId, f, t, "closing_date")));
            asset.add(assetLine("Stock in Hand (Gold)",     stockAmount(companyId, "GOLD")));
            asset.add(assetLine("Stock in Hand (Silver)",   stockAmount(companyId, "SILVER")));
            double totalAsset = asset.stream().mapToDouble(m -> toDouble(m.get("amount"))).sum();
            result.put("asset", asset);
            result.put("totalAsset", totalAsset);

            // ── LIABILITY ────────────────────────────────────────────────────
            double cashInHand   = cashInHand(companyId);
            double profitLoss   = totalIncome - totalExpense;
            List<Map<String, Object>> liability = new ArrayList<>();
            liability.add(liabLine("Cash in Hand", cashInHand));
            liability.add(liabLine("Profit / Loss", profitLoss));
            double totalLiability = liability.stream().mapToDouble(m -> toDouble(m.get("amount"))).sum();
            result.put("liability", liability);
            result.put("totalLiability", totalLiability);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Trial balance error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private double goldSilverInterest(String cid, String f, String t, String mat, String dateCol, String amtCol) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(" + amtCol + "),0) FROM company_billing " +
                "WHERE company_id=? AND jewel_material_type::text=? " +
                "AND " + dateCol + "::date BETWEEN ?::date AND ?::date",
                Double.class, cid, mat, f, t);
        } catch (Exception e) { return 0; }
    }

    private double goldSilverCharges(String cid, String f, String t, String mat) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(total_other_charges),0) FROM company_billing " +
                "WHERE company_id=? AND jewel_material_type::text=? " +
                "AND closing_date::date BETWEEN ?::date AND ?::date",
                Double.class, cid, mat, f, t);
        } catch (Exception e) { return 0; }
    }

    private double advanceCredit(String cid, String f, String t) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(credit_amount),0) FROM employee_advance_amount_credit " +
                "WHERE company_id=? AND credited_date::date BETWEEN ?::date AND ?::date",
                Double.class, cid, f, t);
        } catch (Exception e) { return 0; }
    }

    private double tableSum(String cid, String f, String t, String table, String amtCol, String dateCol) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(" + amtCol + "),0) FROM " + table +
                " WHERE company_id=? AND " + dateCol + "::date BETWEEN ?::date AND ?::date",
                Double.class, cid, f, t);
        } catch (Exception e) { return 0; }
    }

    private double capitalSum(String cid, String f, String t, String mat, String dateCol) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM company_billing " +
                "WHERE company_id=? AND jewel_material_type::text=? " +
                "AND " + dateCol + "::date BETWEEN ?::date AND ?::date",
                Double.class, cid, mat, f, t);
        } catch (Exception e) { return 0; }
    }

    private double repCapital(String cid, String f, String t, String dateCol) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM repledge_billing " +
                "WHERE company_id=? AND " + dateCol + "::date BETWEEN ?::date AND ?::date",
                Double.class, cid, f, t);
        } catch (Exception e) { return 0; }
    }

    private double discount(String cid, String f, String t, String mat) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(discount_amount),0) FROM company_billing " +
                "WHERE company_id=? AND jewel_material_type::text=? " +
                "AND closing_date::date BETWEEN ?::date AND ?::date",
                Double.class, cid, mat, f, t);
        } catch (Exception e) { return 0; }
    }

    private double stockAmount(String cid, String mat) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM company_billing " +
                "WHERE company_id=? AND jewel_material_type::text=? AND status::text IN ('OPENED','LOCKED')",
                Double.class, cid, mat);
        } catch (Exception e) { return 0; }
    }

    private double cashInHand(String cid) {
        try {
            return jdbc.queryForObject(
                "SELECT COALESCE(todays_actual_amount,0) FROM company_todays_account " +
                "WHERE company_id=? AND ref_mark='L'",
                Double.class, cid);
        } catch (Exception e) { return 0; }
    }

    private Map<String, Object> incomeLine(String name, double amount) {
        return Map.of("name", name, "amount", amount);
    }
    private Map<String, Object> expLine(String name, double amount) {
        return Map.of("name", name, "amount", amount);
    }
    private Map<String, Object> assetLine(String name, double amount) {
        return Map.of("name", name, "amount", amount);
    }
    private Map<String, Object> liabLine(String name, double amount) {
        return Map.of("name", name, "amount", amount);
    }

    private double toDouble(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return 0; }
    }
    private long toLong(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (Exception e) { return 0; }
    }
}
