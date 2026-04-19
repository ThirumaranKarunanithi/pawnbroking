package magizhchi.academy.pawnrestservice.api;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Proxies bill images stored in S3 back to the Android client.
 * The Android app has no AWS credentials — it calls this endpoint instead
 * of accessing S3 directly.
 *
 * GET /api/bills/image?companyId=X&materialType=GOLD&billNumber=ALW/001&imageName=open_customer.png
 */
@RestController
@RequestMapping("/api/bills")
@CrossOrigin
public class BillImageController {

    private static final Logger log = LoggerFactory.getLogger(BillImageController.class);

    @Value("${aws.accessKey:}")
    private String accessKeyId;

    @Value("${aws.secretKey:}")
    private String secretAccessKey;

    @Value("${aws.region:eu-north-1}")
    private String region;

    @Value("${aws.s3.bucket:pawnbroking}")
    private String bucket;

    @Value("${aws.s3.prefix:alwarpuram}")
    private String prefix;

    private S3Client s3Client;
    private String   initError = null;

    @PostConstruct
    void init() {
        if (accessKeyId == null || accessKeyId.isBlank() ||
            secretAccessKey == null || secretAccessKey.isBlank()) {
            initError = "AWS credentials not configured (set AWS_ACCESS_KEY / AWS_SECRET_KEY env vars)";
            log.warn("[BILL-IMAGE] {}", initError);
            return;
        }
        try {
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                    .build();
            log.info("[BILL-IMAGE] S3 client ready — bucket='{}' region='{}'", bucket, region);
        } catch (Exception e) {
            initError = "S3 init failed: " + e.getMessage();
            log.error("[BILL-IMAGE] {}", initError, e);
        }
    }

    @PreDestroy
    void destroy() {
        if (s3Client != null) s3Client.close();
    }

    /**
     * Streams one bill image from S3.
     *
     * @param companyId    company identifier, e.g. "ALW001"
     * @param materialType "GOLD" or "SILVER"
     * @param billNumber   bill number — slashes are automatically replaced with underscores
     *                     to match the folder name created by the desktop app
     * @param imageName    one of: open_customer.png, open_jewel.png, open_user.png,
     *                             close_customer.png, close_jewel.png, close_user.png
     */
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(
            @RequestParam String companyId,
            @RequestParam String materialType,
            @RequestParam String billNumber,
            @RequestParam String imageName) {

        if (s3Client == null) {
            log.warn("[BILL-IMAGE] S3 client not available: {}", initError);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        // Desktop app stores images under: {prefix}/{companyId}/{MATERIAL}/{bill_number}/image.png
        // Bill numbers that contain "/" are saved with "_" on disk (and therefore on S3)
        String safeBill = billNumber.replace("/", "_");
        String s3Key    = prefix + "/" + companyId + "/" + materialType.toUpperCase()
                        + "/" + safeBill + "/" + imageName;

        log.info("[BILL-IMAGE] Fetching s3://{}/{}", bucket, s3Key);

        try {
            ResponseBytes<GetObjectResponse> obj = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3Key)
                            .build());

            byte[] data = obj.asByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(imageName.endsWith(".jpg")
                    ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG);
            headers.setContentLength(data.length);
            // Cache for 24 h — bill images don't change once saved
            headers.setCacheControl("public, max-age=86400");

            log.info("[BILL-IMAGE] OK — {} bytes for {}", data.length, s3Key);
            return ResponseEntity.ok().headers(headers).body(data);

        } catch (NoSuchKeyException e) {
            log.info("[BILL-IMAGE] Not found (NoSuchKey): {}", s3Key);
            return ResponseEntity.notFound().build();
        } catch (S3Exception e) {
            // S3 returns 403 instead of 404 for non-existent keys when bucket policy
            // blocks s3:ListBucket — treat any 4xx from S3 as "not found"
            int code = e.statusCode();
            log.warn("[BILL-IMAGE] S3 HTTP {} for '{}' — {}", code,
                    s3Key, e.awsErrorDetails().errorMessage());
            if (code >= 400 && code < 500) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            log.error("[BILL-IMAGE] Unexpected error for '{}'", s3Key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Diagnostic endpoint — returns the S3 key that would be fetched for a given bill,
     * plus credential/bucket status. Useful for debugging without loading the image.
     *
     * GET /api/bills/image/diagnose?companyId=X&materialType=GOLD&billNumber=E001&imageName=open_customer.png
     */
    @GetMapping("/image/diagnose")
    public ResponseEntity<java.util.Map<String,Object>> diagnose(
            @RequestParam String companyId,
            @RequestParam String materialType,
            @RequestParam String billNumber,
            @RequestParam String imageName) {

        java.util.Map<String,Object> out = new java.util.LinkedHashMap<>();
        String safeBill = billNumber.replace("/", "_");
        String s3Key    = prefix + "/" + companyId + "/" + materialType.toUpperCase()
                        + "/" + safeBill + "/" + imageName;

        out.put("s3Key",     s3Key);
        out.put("bucket",    bucket);
        out.put("region",    region);
        out.put("s3Ready",   s3Client != null);
        out.put("initError", initError);

        if (s3Client != null) {
            try {
                s3Client.headObject(
                    software.amazon.awssdk.services.s3.model.HeadObjectRequest.builder()
                        .bucket(bucket).key(s3Key).build());
                out.put("objectExists", true);
            } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
                out.put("objectExists", false);
                out.put("s3Error", "NoSuchKey");
            } catch (S3Exception e) {
                out.put("objectExists", false);
                out.put("s3Error", "HTTP " + e.statusCode() + ": " + e.awsErrorDetails().errorMessage());
            } catch (Exception e) {
                out.put("objectExists", false);
                out.put("s3Error", e.getMessage());
            }
        }
        return ResponseEntity.ok(out);
    }
}
