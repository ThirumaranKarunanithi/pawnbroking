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

    // ── Bill image proxy (server fetches from S3, no public bucket needed) ──
    public static final String BILL_IMAGE = BASE_URL + "/api/bills/image";

    /**
     * Returns the REST proxy URL for a bill image.
     * The server fetches the image from S3 and streams it back.
     *
     * @param companyId    e.g. "ALW001"
     * @param materialType e.g. "GOLD" or "SILVER"
     * @param billNumber   e.g. "ALW/GOLD/001" — the server replaces "/" with "_"
     * @param imageName    e.g. "open_customer.png"
     */
    public static String billImageUrl(String companyId, String materialType,
                                       String billNumber, String imageName) {
        try {
            return BILL_IMAGE
                + "?companyId="    + java.net.URLEncoder.encode(companyId,    "UTF-8")
                + "&materialType=" + java.net.URLEncoder.encode(materialType.toUpperCase(), "UTF-8")
                + "&billNumber="   + java.net.URLEncoder.encode(billNumber,   "UTF-8")
                + "&imageName="    + java.net.URLEncoder.encode(imageName,    "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            // UTF-8 is always supported — this never throws
            return BILL_IMAGE + "?companyId=" + companyId
                + "&materialType=" + materialType + "&billNumber=" + billNumber
                + "&imageName=" + imageName;
        }
    }
}
