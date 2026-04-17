package magizhchi.academy.pawndbsync.sync;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileSyncController {

    private final FileSyncService fileSyncService;

    public FileSyncController(FileSyncService fileSyncService) {
        this.fileSyncService = fileSyncService;
    }

    /** GET /api/files/status */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("syncing",        fileSyncService.isSyncing());
        body.put("lastStatus",     fileSyncService.getLastSyncStatus());
        body.put("filesUploaded",  fileSyncService.getLastFilesUploaded());
        body.put("filesSkipped",   fileSyncService.getLastFilesSkipped());
        body.put("filesFailed",    fileSyncService.getLastFilesFailed());
        body.put("cameraFolder",   fileSyncService.getLastCameraFolder());
        body.put("lastSyncTime",   fileSyncService.getLastSyncTime() == null ? null
                : fileSyncService.getLastSyncTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseEntity.ok(body);
    }

    /** POST /api/files/trigger — manually kick off a file sync */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> trigger() {
        if (fileSyncService.isSyncing()) {
            return ResponseEntity.ok(Map.of("message", "File sync already in progress."));
        }
        new Thread(fileSyncService::runSync, "manual-file-sync").start();
        return ResponseEntity.ok(Map.of("message",
                "File sync triggered. Check /api/files/status for progress."));
    }

    /**
     * GET /api/files/diagnose
     * Checks every layer — S3 connectivity, DB value, folder existence, file count.
     * Open this in a browser to find out exactly what is failing.
     */
    @GetMapping("/diagnose")
    public ResponseEntity<Map<String, Object>> diagnose() {
        Map<String, Object> report = new LinkedHashMap<>();

        // 1. AWS config (masked secret)
        report.put("aws_region", fileSyncService.getRegion());
        report.put("aws_bucket", fileSyncService.getBucket());
        report.put("aws_prefix", fileSyncService.getPrefix());

        // 2. S3 bucket reachability
        String s3Check = fileSyncService.diagnoseS3();
        report.put("s3_bucket_check", s3Check);

        // 3. DB value of camera_temp_file_name
        String cameraPath = fileSyncService.diagnoseCameraFolder();
        report.put("db_camera_temp_file_name", cameraPath);

        // 4. Does the folder exist and how many files are in it?
        if (!cameraPath.startsWith("DB ERROR") && !cameraPath.startsWith("NO ROWS")) {
            String trimmed = cameraPath.trim();
            Path base = Paths.get(trimmed);
            report.put("folder_exists",      Files.exists(base));
            report.put("folder_is_directory", Files.isDirectory(base));
            if (Files.isDirectory(base)) {
                try {
                    long count = Files.walk(base).filter(Files::isRegularFile).count();
                    report.put("folder_file_count", count);
                } catch (Exception e) {
                    report.put("folder_scan_error", e.getMessage());
                }
            }
        }

        // 5. Startup init error (if any)
        report.put("init_error", fileSyncService.getInitError());

        // 6. Last sync summary
        report.put("last_sync_status",    fileSyncService.getLastSyncStatus());
        report.put("last_files_uploaded", fileSyncService.getLastFilesUploaded());
        report.put("last_files_skipped",  fileSyncService.getLastFilesSkipped());
        report.put("last_files_failed",   fileSyncService.getLastFilesFailed());

        return ResponseEntity.ok(report);
    }
}
