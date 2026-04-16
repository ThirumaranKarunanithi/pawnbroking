package magizhchi.academy.pawnrestservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final JdbcTemplate jdbc;

    public CustomerController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** GET /api/customers/search?query= — search by name or mobile */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String query) {

        String sql = """
                SELECT id, customer_name, mobile_number, mobile_number_2,
                       door_number, street, area, city, status
                FROM customer_details
                WHERE status = 'ACTIVE'
                  AND (customer_name ILIKE ? OR mobile_number ILIKE ? OR mobile_number_2 ILIKE ?)
                ORDER BY customer_name
                LIMIT 50
                """;
        String q = "%" + query + "%";
        return ResponseEntity.ok(jdbc.queryForList(sql, q, q, q));
    }

    /** GET /api/customers/{id} — single customer details */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        String sql = """
                SELECT id, customer_name, gender, spouse_type, spouse_name,
                       door_number, street, area, city, mobile_number, mobile_number_2,
                       nominee_name, id_proof_type, id_proof_number, occupation, status
                FROM customer_details
                WHERE id = ?
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }
}
