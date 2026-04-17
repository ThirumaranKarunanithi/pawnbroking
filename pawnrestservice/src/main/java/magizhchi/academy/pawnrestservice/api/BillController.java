package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);
    private final JdbcTemplate jdbc;

    public BillController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /**
     * GET /api/bills?companyId=CMP1&type=GOLD&status=OPENED&search=&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "ALL")    String type,
            @RequestParam(defaultValue = "OPENED") String status,
            @RequestParam(defaultValue = "")       String search,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "20")     int size) {
        try {
            List<Object> params = new ArrayList<>();
            StringBuilder where = new StringBuilder("WHERE company_id = ?");
            params.add(companyId);

            if (!"ALL".equalsIgnoreCase(type)) {
                where.append(" AND jewel_material_type::text = ?");
                params.add(type.toUpperCase());
            }
            if (!"ALL".equalsIgnoreCase(status)) {
                where.append(" AND status::text = ?");
                params.add(status.toUpperCase());
            }
            if (!search.isBlank()) {
                where.append(" AND (bill_number ILIKE ? OR customer_name ILIKE ? OR mobile_number ILIKE ?)");
                String like = "%" + search + "%";
                params.add(like); params.add(like); params.add(like);
            }

            Long total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM company_billing " + where, Long.class, params.toArray());

            List<Object> dataParams = new ArrayList<>(params);
            dataParams.add(size);
            dataParams.add((long) page * size);

            List<Map<String, Object>> bills = jdbc.queryForList("""
                    SELECT bill_number, jewel_material_type::text jewel_material_type,
                           customer_name, mobile_number, opening_date, accepted_closing_date,
                           amount, open_taken_amount, status::text status, items
                    FROM company_billing
                    """ + where + " ORDER BY created_date DESC LIMIT ? OFFSET ?",
                dataParams.toArray());

            return ResponseEntity.ok(Map.of(
                "bills", bills, "total", total != null ? total : 0L,
                "page", page, "size", size));
        } catch (Exception e) {
            log.error("Bills list error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/bills/{billNumber}?companyId=CMP1&type=GOLD */
    @GetMapping("/{billNumber}")
    public ResponseEntity<?> get(
            @PathVariable String billNumber,
            @RequestParam String companyId,
            @RequestParam(defaultValue = "GOLD") String type) {
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("""
                    SELECT b.*,
                           b.jewel_material_type::text                         AS material_type_str,
                           b.status::text                                       AS status_str,
                           to_char(b.opening_date,          'DD-MM-YYYY')       AS opening_date_str,
                           to_char(b.closing_date,          'DD-MM-YYYY')       AS closing_date_str,
                           to_char(b.accepted_closing_date, 'DD-MM-YYYY')       AS accepted_closing_date_str,
                           to_char(b.created_date,          'DD-MM-YYYY HH24:MI') AS created_date_str,
                           to_char(b.closed_date,           'DD-MM-YYYY HH24:MI') AS closed_date_str
                    FROM company_billing b
                    WHERE b.company_id = ? AND b.bill_number = ?
                      AND b.jewel_material_type::text = ?
                    ORDER BY b.created_date DESC LIMIT 1
                    """, companyId, billNumber, type.toUpperCase());
            if (rows.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(rows.get(0));
        } catch (Exception e) {
            log.error("Bill detail error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
