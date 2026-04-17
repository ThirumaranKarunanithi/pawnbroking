package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final JdbcTemplate jdbc;

    public DashboardController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /** GET /api/dashboard?companyId=CMP1&date=yyyy-MM-dd */
    @GetMapping
    public ResponseEntity<?> dashboard(
            @RequestParam String companyId,
            @RequestParam(required = false) String date) {
        try {
            String targetDate = (date != null && !date.isBlank()) ? date : LocalDate.now().toString();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("companyId", companyId);
            result.put("date",      targetDate);
            result.put("openedToday",    countBills(companyId, targetDate, "opening_date", "OPENED"));
            result.put("closedToday",    countBills(companyId, targetDate, "closing_date",  "CLOSED"));
            result.put("totalOpenBills", totalOpen(companyId));
            result.putAll(amountStats(companyId, targetDate));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Dashboard error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private long countBills(String companyId, String date, String dateCol, String status) {
        try {
            Long c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM company_billing WHERE company_id = ? " +
                "AND " + dateCol + "::date = ?::date AND status::text = ?",
                Long.class, companyId, date, status);
            return c != null ? c : 0L;
        } catch (Exception e) { return 0L; }
    }

    private long totalOpen(String companyId) {
        try {
            Long c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM company_billing WHERE company_id = ? AND status::text IN ('OPENED','LOCKED')",
                Long.class, companyId);
            return c != null ? c : 0L;
        } catch (Exception e) { return 0L; }
    }

    private Map<String, Object> amountStats(String companyId, String date) {
        try {
            String sql = """
                    SELECT
                      COALESCE(SUM(CASE WHEN opening_date::date = ?::date THEN amount ELSE 0 END), 0) AS given_today,
                      COALESCE(SUM(CASE WHEN closing_date::date = ?::date AND status::text IN ('CLOSED','DELIVERED')
                                   THEN got_amount ELSE 0 END), 0) AS received_today,
                      COALESCE(SUM(CASE WHEN status::text IN ('OPENED','LOCKED') THEN amount ELSE 0 END), 0) AS total_loan_amount
                    FROM company_billing
                    WHERE company_id = ?
                    """;
            List<Map<String, Object>> rows = jdbc.queryForList(sql, date, date, companyId);
            return rows.isEmpty() ? Map.of("given_today", 0, "received_today", 0, "total_loan_amount", 0)
                                  : rows.get(0);
        } catch (Exception e) {
            return Map.of("given_today", 0, "received_today", 0, "total_loan_amount", 0);
        }
    }
}
