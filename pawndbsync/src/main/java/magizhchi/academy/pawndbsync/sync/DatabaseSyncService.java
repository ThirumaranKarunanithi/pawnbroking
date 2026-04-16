package magizhchi.academy.pawndbsync.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DatabaseSyncService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSyncService.class);

    @Value("${local.datasource.url}")
    private String localJdbcUrl;

    @Value("${local.datasource.username}")
    private String localUser;

    @Value("${local.datasource.password}")
    private String localPass;

    @Value("${cloud.datasource.url}")
    private String cloudJdbcUrl;

    @Value("${cloud.datasource.username}")
    private String cloudUser;

    @Value("${cloud.datasource.password}")
    private String cloudPass;

    private final JdbcTemplate cloudJdbc;
    private final DataSource   localDataSource;

    private volatile boolean syncing = false;
    private LocalDateTime lastSyncTime;
    private String lastSyncStatus   = "NOT_STARTED";
    private int    lastTablesSynced = 0;

    public DatabaseSyncService(
            @Qualifier("cloudJdbcTemplate") JdbcTemplate cloudJdbc,
            @Qualifier("localDataSource")   DataSource   localDataSource) {
        this.cloudJdbc       = cloudJdbc;
        this.localDataSource = localDataSource;
    }

    // ── Scheduler: every 5 minutes, first run 30 s after startup ─────────────

    @Scheduled(fixedDelay = 300_000, initialDelay = 30_000)
    public void scheduledSync() {
        if (syncing) { log.info("[SYNC] Already running — skipping."); return; }
        if (!isInternetAvailable()) {
            lastSyncStatus = "SKIPPED (no internet)";
            log.info("[SYNC] No internet — skipping.");
            return;
        }
        if (!isLocalDbAvailable()) {
            lastSyncStatus = "SKIPPED (local DB down)";
            log.info("[SYNC] Local DB unreachable — skipping.");
            return;
        }
        runSync();
    }

    // ── Core sync — also callable from REST /api/sync/trigger ─────────────────

    public synchronized void runSync() {
        syncing = true;
        lastSyncStatus   = "RUNNING";
        lastTablesSynced = 0;
        log.info("[SYNC] ===== Starting local → cloud sync (pg_dump) =====");

        try {
            pgDumpAndRestore();

            List<String> tables = cloudJdbc.queryForList(
                    "SELECT tablename FROM pg_tables WHERE schemaname = 'public'",
                    String.class);
            lastTablesSynced = tables.size();
            lastSyncTime     = LocalDateTime.now();
            lastSyncStatus   = "SUCCESS — " + lastTablesSynced + " table(s) at "
                    + lastSyncTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("[SYNC] ===== Sync complete: {} =====", lastSyncStatus);

        } catch (Exception e) {
            lastSyncStatus = "FAILED — " + e.getMessage();
            log.error("[SYNC] Sync failed", e);
        } finally {
            syncing = false;
        }
    }

    // ── pg_dump + psql ────────────────────────────────────────────────────────

    private void pgDumpAndRestore() throws Exception {
        String pgDump = findPgTool("pg_dump");
        String psql   = findPgTool("psql");
        String dump   = System.getProperty("java.io.tmpdir") + File.separator + "pawnbroking_sync.sql";

        // Parse local JDBC URL: jdbc:postgresql://host:port/dbname
        String localBody   = localJdbcUrl.replace("jdbc:postgresql://", "");
        String[] localParts    = localBody.split("/");
        String[] localHostPort = localParts[0].split(":");
        String   localHost     = localHostPort[0];
        String   localPort     = localHostPort.length > 1 ? localHostPort[1] : "5432";
        String   localDb       = localParts[1];

        // 1. Dump local pawnbroking DB
        log.info("[SYNC] pg_dump: {}:{}/{} → {}", localHost, localPort, localDb, dump);
        ProcessBuilder dumpPb = new ProcessBuilder(
                pgDump,
                "-h", localHost, "-p", localPort,
                "-U", localUser,
                "-d", localDb,
                "--no-owner", "--no-acl",
                "--clean", "--if-exists",
                "-f", dump);
        dumpPb.environment().put("PGPASSWORD", localPass);
        dumpPb.redirectErrorStream(true);

        Process dumpProc = dumpPb.start();
        String  dumpOut  = new String(dumpProc.getInputStream().readAllBytes());
        int     dumpExit = dumpProc.waitFor();

        if (dumpExit != 0)
            throw new RuntimeException("pg_dump failed (exit " + dumpExit + "): " + dumpOut);
        log.info("[SYNC] pg_dump complete. File size: {} bytes", new File(dump).length());

        // 2. Parse cloud JDBC URL
        String cloudBody    = cloudJdbcUrl.replace("jdbc:postgresql://", "");
        String[] cloudParts     = cloudBody.split("/");
        String[] cloudHostPort  = cloudParts[0].split(":");
        String   cloudHost      = cloudHostPort[0];
        String   cloudPort      = cloudHostPort.length > 1 ? cloudHostPort[1] : "5432";
        String   cloudDb        = cloudParts[1];

        // 3. Restore to Railway cloud
        log.info("[SYNC] psql: restoring to cloud {}:{}/{}", cloudHost, cloudPort, cloudDb);
        ProcessBuilder restorePb = new ProcessBuilder(
                psql,
                "-h", cloudHost, "-p", cloudPort,
                "-U", cloudUser,
                "-d", cloudDb,
                "-f", dump);
        restorePb.environment().put("PGPASSWORD", cloudPass);
        restorePb.environment().put("PGSSLMODE", "require");
        restorePb.redirectErrorStream(true);

        Process restoreProc = restorePb.start();
        String  restoreOut  = new String(restoreProc.getInputStream().readAllBytes());
        int     restoreExit = restoreProc.waitFor();

        log.info("[SYNC] psql restore exit={}", restoreExit);
        if (!restoreOut.isBlank())
            log.info("[SYNC] psql output:\n{}", restoreOut);

        if (restoreExit != 0)
            throw new RuntimeException("psql restore failed (exit " + restoreExit + ")");
    }

    private String findPgTool(String tool) {
        for (int v = 18; v >= 10; v--) {
            File f = new File("C:\\Program Files\\PostgreSQL\\" + v + "\\bin\\" + tool + ".exe");
            if (f.exists()) {
                log.debug("[SYNC] Found {}: {}", tool, f.getAbsolutePath());
                return f.getAbsolutePath();
            }
        }
        log.warn("[SYNC] {} not found in Program Files — using PATH", tool);
        return tool;
    }

    // ── Connectivity checks ───────────────────────────────────────────────────

    private boolean isInternetAvailable() {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean isLocalDbAvailable() {
        try (Connection c = localDataSource.getConnection()) {
            return c.isValid(3);
        } catch (Exception e) { return false; }
    }

    // ── Status accessors ──────────────────────────────────────────────────────

    public boolean       isSyncing()          { return syncing; }
    public LocalDateTime getLastSyncTime()     { return lastSyncTime; }
    public String        getLastSyncStatus()   { return lastSyncStatus; }
    public int           getLastTablesSynced() { return lastTablesSynced; }
}
