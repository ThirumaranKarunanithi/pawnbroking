package magizhchi.academy.pawndbsync.sync;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSyncService {

    private static final Logger log = LoggerFactory.getLogger(FileSyncService.class);

    // ── AWS config ────────────────────────────────────────────────────────────

    @Value("${aws.accessKey}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.prefix:alwarpuram}")
    private String prefix;

    // ── Local DB ──────────────────────────────────────────────────────────────

    private final JdbcTemplate localJdbc;

    // ── State ─────────────────────────────────────────────────────────────────

    private S3Client s3Client;
    private volatile boolean syncing      = false;
    private LocalDateTime    lastSyncTime;
    private String  lastSyncStatus        = "NOT_STARTED";
    private int     lastFilesUploaded     = 0;
    private int     lastFilesSkipped      = 0;
    private int     lastFilesFailed       = 0;
    private String  lastCameraFolder      = "—";
    private String  initError             = null;

    public FileSyncService(
            @Qualifier("localJdbcTemplate") JdbcTemplate localJdbc) {
        this.localJdbc = localJdbc;
    }

    // ── Build S3 client on startup and verify bucket access ──────────────────

    @PostConstruct
    void init() {
        buildS3Client();
    }

    /**
     * Builds the S3 client and verifies bucket access.
     * A 403 just means IAM permissions are missing — we log a warning but
     * do NOT treat it as fatal so the scheduler can still try later.
     */
    private boolean buildS3Client() {
        String maskedKey = accessKeyId != null && accessKeyId.length() > 8
                ? accessKeyId.substring(0, 4) + "****" + accessKeyId.substring(accessKeyId.length() - 4)
                : "(empty)";
        log.info("[FILE-SYNC] Initialising — region='{}' bucket='{}' prefix='{}' accessKey='{}'",
                region, bucket, prefix, maskedKey);
        try {
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                    .build();

            // Probe the bucket — 403 means "exists but no ListBucket perm",
            // which is fine; uploads only need PutObject/HeadObject.
            try {
                s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
                log.info("[FILE-SYNC] S3 client ready — bucket='{}' is reachable.", bucket);
            } catch (S3Exception e) {
                if (e.statusCode() == 403) {
                    // Credentials work, bucket exists — missing s3:ListBucket only.
                    // Uploads (PutObject) may still succeed if PutObject is granted.
                    log.warn("[FILE-SYNC] HeadBucket returned 403 — bucket exists but " +
                             "s3:ListBucket not granted. Will attempt uploads anyway. " +
                             "Add s3:ListBucket + s3:PutObject + s3:HeadObject to IAM policy.");
                } else if (e.statusCode() == 404) {
                    initError = "Bucket '" + bucket + "' does not exist in region '" + region + "'";
                    log.error("[FILE-SYNC] {}", initError);
                    return false;
                } else {
                    initError = "S3 bucket check failed: HTTP " + e.statusCode()
                            + " — " + e.awsErrorDetails().errorMessage();
                    log.error("[FILE-SYNC] {}", initError);
                    return false;
                }
            }

            initError = null;
            return true;

        } catch (Exception e) {
            initError = "S3 init failed: " + e.getMessage();
            log.error("[FILE-SYNC] {}", initError, e);
            return false;
        }
    }

    @PreDestroy
    void destroy() {
        if (s3Client != null) s3Client.close();
    }

    // ── Scheduler: every 5 minutes, first run 90 s after startup ─────────────

    @Scheduled(fixedDelay = 300_000, initialDelay = 90_000)
    public void scheduledSync() {
        if (syncing) {
            log.info("[FILE-SYNC] Already running — skipping.");
            return;
        }
        runSync();
    }

    // ── Core sync — also callable from REST /api/files/trigger ───────────────

    public synchronized void runSync() {

        if (s3Client == null || initError != null) {
            log.warn("[FILE-SYNC] S3 not ready ({}), retrying init...", initError);
            if (!buildS3Client()) {
                lastSyncStatus = "FAILED — S3 not reachable: " + initError;
                return;
            }
        }

        syncing           = true;
        lastSyncStatus    = "RUNNING";
        lastFilesUploaded = 0;
        lastFilesSkipped  = 0;
        lastFilesFailed   = 0;
        log.info("[FILE-SYNC] ===== Starting local → S3 file sync =====");

        try {
            // 1. Read camera folder path from local DB
            List<String> paths = readCameraFolderPaths();

            if (paths.isEmpty()) {
                lastSyncStatus = "SKIPPED — camera_temp_file_name is empty or not set in DB";
                log.warn("[FILE-SYNC] {}", lastSyncStatus);
                return;
            }

            // 2. Sync each folder row
            for (String rawPath : paths) {
                String folderPath = rawPath.trim();
                if (!folderPath.isEmpty()) {
                    lastCameraFolder = folderPath;
                    syncFolder(folderPath);
                }
            }

            lastSyncTime   = LocalDateTime.now();
            lastSyncStatus = String.format("SUCCESS — %d uploaded, %d skipped, %d failed  [%s]",
                    lastFilesUploaded, lastFilesSkipped, lastFilesFailed,
                    lastSyncTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log.info("[FILE-SYNC] ===== Sync complete: {} =====", lastSyncStatus);

        } catch (Exception e) {
            lastSyncStatus = "FAILED — " + e.getMessage();
            log.error("[FILE-SYNC] Sync failed", e);
        } finally {
            syncing = false;
        }
    }

    // ── Read folder path from company_other_settings ──────────────────────────

    private List<String> readCameraFolderPaths() {
        try {
            List<String> paths = localJdbc.queryForList(
                    "SELECT camera_temp_file_name FROM company_other_settings " +
                    "WHERE camera_temp_file_name IS NOT NULL AND camera_temp_file_name <> ''",
                    String.class);
            log.info("[FILE-SYNC] camera_temp_file_name rows from DB: {}", paths);
            return paths;
        } catch (DataAccessException e) {
            log.error("[FILE-SYNC] Cannot read company_other_settings: {}", e.getMessage());
            return List.of();
        }
    }

    // ── Walk local folder and upload changed files ────────────────────────────

    private void syncFolder(String folderPath) {
        Path base = Paths.get(folderPath);

        if (!Files.exists(base)) {
            log.warn("[FILE-SYNC] Folder does not exist — skipping: '{}'", folderPath);
            lastFilesFailed++;
            return;
        }
        if (!Files.isDirectory(base)) {
            log.warn("[FILE-SYNC] Path is not a directory — skipping: '{}'", folderPath);
            lastFilesFailed++;
            return;
        }

        log.info("[FILE-SYNC] Scanning folder: {}", base);

        try (Stream<Path> stream = Files.walk(base)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .toList();

            log.info("[FILE-SYNC] Found {} file(s) under {}", files.size(), base);

            for (Path file : files) {
                // Build S3 key preserving the relative folder structure
                String relative = base.relativize(file).toString().replace("\\", "/");
                String s3Key    = prefix + "/" + relative;
                uploadIfChanged(file, s3Key);
            }

        } catch (IOException e) {
            log.error("[FILE-SYNC] Error walking folder '{}': {}", folderPath, e.getMessage());
            lastFilesFailed++;
        }
    }

    // ── Upload one file — skip if S3 already has same size ───────────────────

    private void uploadIfChanged(Path localFile, String s3Key) {
        try {
            long localSize = Files.size(localFile);

            // --- Check if object already exists on S3 with the same size ---
            // NOTE: AWS SDK v2 headObject throws a plain S3Exception (HTTP 404)
            //       for missing keys — NOT always NoSuchKeyException — so we
            //       check the status code explicitly.
            try {
                HeadObjectResponse head = s3Client.headObject(
                        HeadObjectRequest.builder()
                                .bucket(bucket)
                                .key(s3Key)
                                .build());

                if (head.contentLength() != null && head.contentLength() == localSize) {
                    lastFilesSkipped++;
                    log.debug("[FILE-SYNC] Unchanged — skip: {}", s3Key);
                    return;
                }
                log.info("[FILE-SYNC] Size changed (S3={} local={}) — re-uploading: {}",
                        head.contentLength(), localSize, s3Key);

            } catch (S3Exception e) {
                if (e.statusCode() == 404) {
                    // Object not on S3 yet — proceed to upload
                    log.debug("[FILE-SYNC] New file — uploading: {}", s3Key);
                } else {
                    // 403 = no GetObject perm yet, other = transient error.
                    // Don't skip — attempt PutObject anyway; it may still succeed.
                    log.debug("[FILE-SYNC] HeadObject HTTP {} for '{}' — attempting upload anyway.",
                            e.statusCode(), s3Key);
                }
            }

            // --- Upload ---
            log.info("[FILE-SYNC] PUT  {} → s3://{}/{}", localFile.getFileName(), bucket, s3Key);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Key)
                            .contentLength(localSize)
                            .build(),
                    RequestBody.fromFile(localFile));

            lastFilesUploaded++;
            log.info("[FILE-SYNC] OK   {} ({} bytes)", s3Key, localSize);

        } catch (S3Exception e) {
            log.error("[FILE-SYNC] S3 upload failed for '{}': HTTP {} — {}",
                    s3Key, e.statusCode(), e.awsErrorDetails().errorMessage());
            lastFilesFailed++;
        } catch (IOException e) {
            log.error("[FILE-SYNC] IO error reading '{}': {}", localFile, e.getMessage());
            lastFilesFailed++;
        }
    }

    // ── Diagnostic helpers (used by FileSyncController /diagnose) ────────────

    public String diagnoseCameraFolder() {
        try {
            List<String> paths = localJdbc.queryForList(
                    "SELECT camera_temp_file_name FROM company_other_settings",
                    String.class);
            if (paths.isEmpty()) return "NO ROWS in company_other_settings";
            return String.join(", ", paths);
        } catch (Exception e) {
            return "DB ERROR: " + e.getMessage();
        }
    }

    public String diagnoseS3() {
        if (s3Client == null) return "S3 client not initialised";
        if (initError  != null) return "INIT ERROR: " + initError;
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            return "OK — bucket '" + bucket + "' is reachable in region '" + region + "'";
        } catch (S3Exception e) {
            return "FAIL — HTTP " + e.statusCode() + ": " + e.awsErrorDetails().errorMessage();
        } catch (Exception e) {
            return "FAIL — " + e.getMessage();
        }
    }

    // ── Status accessors ──────────────────────────────────────────────────────

    public boolean       isSyncing()           { return syncing; }
    public LocalDateTime getLastSyncTime()      { return lastSyncTime; }
    public String        getLastSyncStatus()    { return lastSyncStatus; }
    public int           getLastFilesUploaded() { return lastFilesUploaded; }
    public int           getLastFilesSkipped()  { return lastFilesSkipped; }
    public int           getLastFilesFailed()   { return lastFilesFailed; }
    public String        getLastCameraFolder()  { return lastCameraFolder; }
    public String        getInitError()         { return initError; }
    public String        getBucket()            { return bucket; }
    public String        getPrefix()            { return prefix; }
    public String        getRegion()            { return region; }
}
