package magizhchi.academy.pawnrestservice.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);
    private final JdbcTemplate jdbc;

    public BillingController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    /**
     * GET /api/billing/next-bill-number?companyId=&materialType=GOLD
     * Reads current bill number from COMPANY table and returns next one.
     */
    @GetMapping("/next-bill-number")
    public ResponseEntity<?> nextBillNumber(
            @RequestParam String companyId,
            @RequestParam String materialType) {
        try {
            String mt = materialType.toUpperCase();
            String sql = "SELECT GOLD_CUR_BILL_PREFIX, GOLD_CUR_BILL_NUMBER, " +
                         "SILVER_CUR_BILL_PREFIX, SILVER_CUR_BILL_NUMBER " +
                         "FROM COMPANY WHERE COMPANY_ID = ?";
            Map<String, Object> row = jdbc.queryForMap(sql, companyId);

            String prefix;
            long curNumber;
            if ("GOLD".equals(mt)) {
                prefix    = row.get("GOLD_CUR_BILL_PREFIX") != null ? row.get("GOLD_CUR_BILL_PREFIX").toString() : "";
                curNumber = row.get("GOLD_CUR_BILL_NUMBER") != null ? ((Number) row.get("GOLD_CUR_BILL_NUMBER")).longValue() : 0L;
            } else {
                prefix    = row.get("SILVER_CUR_BILL_PREFIX") != null ? row.get("SILVER_CUR_BILL_PREFIX").toString() : "";
                curNumber = row.get("SILVER_CUR_BILL_NUMBER") != null ? ((Number) row.get("SILVER_CUR_BILL_NUMBER")).longValue() : 0L;
            }
            long nextNumber = curNumber + 1;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("prefix", prefix);
            result.put("number", nextNumber);
            result.put("billNumber", prefix + nextNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("nextBillNumber error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/billing/find?companyId=&billNumber=
     * Fetches all stored fields for an existing bill — used to populate the mobile form.
     * Searches without materialType so the user only needs to know the bill number.
     */
    @GetMapping("/find")
    public ResponseEntity<?> find(
            @RequestParam String companyId,
            @RequestParam String billNumber,
            @RequestParam(required = false) String materialType) {
        try {
            boolean filterMt = materialType != null && !materialType.isBlank();
            String sql =
                "SELECT bill_number, jewel_material_type::text AS material_type, " +
                "  to_char(opening_date, 'DD-MM-YYYY') AS opening_date, " +
                "  customer_name, gender::text AS gender, " +
                "  COALESCE(spouse_type,'') AS spouse_type, COALESCE(spouse_name,'') AS spouse_name, " +
                "  COALESCE(door_number,'') AS door_number, COALESCE(street,'') AS street, " +
                "  COALESCE(area,'') AS area, COALESCE(city,'') AS city, " +
                "  COALESCE(mobile_number,'') AS mobile_number, " +
                "  COALESCE(mobile_number_2,'') AS mobile_number_2, " +
                "  COALESCE(items,'') AS items, " +
                "  COALESCE(gross_weight,0) AS gross_weight, " +
                "  COALESCE(net_weight,0) AS net_weight, " +
                "  COALESCE(purity,0) AS purity, " +
                "  COALESCE(amount,0) AS amount, " +
                "  COALESCE(interest,0) AS interest, " +
                "  COALESCE(document_charge,0) AS document_charge, " +
                "  COALESCE(open_taken_amount,0) AS taken_amount, " +
                "  COALESCE(togive_amount,0) AS to_give_amount, " +
                "  COALESCE(given_amount,0) AS given_amount, " +
                "  status::text AS status, " +
                "  COALESCE(physical_location,'') AS physical_location, " +
                "  COALESCE(note,'') AS note, " +
                "  COALESCE(nominee_name,'') AS nominee_name, " +
                "  to_char(accepted_closing_date, 'DD-MM-YYYY') AS accepted_closing_date, " +
                "  to_char(created_date, 'DD-MM-YYYY HH24:MI') AS created_date, " +
                "  COALESCE(close_taken_amount,0) AS close_taken_amount, " +
                "  COALESCE(total_advance_amount_paid,0) AS total_advance_amount_paid, " +
                "  COALESCE(total_other_charges,0) AS total_other_charges, " +
                "  COALESCE(toget_amount,0) AS toget_amount, " +
                "  COALESCE(discount_amount,0) AS discount_amount, " +
                "  COALESCE(got_amount,0) AS got_amount, " +
                "  COALESCE(customer_copy::text,'') AS customer_copy, " +
                "  COALESCE(closed_user_id,'') AS closed_user_id, " +
                "  to_char(closing_date, 'DD-MM-YYYY') AS closing_date " +
                "FROM company_billing " +
                "WHERE company_id = ? AND bill_number = ?" +
                (filterMt ? " AND jewel_material_type = ?::MATERIAL_TYPE" : "") +
                " LIMIT 1";

            List<Map<String, Object>> rows = filterMt
                ? jdbc.queryForList(sql, companyId, billNumber, materialType.toUpperCase())
                : jdbc.queryForList(sql, companyId, billNumber);
            if (rows.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Bill not found: " + billNumber));
            }

            Map<String, Object> row = rows.get(0);
            Map<String, Object> result = new LinkedHashMap<>();
            row.forEach((k, v) -> result.put(k, v != null ? v : ""));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("find error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/billing/calculate
     * Body: { companyId, materialType, amount, date (yyyy-MM-dd) }
     * Replicates desktop setAmountRelatedText() logic exactly:
     *   1. Fetch INTEREST from COMPANY_INTEREST
     *   2. Fetch DOCUMENT_CHARGE from COMPANY_DOCUMENT_CHARGE
     *   3. Fetch FORMULA from COMPANY_FORMULA (OPEN type)
     *   4. Replace AMOUNT/INTEREST/DOCUMENT_CHARGE placeholders in formula
     *   5. Evaluate formula → takenAmount
     *   6. toGiveAmount = amount - takenAmount
     *   7. interestPerMonth = takenAmount - documentCharge
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@RequestBody Map<String, Object> body) {
        try {
            String companyId    = body.get("companyId").toString();
            String materialType = body.get("materialType").toString().toUpperCase();
            double amount       = Double.parseDouble(body.get("amount").toString());
            String date         = body.get("date").toString(); // yyyy-MM-dd

            // 1. Interest — exact desktop query from COMPANY_INTEREST
            String interestSql =
                "SELECT COALESCE(INTEREST::text, '0') FROM COMPANY_INTEREST " +
                "WHERE COMPANY_ID = ? AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO " +
                "AND ?::date BETWEEN DATE_FROM AND DATE_TO LIMIT 1";
            String sInterest = queryScalar(interestSql, companyId, materialType, amount, date);

            // 2. Document charge — exact desktop query from COMPANY_DOCUMENT_CHARGE
            String docSql =
                "SELECT COALESCE(DOCUMENT_CHARGE::text, '0') FROM COMPANY_DOCUMENT_CHARGE " +
                "WHERE COMPANY_ID = ? AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO " +
                "AND ?::date BETWEEN DATE_FROM AND DATE_TO LIMIT 1";
            String sDocCharge = queryScalar(docSql, companyId, materialType, amount, date);

            // 3. Formula — exact desktop query from COMPANY_FORMULA (OPEN type)
            String formulaSql =
                "SELECT COALESCE(FORMULA, 'AMOUNT - INTEREST - DOCUMENT_CHARGE') FROM COMPANY_FORMULA " +
                "WHERE COMPANY_ID = ? AND JEWEL_MATERIAL_TYPE = ?::MATERIAL_TYPE " +
                "AND FORMULA_OPERATION_TYPE = ?::OPERATION_TYPE " +
                "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO " +
                "AND ?::date BETWEEN DATE_FROM AND DATE_TO LIMIT 1";
            String sFormula = queryScalar(formulaSql, companyId, materialType, "OPEN", amount, date);
            if (sFormula == null || sFormula.isBlank()) {
                sFormula = "AMOUNT - INTEREST - DOCUMENT_CHARGE";
            }

            double dInterest  = parseDouble(sInterest);
            double dDocCharge = parseDouble(sDocCharge);

            // 4. Replace placeholders (same order as desktop)
            String evaluated = sFormula
                .replace("AMOUNT",           String.valueOf(amount))
                .replace("INTEREST",         String.valueOf(dInterest))
                .replace("DOCUMENT_CHARGE",  String.valueOf(dDocCharge));

            // 5. Evaluate arithmetic expression using Spring SpEL (replaces desktop ScriptEngine/Nashorn)
            double takenAmount;
            try {
                ExpressionParser parser = new SpelExpressionParser();
                Expression exp = parser.parseExpression(evaluated);
                takenAmount = Math.round(exp.getValue(Double.class));
            } catch (Exception e) {
                log.warn("Formula eval failed ({}), using fallback: {}", evaluated, e.getMessage());
                takenAmount = Math.round(amount - dInterest - dDocCharge);
            }

            // 6-7. Derived amounts — exact desktop logic
            double toGiveAmount    = amount - takenAmount;
            double interestPerMonth = takenAmount - dDocCharge;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("interest",         dInterest);
            result.put("documentCharge",   dDocCharge);
            result.put("takenAmount",      takenAmount);
            result.put("toGiveAmount",     toGiveAmount);
            result.put("givenAmount",      toGiveAmount);  // default given = toGive
            result.put("interestPerMonth", interestPerMonth);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("calculate error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/billing/calculate-closing
     * Exact port of GoldBillClosingController.setAllHeaderValuesToFields() logic.
     * Params: companyId, materialType, amount, interest, documentCharge, openingDate (yyyy-MM-dd),
     *         totalAdvancePaid, closingDate (yyyy-MM-dd, defaults to today)
     */
    @GetMapping("/calculate-closing")
    public ResponseEntity<?> calculateClosing(
            @RequestParam String companyId,
            @RequestParam String materialType,
            @RequestParam double amount,
            @RequestParam double interest,
            @RequestParam(defaultValue = "0") double documentCharge,
            @RequestParam String openingDate,                         // yyyy-MM-dd
            @RequestParam(defaultValue = "0") double totalAdvancePaid,
            @RequestParam(required = false) String closingDate) {    // yyyy-MM-dd, null = today
        try {
            String mt = materialType.toUpperCase();
            java.time.LocalDate startDate = java.time.LocalDate.parse(openingDate);
            java.time.LocalDate endDate   = (closingDate != null && !closingDate.isBlank())
                                            ? java.time.LocalDate.parse(closingDate)
                                            : java.time.LocalDate.now();
            String endDateStr = endDate.toString(); // yyyy-MM-dd

            // 1. Interest type: DAY_OR_MONTHLY_INTEREST from COMPANY (cast enum to text)
            String interestType = queryScalar(
                "SELECT COALESCE(DAY_OR_MONTHLY_INTEREST::text,'MONTH') FROM COMPANY WHERE COMPANY_ID = ?",
                companyId);
            if (interestType == null || interestType.isBlank() || "0".equals(interestType)) interestType = "MONTH";

            // 2. Reduce data from COMPANY_REDUCE_MONTHS_OR_DAYS
            String[] reduceDatas  = queryReduceRow(companyId, mt, "REDUCTION");
            String[] minimumDatas = queryReduceRow(companyId, mt, "MINIMUM");

            // 3. Total days between opening and closing
            long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

            // 4. Chettinad month calculation (exact desktop logic)
            long[] actualMonths = getDifferenceMonthsChettinad(startDate, endDate);
            long   actM = actualMonths[0];
            long   actD = actualMonths[1];
            String actualTotalLabel = actM + " Months and " + actD + " Days.";

            // 5. Apply reduction → takenMonths, takenDays
            double takenMonths = 0;
            long   takenDays   = 0;
            int    reduceVal   = parseInt(reduceDatas[0]);
            String reduceType  = reduceDatas[1] != null ? reduceDatas[1] : "";
            int    minVal      = parseInt(minimumDatas[0]);

            if ("MONTH".equals(interestType)) {
                long[] taken;
                if ("MONTHS FROM TOTAL MONTH".equals(reduceType)) {
                    taken = getDifferenceMonthsWithTotalMonthReduction(actualMonths, reduceVal);
                } else if ("MONTHS FROM OPENING MONTH".equals(reduceType)) {
                    taken = getDifferenceMonthsWithMonthReduction(startDate, totalDays, reduceVal);
                } else if ("DAYS".equals(reduceType)) {
                    taken = getDifferenceMonthsWithDayReduction(startDate, totalDays, reduceVal);
                } else {
                    taken = new long[]{actM, actD};
                }
                // Convert remaining days to fractional months via COMPANY_MONTH_SETTING
                double remDaysAsMonths = 0;
                if (actM > 0 && taken[1] > 0) {
                    String rm = queryScalar(
                        "SELECT COALESCE(AS_MONTH,0) FROM COMPANY_MONTH_SETTING " +
                        "WHERE COMPANY_ID=? AND JEWEL_MATERIAL_TYPE=?::MATERIAL_TYPE " +
                        "AND ? BETWEEN DAYS_FROM AND DAYS_TO " +
                        "AND ?::date BETWEEN DATE_FROM AND DATE_TO LIMIT 1",
                        companyId, mt, (double) taken[1], endDateStr);
                    remDaysAsMonths = parseDouble(rm);
                }
                takenMonths = taken[0] + remDaysAsMonths;
                // NOTE: minimum is displayed only (like desktop), not enforced in formula
            } else {
                // DAY interest type
                if ("MONTHS FROM OPENING MONTH".equals(reduceType)) {
                    long[] taken = getDifferenceMonthsWithDayReductionFromMonths(startDate, totalDays, reduceVal);
                    takenDays = taken[1];
                } else if ("DAYS".equals(reduceType)) {
                    takenDays = Math.max(0, totalDays - reduceVal);
                } else {
                    takenDays = totalDays;
                }
                // NOTE: minimum is displayed only (like desktop), not enforced in formula
                takenMonths = takenDays; // DAY mode uses days in formula
            }

            // 6. CLOSE formula
            String formulaSql =
                "SELECT COALESCE(FORMULA,'AMOUNT * INTEREST / 100 * TAKEN_MONTHS') FROM COMPANY_FORMULA " +
                "WHERE COMPANY_ID=? AND JEWEL_MATERIAL_TYPE=?::MATERIAL_TYPE " +
                "AND FORMULA_OPERATION_TYPE=?::OPERATION_TYPE " +
                "AND ? BETWEEN AMOUNT_FROM AND AMOUNT_TO " +
                "AND ?::date BETWEEN DATE_FROM AND DATE_TO LIMIT 1";
            String sFormula = queryScalar(formulaSql, companyId, mt, "CLOSE", amount, endDateStr);
            if (sFormula == null || sFormula.isBlank() || "0".equals(sFormula))
                sFormula = "AMOUNT * INTEREST / 100 * TAKEN_MONTHS";

            String evaluated = sFormula
                .replace("AMOUNT",          String.valueOf(amount))
                .replace("INTEREST",        String.valueOf(interest))
                .replace("DOCUMENT_CHARGE", String.valueOf(documentCharge))
                .replace("TAKEN_MONTHS",    String.valueOf(takenMonths))
                .replace("TAKEN_DAYS",      String.valueOf(takenDays));

            double closeTaken;
            try {
                ExpressionParser parser = new SpelExpressionParser();
                closeTaken = Math.round(parser.parseExpression(evaluated).getValue(Double.class));
            } catch (Exception e) {
                log.warn("CLOSE formula eval failed ({}): {}", evaluated, e.getMessage());
                closeTaken = Math.round(amount * interest / 100.0 * takenMonths);
            }

            // 7. toGetAmount = amount + closeTaken - totalAdvancePaid
            double toGetAmount = amount + closeTaken - totalAdvancePaid;

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("interestType",      interestType);
            result.put("actualTotalMonths", actualTotalLabel);
            result.put("minimumMonths",     minimumDatas[0] != null ? minimumDatas[0] : "0");
            result.put("toReduceMonths",    reduceDatas[0]  != null ? reduceDatas[0]  : "0");
            result.put("forMonths",         takenMonths);
            result.put("closeTakenAmount",  closeTaken);
            result.put("toGetAmount",       toGetAmount);
            result.put("totalAdvancePaid",  totalAdvancePaid);
            result.put("discount",          0);
            result.put("gotAmount",         toGetAmount);
            result.put("totalOtherCharges", 0);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("calculateClosing error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Date/month helpers (exact port of DateRelatedCalculations) ────────────

    private long[] getDifferenceMonthsChettinad(java.time.LocalDate start, java.time.LocalDate end) {
        int sDay = start.getDayOfMonth(), sMonth = start.getMonthValue(), sYear = start.getYear();
        int eDay = end.getDayOfMonth(), eMonth = end.getMonthValue(), eYear = end.getYear();
        int totDays = eDay - sDay;
        if (totDays < 0 && eMonth > 0) { eDay += 30; eMonth--; totDays = eDay - sDay; }
        int totMonths = eMonth - sMonth;
        if (totMonths < 0 && eYear > sYear) { eMonth += 12; eYear--; totMonths = eMonth - sMonth; }
        long totalMonths = totMonths + ((long)(eYear > start.getYear() ? eYear - start.getYear() : 0) * 12);
        return new long[]{totalMonths, totDays};
    }

    private long[] getDifferenceMonthsWithTotalMonthReduction(long[] actual, int reduce) {
        long m = actual[0] > 0 ? Math.max(0, actual[0] - reduce) : 0;
        return new long[]{m, actual[1]};
    }

    private long[] getDifferenceMonthsWithMonthReduction(java.time.LocalDate start, long totalDays, int reduceMonths) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
        for (int i = 0; i < reduceMonths; i++) {
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            if (daysInMonth <= totalDays) { totalDays -= daysInMonth; } else { break; }
            cal.add(java.util.Calendar.MONTH, 1);
        }
        long months = 0;
        for (;;) {
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            if (daysInMonth <= totalDays) { totalDays -= daysInMonth; months++; } else { break; }
            cal.add(java.util.Calendar.MONTH, 1);
        }
        return new long[]{months, totalDays};
    }

    private long[] getDifferenceMonthsWithDayReduction(java.time.LocalDate start, long totalDays, int reduceDays) {
        totalDays = Math.max(0, totalDays - reduceDays);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
        long months = 0;
        for (;;) {
            int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            if (daysInMonth <= totalDays) { totalDays -= daysInMonth; months++; } else { break; }
            cal.add(java.util.Calendar.MONTH, 1);
        }
        return new long[]{months, totalDays};
    }

    private long[] getDifferenceMonthsWithDayReductionFromMonths(java.time.LocalDate start, long totalDays, int reduceMonths) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth());
        long daysToRemove = 0;
        for (int i = 0; i < reduceMonths; i++) {
            daysToRemove += cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            cal.add(java.util.Calendar.MONTH, 1);
        }
        return new long[]{0, Math.max(0, totalDays - daysToRemove)};
    }

    private String[] queryReduceRow(String companyId, String mt, String type) {
        String[] data = {"0", ""};
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT COALESCE(DAYS_OR_MONTHS,0), COALESCE(REDUCTION_TYPE::text,'') " +
                "FROM COMPANY_REDUCE_MONTHS_OR_DAYS " +
                "WHERE COMPANY_ID=? AND JEWEL_MATERIAL_TYPE=?::MATERIAL_TYPE " +
                "AND REDUCTION_OR_MINIMUM_TYPE::text=? LIMIT 1",
                companyId, mt, type);
            if (!rows.isEmpty()) {
                Map<String, Object> r = rows.get(0);
                Object[] vals = r.values().toArray();
                data[0] = vals[0] != null ? vals[0].toString() : "0";
                data[1] = vals[1] != null ? vals[1].toString() : "";
            }
        } catch (Exception e) { log.warn("queryReduceRow failed: {}", e.getMessage()); }
        return data;
    }

    private int parseInt(String s) {
        try { return s != null ? Integer.parseInt(s.trim()) : 0; } catch (Exception e) { return 0; }
    }

    /**
     * POST /api/billing/save
     * Inserts into COMPANY_BILLING and updates COMPANY current bill number.
     * Exact INSERT columns match desktop BillOpeningDBOperation.java line 689-700.
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {
        try {
            String companyId     = body.get("companyId").toString();
            String materialType  = body.get("materialType").toString().toUpperCase();
            String billNumber    = body.get("billNumber").toString();
            String openingDate   = body.get("openingDate").toString();          // yyyy-MM-dd
            String customerName  = body.get("customerName").toString();
            String gender        = body.getOrDefault("gender",       "MALE").toString().toUpperCase();
            String spouseType    = body.getOrDefault("spouseType",   "").toString();
            String spouseName    = body.getOrDefault("spouseName",   "").toString();
            String doorNumber    = body.getOrDefault("doorNumber",   "").toString();
            String street        = body.getOrDefault("street",       "").toString();
            String area          = body.getOrDefault("area",         "").toString();
            String city          = body.getOrDefault("city",         "").toString();
            String mobileNumber  = body.getOrDefault("mobileNumber", "").toString();
            String items         = body.getOrDefault("items",        "").toString();
            double grossWeight   = parseDouble(body.getOrDefault("grossWeight",   "0").toString());
            double netWeight     = parseDouble(body.getOrDefault("netWeight",     "0").toString());
            double purity        = parseDouble(body.getOrDefault("purity",        "0").toString());
            double amount        = parseDouble(body.getOrDefault("amount",        "0").toString());
            double interest      = parseDouble(body.getOrDefault("interest",      "0").toString());
            double docCharge     = parseDouble(body.getOrDefault("documentCharge","0").toString());
            double takenAmount   = parseDouble(body.getOrDefault("takenAmount",   "0").toString());
            double toGiveAmount  = parseDouble(body.getOrDefault("toGiveAmount",  "0").toString());
            double givenAmount   = parseDouble(body.getOrDefault("givenAmount",   "0").toString());
            String status        = body.getOrDefault("status",       "OPENED").toString().toUpperCase();
            String note          = body.getOrDefault("note",         "").toString();
            String nomineeName   = body.getOrDefault("nomineeName",  "").toString();
            String mobileNumber2 = body.getOrDefault("mobileNumber2","").toString();
            String createdUserId = body.getOrDefault("createdUserId","MOBILE").toString();
            Object acceptedDateObj = body.get("acceptedClosingDate");
            String acceptedDate  = (acceptedDateObj != null && !acceptedDateObj.toString().isBlank())
                                   ? acceptedDateObj.toString() : null;

            // Build INSERT — matches desktop columns exactly
            StringBuilder colsSb = new StringBuilder(
                "COMPANY_ID, JEWEL_MATERIAL_TYPE, BILL_NUMBER, OPENING_DATE, " +
                "CUSTOMER_NAME, GENDER, SPOUSE_TYPE, SPOUSE_NAME, " +
                "DOOR_NUMBER, STREET, AREA, CITY, MOBILE_NUMBER, ITEMS, " +
                "GROSS_WEIGHT, NET_WEIGHT, PURITY, AMOUNT, INTEREST, DOCUMENT_CHARGE, " +
                "OPEN_TAKEN_AMOUNT, TOGIVE_AMOUNT, GIVEN_AMOUNT, STATUS, NOTE, " +
                "CREATED_USER_ID, created_date, NOMINEE_NAME, mobile_number_2");
            StringBuilder valsSb = new StringBuilder(
                "?, ?::MATERIAL_TYPE, ?, ?::date, " +
                "?, ?::GENDER_TYPE, ?, ?, " +
                "?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?::COMPANY_BILL_STATUS, ?, " +
                "?, NOW(), ?, ?");

            List<Object> params = new ArrayList<>();
            params.add(companyId);   params.add(materialType); params.add(billNumber);  params.add(openingDate);
            params.add(customerName);params.add(gender);        params.add(spouseType);  params.add(spouseName);
            params.add(doorNumber);  params.add(street);        params.add(area);        params.add(city);
            params.add(mobileNumber);params.add(items);
            params.add(grossWeight); params.add(netWeight);     params.add(purity);
            params.add(amount);      params.add(interest);      params.add(docCharge);
            params.add(takenAmount); params.add(toGiveAmount);  params.add(givenAmount);
            params.add(status);      params.add(note);
            params.add(createdUserId);params.add(nomineeName);  params.add(mobileNumber2);

            if (acceptedDate != null) {
                colsSb.append(", accepted_closing_date");
                valsSb.append(", ?::date");
                params.add(acceptedDate);
            }

            String insertSql = "INSERT INTO COMPANY_BILLING(" + colsSb + ") VALUES(" + valsSb + ")";
            jdbc.update(insertSql, params.toArray());

            // Update COMPANY table current bill number — matches desktop updateGold/SilverPreNumberDetail()
            String prefix = billNumber.replaceAll("[0-9]", "");
            long   number;
            try { number = Long.parseLong(billNumber.replaceAll("[^0-9]", "")); }
            catch (NumberFormatException e) { number = 0; }

            if ("GOLD".equals(materialType)) {
                jdbc.update("UPDATE COMPANY SET GOLD_CUR_BILL_PREFIX = ?, GOLD_CUR_BILL_NUMBER = ? WHERE COMPANY_ID = ?",
                    prefix, number, companyId);
            } else {
                jdbc.update("UPDATE COMPANY SET SILVER_CUR_BILL_PREFIX = ?, SILVER_CUR_BILL_NUMBER = ? WHERE COMPANY_ID = ?",
                    prefix, number, companyId);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success",    true);
            result.put("billNumber", billNumber);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("save error", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String queryScalar(String sql, Object... params) {
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(sql, params);
            if (!rows.isEmpty() && !rows.get(0).isEmpty()) {
                Object val = rows.get(0).values().iterator().next();
                return val != null ? val.toString() : "0";
            }
        } catch (Exception e) {
            log.warn("queryScalar failed: {}", e.getMessage());
        }
        return "0";
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }
}
