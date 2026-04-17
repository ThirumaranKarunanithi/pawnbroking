package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);
    private final JdbcTemplate jdbc;

    public StockController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /**
     * GET /api/stock?companyId=CMP1&materialType=ALL&search=&from=&to=&page=0&size=50
     */
    @GetMapping
    public ResponseEntity<?> getStock(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "ALL")  String materialType,
            @RequestParam(defaultValue = "")     String search,
            @RequestParam(required = false)      String from,
            @RequestParam(required = false)      String to,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "50")   int size) {
        try {
            boolean filterMat  = !materialType.equalsIgnoreCase("ALL");
            boolean filterFrom = from != null && !from.isBlank();
            boolean filterTo   = to   != null && !to.isBlank();
            boolean filterSrch = !search.isBlank();

            StringBuilder where = new StringBuilder(
                "WHERE cb.company_id = ? AND cb.status::text IN ('OPENED','LOCKED') " +
                "AND (cb.repledge_bill_id IS NULL OR cb.repledge_bill_id = '') ");
            List<Object> params = new ArrayList<>(List.of(companyId));

            if (filterMat)  { where.append("AND cb.jewel_material_type::text = ? "); params.add(materialType.toUpperCase()); }
            if (filterFrom) { where.append("AND cb.opening_date::date >= ?::date "); params.add(from); }
            if (filterTo)   { where.append("AND cb.opening_date::date <= ?::date "); params.add(to); }
            if (filterSrch) {
                where.append("AND (LOWER(cb.bill_number) LIKE ? OR LOWER(cb.customer_name) LIKE ? OR LOWER(cb.area) LIKE ?) ");
                String like = "%" + search.toLowerCase() + "%";
                params.add(like); params.add(like); params.add(like);
            }

            String baseQuery = "FROM company_billing cb " + where;

            // Summary
            String sumSql = "SELECT COUNT(*) total, COALESCE(SUM(cb.amount),0) total_amount, " +
                            "COALESCE(SUM(cb.interest),0) total_interest, " +
                            "COALESCE(SUM(cb.gross_weight),0) total_weight " + baseQuery;
            Map<String, Object> summary = jdbc.queryForMap(sumSql, params.toArray());

            // Paged list
            String listSql = "SELECT cb.bill_number, cb.opening_date::text, cb.customer_name, " +
                "cb.spouse_type, cb.spouse_name, cb.area, cb.items, cb.amount, cb.interest, " +
                "cb.gross_weight, cb.jewel_material_type::text material_type, " +
                "cb.status::text, cb.document_charge, cb.total_advance_amount_paid " +
                baseQuery + "ORDER BY cb.opening_date DESC, cb.bill_number " +
                "LIMIT ? OFFSET ?";
            List<Object> listParams = new ArrayList<>(params);
            listParams.add(size);
            listParams.add(page * size);

            List<Map<String, Object>> rows = jdbc.queryForList(listSql, listParams.toArray());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total",          summary.get("total"));
            result.put("totalAmount",    summary.get("total_amount"));
            result.put("totalInterest",  summary.get("total_interest"));
            result.put("totalWeight",    summary.get("total_weight"));
            result.put("page",           page);
            result.put("size",           size);
            result.put("bills",          rows);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Stock error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/stock/repledge?companyId=CMP1&materialType=ALL&search=
     */
    @GetMapping("/repledge")
    public ResponseEntity<?> getRepledgeStock(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "ALL") String materialType,
            @RequestParam(defaultValue = "")    String search,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "50")  int size) {
        try {
            boolean filterMat  = !materialType.equalsIgnoreCase("ALL");
            boolean filterSrch = !search.isBlank();

            StringBuilder where = new StringBuilder(
                "WHERE company_id = ? AND status::text IN ('OPENED','LOCKED') ");
            List<Object> params = new ArrayList<>(List.of(companyId));

            if (filterMat)  { where.append("AND jewel_material_type::text = ? "); params.add(materialType.toUpperCase()); }
            if (filterSrch) {
                where.append("AND (LOWER(repledge_bill_id) LIKE ? OR LOWER(repledge_name) LIKE ?) ");
                String like = "%" + search.toLowerCase() + "%";
                params.add(like); params.add(like);
            }

            String baseQuery = "FROM repledge_billing " + where;
            String sumSql    = "SELECT COUNT(*) total, COALESCE(SUM(amount),0) total_amount, " +
                               "COALESCE(SUM(open_taken_amount),0) total_interest " + baseQuery;
            Map<String, Object> summary = jdbc.queryForMap(sumSql, params.toArray());

            String listSql = "SELECT repledge_bill_id, repledge_bill_number, opening_date::text, " +
                "repledge_name, company_bill_number, amount, open_taken_amount interest, " +
                "jewel_material_type::text material_type, status::text " +
                baseQuery + "ORDER BY opening_date DESC LIMIT ? OFFSET ?";
            List<Object> listParams = new ArrayList<>(params);
            listParams.add(size); listParams.add(page * size);

            List<Map<String, Object>> rows = jdbc.queryForList(listSql, listParams.toArray());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total",        summary.get("total"));
            result.put("totalAmount",  summary.get("total_amount"));
            result.put("totalInterest",summary.get("total_interest"));
            result.put("page", page); result.put("size", size);
            result.put("bills", rows);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Repledge stock error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
