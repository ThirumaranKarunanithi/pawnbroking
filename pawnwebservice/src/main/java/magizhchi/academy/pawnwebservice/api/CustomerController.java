package magizhchi.academy.pawnwebservice.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final JdbcTemplate jdbc;

    public CustomerController(@Qualifier("cloudJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** GET /api/customers/search?query= — search by name or mobile */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {

        String sql = """
                SELECT id, name, mobile_number, door_number, street, area, city, status
                FROM customer_details
                WHERE name ILIKE ? OR mobile_number ILIKE ?
                ORDER BY name
                LIMIT ?
                """;
        String q = "%" + query + "%";
        return ResponseEntity.ok(jdbc.queryForList(sql, q, q, limit));
    }

    /** GET /api/customers/{id} — single customer details */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        String sql = """
                SELECT id, name, mobile_number, mobile_number_2,
                       door_number, street, area, city, state,
                       id_proof_type, id_proof_number, status
                FROM customer_details
                WHERE id = ?
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }
}
