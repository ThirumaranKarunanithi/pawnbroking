package magizhchi.academy.pawnrestservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JdbcTemplate jdbc;

    public AuthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        String sql = """
                SELECT u.id, u.user_name, u.user_password, u.salt_value,
                       e.name AS emp_name, r.id AS role_id, r.role_name
                FROM user_master u
                JOIN employee e ON u.emp_id = e.id
                JOIN role_master r ON u.role_id = r.id
                WHERE u.user_name = ?
                  AND u.status = 'ACTIVE'
                  AND e.status = 'ACTIVE'
                  AND r.status = 'ACTIVE'
                """;

        List<Map<String, Object>> rows = jdbc.queryForList(sql, username);

        if (rows.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or inactive account"));
        }

        Map<String, Object> user = rows.get(0);
        String storedHash = (String) user.get("user_password");
        String salt       = (String) user.get("salt_value");

        if (!verifyPassword(password, storedHash, salt)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId",       user.get("id"));
        response.put("userName",     user.get("user_name"));
        response.put("employeeName", user.get("emp_name"));
        response.put("roleId",       user.get("role_id"));
        response.put("roleName",     user.get("role_name"));
        String token = Base64.getEncoder().encodeToString(
                (user.get("id") + ":" + username + ":" + System.currentTimeMillis()).getBytes());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    /** PBKDF2WithHmacSHA1, 10000 iterations, 256-bit key — same as desktop app */
    private boolean verifyPassword(String provided, String stored, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(provided.toCharArray(), salt.getBytes(), 10000, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            Arrays.fill(provided.toCharArray(), Character.MIN_VALUE);
            spec.clearPassword();
            String generated = Base64.getEncoder().encodeToString(hash);
            return generated.equalsIgnoreCase(stored);
        } catch (Exception e) {
            return false;
        }
    }
}
