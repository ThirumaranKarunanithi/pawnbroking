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
    @GetMapping("/monthly")
    public ResponseEntity<?> monthly(@RequestParam String companyId) {
        try {
            String sql = """
                    SELECT mon, yr,
                        COALESCE(open_count, 0) open_count,
                        COALESCE(open_amount, 0) open_amount,
                        COALESCE(close_count, 0) close_count,
                        COALESCE(close_amount, 0) close_amount,
                        COALESCE(stock_count, 0) stock_count,
                        COALESCE(stock_amount, 0) stock_amount
                    FROM (
                        SELECT TO_CHAR(opening_date, 'Mon') mon,
                               EXTRACT(YEAR FROM opening_date)::int yr,
                               EXTRACT(MONTH FROM opening_date)::int mon_num,
                               COUNT(*) open_count, COALESCE(SUM(amount),0) open_amount,
                               0 close_count, 0 close_amount, 0 stock_count, 0 stock_amount
                        FROM company_billing
                        WHERE company_id = ? AND status::text NOT IN ('CANCELED')
                        GROUP BY TO_CHAR(opening_date,'Mon'), EXTRACT(YEAR FROM opening_date),
                                 EXTRACT(MONTH FROM opening_date)
                        UNION ALL
                        SELECT TO_CHAR(closing_date,'Mon'), EXTRACT(YEAR FROM closing_date)::int,
                               EXTRACT(MONTH FROM closing_date)::int,
                               0, 0, COUNT(*), COALESCE(SUM(got_amount),0), 0, 0
                        FROM company_billing
                        WHERE company_id = ? AND closing_date IS NOT NULL
                        GROUP BY TO_CHAR(closing_date,'Mon'), EXTRACT(YEAR FROM closing_date),
                                 EXTRACT(MONTH FROM closing_date)
                        UNION ALL
                        SELECT TO_CHAR(opening_date,'Mon'), EXTRACT(YEAR FROM opening_date)::int,
                               EXTRACT(MONTH FROM opening_date)::int,
                               0, 0, 0, 0, COUNT(*), COALESCE(SUM(amount),0)
                        FROM company_billing
                        WHERE company_id = ? AND status::text IN ('OPENED','LOCKED')
                        GROUP BY TO_CHAR(opening_date,'Mon'), EXTRACT(YEAR FROM opening_date),
                                 EXTRACT(MONTH FROM opening_date)
                    ) t
                    GROUP BY mon, yr, mon_num
                    ORDER BY yr DESC, mon_num DESC
                    """;
            List<Map<String, Object>> raw = jdbc.queryForList(sql, companyId, companyId, companyId);

            // Merge rows by mon+yr
            Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
            for (Map<String, Object> r : raw) {
                String key = r.get("mon") + "-" + r.get("yr");
                Map<String, Object> m = merged.computeIfAbsent(key, k -> {
                    Map<String, Object> nm = new LinkedHashMap<>();
                    nm.put("month", r.get("mon") + "-" + r.get("yr"));
                    nm.put("openCount",   0L); nm.put("openAmount",   0.0);
                    nm.put("closeCount",  0L); nm.put("closeAmount",  0.0);
                    nm.put("stockCount",  0L); nm.put("stockAmount",  0.0);
                    return nm;
                });
                m.put("openCount",  toLong(m.get("openCount"))  + toLong(r.get("open_count")));
                m.put("openAmount", toDouble(m.get("openAmount"))+ toDouble(r.get("open_amount")));
                m.put("closeCount", toLong(m.get("closeCount")) + toLong(r.get("close_count")));
                m.put("closeAmount",toDouble(m.get("closeAmount"))+toDouble(r.get("close_amount")));
                m.put("stockCount", toLong(m.get("stockCount")) + toLong(r.get("stock_count")));
                m.put("stockAmount",toDouble(m.get("stockAmount"))+toDouble(r.get("stock_amount")));
            }

            List<Map<String, Object>> months = new ArrayList<>(merged.values());
            // Add earned columns
            for (Map<String, Object> m : months) {
                m.put("earnedCount",  toLong(m.get("openCount"))   - toLong(m.get("closeCount")));
                m.put("earnedAmount", toDouble(m.get("openAmount"))- toDouble(m.get("closeAmount")));
            }

            return ResponseEntity.ok(Map.of("months", months, "total", months.size()));
        } catch (Exception e) {
            log.error("Monthly report error: {}", e.getMessage(), e);
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
