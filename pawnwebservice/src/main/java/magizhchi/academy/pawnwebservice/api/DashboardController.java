package magizhchi.academy.pawnwebservice.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbc;

    public DashboardController(@Qualifier("cloudJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * GET /api/dashboard?companyId=&date=yyyy-MM-dd
     * Returns today's key statistics for the given company.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> dashboard(
            @RequestParam long companyId,
            @RequestParam(required = false) String date) {

        String targetDate = (date != null && !date.isBlank()) ? date : LocalDate.now().toString();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("companyId",  companyId);
        result.put("date",       targetDate);

        // Bills opened today
        result.put("openedToday", countBills(companyId, targetDate, "opening_date", "OPENED"));

        // Bills closed today
        result.put("closedToday", countBills(companyId, targetDate, "closing_date", "CLOSED"));

        // Total opened bills (active)
        result.put("totalOpenBills", totalOpen(companyId, "OPENED"));

        // Amount stats for today
        result.putAll(amountStats(companyId, targetDate));

        // Today's account summary
        result.put("goldAccount",   accountSummary(companyId, targetDate, "GOLD"));
        result.put("silverAccount", accountSummary(companyId, targetDate, "SILVER"));

        return ResponseEntity.ok(result);
    }

    private long countBills(long companyId, String date, String dateCol, String status) {
        String sql = "SELECT COUNT(*) FROM company_billing WHERE company_id = ? "
                   + "AND " + dateCol + "::date = ?::date AND status = ?";
        Long c = jdbc.queryForObject(sql, Long.class, companyId, date, status);
        return c != null ? c : 0L;
    }

    private long totalOpen(long companyId, String status) {
        Long c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM company_billing WHERE company_id = ? AND status = ?",
                Long.class, companyId, status);
        return c != null ? c : 0L;
    }

    private Map<String, Object> amountStats(long companyId, String date) {
        String sql = """
                SELECT
                  COALESCE(SUM(CASE WHEN opening_date::date = ?::date THEN open_taken_amount ELSE 0 END), 0) AS given_today,
                  COALESCE(SUM(CASE WHEN closing_date::date = ?::date AND status IN ('CLOSED','DELIVERED')
                               THEN got_amount ELSE 0 END), 0) AS received_today,
                  COALESCE(SUM(CASE WHEN status = 'OPENED' THEN amount ELSE 0 END), 0) AS total_loan_amount
                FROM company_billing
                WHERE company_id = ?
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, date, date, companyId);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    private Map<String, Object> accountSummary(long companyId, String date, String type) {
        String sql = """
                SELECT todays_actual_amount, todays_available_amount, todays_deficit_amount
                FROM company_todays_account
                WHERE company_id = ? AND todays_date::date = ?::date
                  AND ref_mark = 'L'
                LIMIT 1
                """;
        // ref_mark stores material type in some schemas; try with type filter too
        String sqlWithType = """
                SELECT todays_actual_amount, todays_available_amount, todays_deficit_amount
                FROM company_todays_account
                WHERE company_id = ? AND todays_date::date = ?::date
                LIMIT 1
                """;
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(sqlWithType, companyId, date);
            return rows.isEmpty() ? Map.of() : rows.get(0);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
