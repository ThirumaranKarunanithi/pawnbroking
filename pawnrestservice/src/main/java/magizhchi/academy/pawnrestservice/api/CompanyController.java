package magizhchi.academy.pawnrestservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final JdbcTemplate jdbc;

    public CompanyController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** GET /api/companies — list all active companies */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        String sql = """
                SELECT id, name, city, area, mobile_number, type,
                       day_or_monthly_interest, status
                FROM company
                WHERE status = 'ACTIVE'
                ORDER BY name
                """;
        return ResponseEntity.ok(jdbc.queryForList(sql));
    }

    /** GET /api/companies/{id} — single company details */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        String sql = """
                SELECT id, name, door_number, street, area, city, state,
                       lc_holder_name, lc_number, mobile_number, landline_number,
                       day_or_monthly_interest, status, note, type, entry_mode
                FROM company
                WHERE id = ?
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id);
        if (rows.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rows.get(0));
    }
}
