package com.pawnbroking.app.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.pawnbroking.app.config.AppConfig;
import com.pawnbroking.app.models.Bill;
import com.pawnbroking.app.models.Company;
import com.pawnbroking.app.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.HttpUrl;

public class ApiService {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ExecutorService EXEC = Executors.newCachedThreadPool();
    private static final String PREFS = "pawn_prefs";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    public static void login(Context ctx, String username, String password, Callback<User> cb) {
        EXEC.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("username", username);
                body.put("password", password);
                Request req = new Request.Builder()
                    .url(AppConfig.LOGIN)
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "";
                    if (res.isSuccessful()) {
                        JSONObject data = new JSONObject(raw);
                        User user = User.fromJson(data);
                        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
                        ed.putString("token", user.token);
                        ed.putString("userName", user.userName);
                        ed.putString("employeeName", user.employeeName);
                        ed.apply();
                        cb.onSuccess(user);
                    } else {
                        JSONObject err = new JSONObject(raw);
                        String msg = err.optString("error", null);
                        if (msg == null) msg = err.optString("message", null);
                        if (msg == null) msg = "Login failed (HTTP " + res.code() + ")";
                        cb.onError(msg);
                    }
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    public static void logout(Context ctx) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static boolean isLoggedIn(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                  .contains("token");
    }

    public static String getSavedEmployeeName(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                  .getString("employeeName", "");
    }

    // ── Companies ─────────────────────────────────────────────────────────────

    public static void getCompanies(Callback<List<Company>> cb) {
        EXEC.execute(() -> {
            try {
                Request req = new Request.Builder().url(AppConfig.COMPANIES).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "[]";
                    checkStatus(res, raw);
                    JSONArray arr = new JSONArray(raw);
                    List<Company> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++)
                        list.add(Company.fromJson(arr.getJSONObject(i)));
                    cb.onSuccess(list);
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    // ── Bills ─────────────────────────────────────────────────────────────────

    public static void getBills(String companyId, String type, String status,
                                String search, int page, int size,
                                Callback<BillsResult> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.BILLS).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("type", type)
                    .addQueryParameter("status", status)
                    .addQueryParameter("search", search)
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("size", String.valueOf(size))
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    JSONObject data = new JSONObject(raw);
                    JSONArray arr = data.getJSONArray("bills");
                    List<Bill> bills = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++)
                        bills.add(Bill.fromJson(arr.getJSONObject(i)));
                    cb.onSuccess(new BillsResult(bills, data.optInt("total", 0)));
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    public static void getBillDetail(String companyId, String billNumber, String type,
                                     Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.BILLS + "/" + billNumber).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("type", type)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    public static void getDashboard(String companyId, String date, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl.Builder builder = HttpUrl.parse(AppConfig.DASHBOARD).newBuilder()
                    .addQueryParameter("companyId", companyId);
                if (date != null) builder.addQueryParameter("date", date);
                Request req = new Request.Builder().url(builder.build()).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    // ── Today's Account ───────────────────────────────────────────────────────

    public static void getTodaysAccount(String companyId, String date, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.TODAYS_ACCOUNT).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("date", date)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    // ── Customers ─────────────────────────────────────────────────────────────

    public static void searchCustomers(String companyId, String query, Callback<JSONArray> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.CUSTOMERS + "/search").newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("query", query)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "[]";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONArray(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    // ── Stock ─────────────────────────────────────────────────────────────────

    public static void getStock(String companyId, String materialType, String search,
                                String from, String to, int page, int size,
                                Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl.Builder b = HttpUrl.parse(AppConfig.STOCK).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("materialType", materialType)
                    .addQueryParameter("search", search != null ? search : "")
                    .addQueryParameter("page",  String.valueOf(page))
                    .addQueryParameter("size",  String.valueOf(size));
                if (from != null && !from.isEmpty()) b.addQueryParameter("from", from);
                if (to   != null && !to.isEmpty())   b.addQueryParameter("to",   to);
                Request req = new Request.Builder().url(b.build()).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void getRepledgeStock(String companyId, String materialType, String search,
                                        int page, int size, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.STOCK_REPLEDGE).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("materialType", materialType)
                    .addQueryParameter("search", search != null ? search : "")
                    .addQueryParameter("page",  String.valueOf(page))
                    .addQueryParameter("size",  String.valueOf(size))
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public static void getMonthlyReport(String companyId, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.REPORT_MONTHLY).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void getTrialBalance(String companyId, String from, String to,
                                       Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl.Builder b = HttpUrl.parse(AppConfig.REPORT_TRIAL_BALANCE).newBuilder()
                    .addQueryParameter("companyId", companyId);
                if (from != null && !from.isEmpty()) b.addQueryParameter("from", from);
                if (to   != null && !to.isEmpty())   b.addQueryParameter("to",   to);
                Request req = new Request.Builder().url(b.build()).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void getTodaysAccountDetails(String companyId, String date, String type,
                                               Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.TODAYS_ACCOUNT_DETAILS).newBuilder()
                    .addQueryParameter("companyId", companyId)
                    .addQueryParameter("date", date)
                    .addQueryParameter("type", type)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) {
                cb.onError(e.getMessage());
            }
        });
    }

    // ── Billing ───────────────────────────────────────────────────────────────

    public static void findBill(String companyId, String billNumber, String materialType,
                                Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.BILLING + "/find").newBuilder()
                    .addQueryParameter("companyId",    companyId)
                    .addQueryParameter("billNumber",   billNumber)
                    .addQueryParameter("materialType", materialType)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void getNextBillNumber(String companyId, String materialType,
                                         Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.BILLING + "/next-bill-number").newBuilder()
                    .addQueryParameter("companyId",    companyId)
                    .addQueryParameter("materialType", materialType)
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void calculateClosing(String companyId, String materialType,
                                         double amount, double interest, double documentCharge,
                                         String openingDate, double totalAdvancePaid,
                                         Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                HttpUrl url = HttpUrl.parse(AppConfig.BILLING + "/calculate-closing").newBuilder()
                    .addQueryParameter("companyId",       companyId)
                    .addQueryParameter("materialType",    materialType)
                    .addQueryParameter("amount",          String.valueOf(amount))
                    .addQueryParameter("interest",        String.valueOf(interest))
                    .addQueryParameter("documentCharge",  String.valueOf(documentCharge))
                    .addQueryParameter("openingDate",     openingDate)
                    .addQueryParameter("totalAdvancePaid",String.valueOf(totalAdvancePaid))
                    .build();
                Request req = new Request.Builder().url(url).get().build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void calculateBilling(JSONObject body, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                Request req = new Request.Builder()
                    .url(AppConfig.BILLING + "/calculate")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    public static void saveBill(JSONObject body, Callback<JSONObject> cb) {
        EXEC.execute(() -> {
            try {
                Request req = new Request.Builder()
                    .url(AppConfig.BILLING + "/save")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();
                try (Response res = CLIENT.newCall(req).execute()) {
                    String raw = res.body() != null ? res.body().string() : "{}";
                    checkStatus(res, raw);
                    cb.onSuccess(new JSONObject(raw));
                }
            } catch (Exception e) { cb.onError(e.getMessage()); }
        });
    }

    private static void checkStatus(Response res, String raw) throws Exception {
        if (!res.isSuccessful())
            throw new Exception("API error " + res.code() + ": " + raw);
    }

    public static class BillsResult {
        public final List<Bill> bills;
        public final int total;
        public BillsResult(List<Bill> bills, int total) {
            this.bills = bills; this.total = total;
        }
    }
}
