package magizhchi.academy.pawnrestservice.api;

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

    public DashboardController(JdbcTemplate jdbc) {
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
        result.put("companyId", companyId);
        result.put("date",      targetDate);

        result.put("openedToday",    countBills(companyId, targetDate, "opening_date", "OPENED"));
        result.put("closedToday",    countBills(companyId, targetDate, "closing_date",  "CLOSED"));
        result.put("totalOpenBills", totalOpen(companyId));
        result.putAll(amountStats(companyId, targetDate));

        return ResponseEntity.ok(result);
    }

    private long countBills(long companyId, String date, String dateCol, String status) {
        String sql = "SELECT COUNT(*) FROM company_billing WHERE company_id = ? "
                   + "AND " + dateCol + "::date = ?::date AND status = ?";
        Long c = jdbc.queryForObject(sql, Long.class, companyId, date, status);
        return c != null ? c : 0L;
    }

    private long totalOpen(long companyId) {
        Long c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM company_billing WHERE company_id = ? AND status = 'OPENED'",
                Long.class, companyId);
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
}
