package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final JdbcTemplate jdbc;

    public CustomerController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /**
     * GET /api/customers/search?companyId=CMP1&query=name
     * Searches customers from company_billing records.
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String companyId,
            @RequestParam String query) {
        try {
            String like = "%" + query.toLowerCase() + "%";
            String sql;
            List<Map<String, Object>> rows;

            if (companyId != null && !companyId.isBlank()) {
                sql = """
                    SELECT DISTINCT customer_name,
                           MAX(spouse_type)      AS spouse_type,
                           MAX(spouse_name)      AS spouse_name,
                           MAX(mobile_number)    AS mobile_number,
                           MAX(door_number)      AS door_number,
                           MAX(street)           AS street,
                           MAX(area)             AS area,
                           MAX(city)             AS city,
                           COALESCE(MAX(customer_id),'') AS customer_id,
                           COUNT(CASE WHEN status::text IN ('OPENED','LOCKED') THEN 1 END) AS open_bills,
                           COUNT(*) AS total_bills
                    FROM company_billing
                    WHERE company_id = ?
                      AND (LOWER(customer_name) LIKE ? OR mobile_number LIKE ?)
                    GROUP BY customer_name
                    ORDER BY customer_name
                    LIMIT 50
                    """;
                rows = jdbc.queryForList(sql, companyId, like, like);
            } else {
                sql = """
                    SELECT DISTINCT customer_name,
                           MAX(spouse_type)      AS spouse_type,
                           MAX(spouse_name)      AS spouse_name,
                           MAX(mobile_number)    AS mobile_number,
                           MAX(area)             AS area,
                           MAX(city)             AS city,
                           COALESCE(MAX(customer_id),'') AS customer_id,
                           COUNT(CASE WHEN status::text IN ('OPENED','LOCKED') THEN 1 END) AS open_bills,
                           COUNT(*) AS total_bills
                    FROM company_billing
                    WHERE LOWER(customer_name) LIKE ? OR mobile_number LIKE ?
                    GROUP BY customer_name
                    ORDER BY customer_name
                    LIMIT 50
                    """;
                rows = jdbc.queryForList(sql, like, like);
            }
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            log.error("Customer search error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/customers/bills?companyId=CMP1&customerName=xyz
     * Returns all bills for a customer by name.
     */
    @GetMapping("/bills")
    public ResponseEntity<?> customerBills(
            @RequestParam String companyId,
            @RequestParam String customerName) {
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("""
                    SELECT bill_number, jewel_material_type::text material_type,
                           opening_date::text, closing_date::text,
                           amount, open_taken_amount, status::text status, items,
                           area, mobile_number
                    FROM company_billing
                    WHERE company_id = ? AND customer_name = ?
                    ORDER BY created_date DESC
                    """, companyId, customerName);
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            log.error("Customer bills error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
