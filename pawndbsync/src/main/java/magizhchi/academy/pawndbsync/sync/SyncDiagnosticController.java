package magizhchi.academy.pawndbsync.sync;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncDiagnosticController {

    @Value("${local.datasource.url}")
    private String localUrl;

    @Value("${local.datasource.username}")
    private String localUser;

    @Value("${local.datasource.password}")
    private String localPass;

    private final JdbcTemplate cloudJdbc;

    public SyncDiagnosticController(
            @Qualifier("cloudJdbcTemplate") JdbcTemplate cloudJdbc) {
        this.cloudJdbc = cloudJdbc;
    }

    /** GET /api/sync/diagnose — connectivity + table counts */
    @GetMapping("/diagnose")
    public Map<String, Object> diagnose() {
        Map<String, Object> report = new LinkedHashMap<>();

        // Internet
        report.put("internet_reachable", checkInternet());

        // Local DB
        String localPing = pingLocalDb();
        report.put("local_db_ping", localPing);
        if (localPing.startsWith("OK")) {
            try (Connection c = DriverManager.getConnection(localUrl, localUser, localPass)) {
                var rs = c.createStatement().executeQuery(
                        "SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename");
                List<String> tables = new ArrayList<>();
                while (rs.next()) tables.add(rs.getString(1));
                report.put("local_db_tables_count", tables.size());
                report.put("local_db_tables", tables);
            } catch (Exception e) {
                report.put("local_db_tables_error", e.getMessage());
            }
        }

        // Cloud DB
        String cloudPing = pingCloudDb();
        report.put("cloud_db_ping", cloudPing);
        if (cloudPing.startsWith("OK")) {
            try {
                List<String> tables = cloudJdbc.queryForList(
                        "SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename",
                        String.class);
                report.put("cloud_db_tables_count", tables.size());
                report.put("cloud_db_tables", tables);
            } catch (Exception e) {
                report.put("cloud_db_tables_error", e.getMessage());
            }
        }

        return report;
    }

    private boolean checkInternet() {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            return true;
        } catch (Exception e) { return false; }
    }

    private String pingLocalDb() {
        try (Connection c = DriverManager.getConnection(localUrl, localUser, localPass)) {
            return c.isValid(3) ? "OK" : "FAIL — connection invalid";
        } catch (Exception e) {
            return "FAIL — " + e.getMessage();
        }
    }

    private String pingCloudDb() {
        try {
            cloudJdbc.queryForObject("SELECT 1", Integer.class);
            return "OK";
        } catch (Exception e) {
            return "FAIL — " + e.getMessage();
        }
    }
}
