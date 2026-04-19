package com.pawnbroking.app.config;

public class AppConfig {
    public static final String BASE_URL       = "https://devpawn.magizhchi.academy";
    public static final String LOGIN          = BASE_URL + "/api/auth/login";
    public static final String COMPANIES      = BASE_URL + "/api/companies";
    public static final String BILLS          = BASE_URL + "/api/bills";
    public static final String DASHBOARD      = BASE_URL + "/api/dashboard";
    public static final String CUSTOMERS        = BASE_URL + "/api/customers";
    public static final String CUSTOMERS_SEARCH = BASE_URL + "/api/customers/search";
    public static final String TODAYS_ACCOUNT         = BASE_URL + "/api/todays-account";
    public static final String TODAYS_ACCOUNT_DETAILS = BASE_URL + "/api/todays-account/details";
    public static final String STOCK                  = BASE_URL + "/api/stock";
    public static final String STOCK_REPLEDGE         = BASE_URL + "/api/stock/repledge";
    public static final String STOCK_ALL              = BASE_URL + "/api/stock/all";
    public static final String REPORT_MONTHLY         = BASE_URL + "/api/reports/monthly";
    public static final String REPORT_TRIAL_BALANCE   = BASE_URL + "/api/reports/trial-balance";
    public static final String BILLING                = BASE_URL + "/api/billing";

    // ── AWS S3 image base (pawndbsync uploads here) ──────────────────────────
    private static final String S3_BASE =
        "https://pawnbroking.s3.eu-north-1.amazonaws.com/alwarpuram";

    /**
     * Returns the S3 URL for a bill image.
     * @param companyId      e.g. "ALW001"
     * @param materialType   e.g. "GOLD" or "SILVER"
     * @param billNumber     e.g. "ALW/GOLD/001"  — slashes are replaced with underscores
     * @param imageName      e.g. "open_customer.png"
     */
    public static String billImageUrl(String companyId, String materialType,
                                       String billNumber, String imageName) {
        String safeBill = billNumber.replace("/", "_");
        return S3_BASE + "/" + companyId + "/" + materialType.toUpperCase()
               + "/" + safeBill + "/" + imageName;
    }
}
