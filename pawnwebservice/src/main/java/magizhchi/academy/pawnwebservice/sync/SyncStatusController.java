package magizhchi.academy.pawnwebservice.sync;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncStatusController {

    private final DatabaseSyncService syncService;

    public SyncStatusController(DatabaseSyncService syncService) {
        this.syncService = syncService;
    }

    /** GET /api/sync/status — returns current sync state */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("syncing",         syncService.isSyncing());
        body.put("lastStatus",      syncService.getLastSyncStatus());
        body.put("lastTablesSynced", syncService.getLastTablesSynced());
        body.put("lastSyncTime",    syncService.getLastSyncTime() == null ? null
                : syncService.getLastSyncTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseEntity.ok(body);
    }

    /** POST /api/sync/trigger — manually kick off a sync right now */
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> trigger() {
        if (syncService.isSyncing()) {
            return ResponseEntity.ok(Map.of("message", "Sync already in progress."));
        }
        // Run in a background thread so the HTTP response returns immediately
        new Thread(syncService::runSync, "manual-sync").start();
        return ResponseEntity.ok(Map.of("message", "Sync triggered. Check /api/sync/status for progress."));
    }
}
