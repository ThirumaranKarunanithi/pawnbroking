package magizhchi.academy.pawnrestservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final JdbcTemplate jdbc;

    public BillController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * GET /api/bills
     * Query params: companyId (required), type (GOLD/SILVER/ALL), status (OPENED/CLOSED/ALL),
     *               search (bill number or customer name/mobile), page, size
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam long companyId,
            @RequestParam(defaultValue = "ALL")    String type,
            @RequestParam(defaultValue = "OPENED") String status,
            @RequestParam(defaultValue = "")       String search,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "20")     int size) {

        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder("WHERE company_id = ?");
        params.add(companyId);

        if (!"ALL".equalsIgnoreCase(type)) {
            where.append(" AND jewel_material_type = ?");
            params.add(type);
        }
        if (!"ALL".equalsIgnoreCase(status)) {
            where.append(" AND status = ?");
            params.add(status);
        }
        if (!search.isBlank()) {
            where.append(" AND (bill_number ILIKE ? OR customer_name ILIKE ? OR mobile_number ILIKE ?)");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        String countSql = "SELECT COUNT(*) FROM company_billing " + where;
        Long total = jdbc.queryForObject(countSql, Long.class, params.toArray());

        String dataSql = """
                SELECT bill_number, jewel_material_type, customer_name,
                       mobile_number, opening_date, accepted_closing_date,
                       amount, open_taken_amount, status, items
                FROM company_billing
                """ + where + " ORDER BY created_date DESC LIMIT ? OFFSET ?";

        params.add(size);
        params.add((long) page * size);

        List<Map<String, Object>> bills = jdbc.queryForList(dataSql, params.toArray());

        return ResponseEntity.ok(Map.of(
                "bills", bills,
                "total", total != null ? total : 0L,
                "page",  page,
                "size",  size
        ));
    }

    /** GET /api/bills/{billNumber}?companyId=&type= — full bill details */
    @GetMapping("/{billNumber}")
    public ResponseEntity<?> get(
            @PathVariable String billNumber,
            @RequestParam long companyId,
            @RequestParam(defaultValue = "GOLD") String type) {

        String sql = """
                SELECT b.*,
                       to_char(b.opening_date,          'DD-MM-YYYY')       AS opening_date_str,
                       to_char(b.closing_date,          'DD-MM-YYYY')       AS closing_date_str,
                       to_char(b.accepted_closing_date, 'DD-MM-YYYY')       AS accepted_closing_date_str,
                       to_char(b.created_date,          'DD-MM-YYYY HH24:MI') AS created_date_str,
                       to_char(b.closed_date,           'DD-MM-YYYY HH24:MI') AS closed_date_str
                FROM company_billing b
                WHERE b.company_id = ?
                  AND b.bill_number = ?
                  AND b.jewel_material_type = ?
                ORDER BY b.created_date DESC
                LIMIT 1
                """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql, companyId, billNumber, type);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }

    /** GET /api/bills/customer/{customerId}?companyId= — bills for a customer */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> byCustomer(
            @PathVariable long customerId,
            @RequestParam long companyId) {

        String sql = """
                SELECT bill_number, jewel_material_type, opening_date,
                       amount, status, items
                FROM company_billing
                WHERE company_id = ? AND customer_id = ?
                ORDER BY created_date DESC
                """;
        return ResponseEntity.ok(jdbc.queryForList(sql, companyId, customerId));
    }
}
