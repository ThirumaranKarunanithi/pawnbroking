package com.pawnbroking.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pawnbroking.app.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class MonthlyReportActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TableLayout tableMonthly;
    private TextView tvCompanyName, tvRowCount;

    private String companyId, companyName;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    // Columns: Month | Open# | Open Amt | Close# | Close Amt | Profit | Net# | Net Amt
    private static final String[] HEADERS = {
        "Month", "Open\n#", "Open Amt", "Close\n#", "Close Amt",
        "Profit", "Net\n#", "Net Amt"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        fmt.setMinimumFractionDigits(0);
        fmt.setMaximumFractionDigits(0);

        companyId   = getIntent().getStringExtra("companyId");
        companyName = getIntent().getStringExtra("companyName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MIS Report");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_detail);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_refresh) { load(); return true; }
            return false;
        });

        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvRowCount    = findViewById(R.id.tvRowCount);
        progressBar   = findViewById(R.id.progressBar);
        tableMonthly  = findViewById(R.id.tableMonthly);

        tvCompanyName.setText(companyName);
        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        tableMonthly.removeAllViews();

        ApiService.getMonthlyReport(companyId, new ApiService.Callback<JSONObject>() {
            @Override public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    bind(data);
                });
            }
            @Override public void onError(String msg) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MonthlyReportActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void bind(JSONObject data) {
        JSONArray months = data.optJSONArray("months");
        int total        = data.optInt("total", 0);
        tvRowCount.setText(total + " months");

        // Header
        addRow(null, true);

        if (months == null) return;

        // Totals accumulators
        long   sumOpenCnt = 0, sumCloseCnt = 0, sumNetCnt = 0;
        double sumOpenAmt = 0, sumCloseAmt = 0, sumProfit = 0, sumNetAmt = 0;

        for (int i = 0; i < months.length(); i++) {
            JSONObject m = months.optJSONObject(i);
            if (m == null) continue;

            long   openCnt  = m.optLong("openCount",    0);
            double openAmt  = m.optDouble("openAmount",  0);
            long   closeCnt = m.optLong("closeCount",   0);
            double closeAmt = m.optDouble("closeAmount", 0);
            double profit   = m.optDouble("profit",      0);
            long   netCnt   = m.optLong("earnedCount",  0);
            double netAmt   = m.optDouble("earnedAmount",0);

            sumOpenCnt  += openCnt;  sumOpenAmt  += openAmt;
            sumCloseCnt += closeCnt; sumCloseAmt += closeAmt;
            sumProfit   += profit;
            sumNetCnt   += netCnt;   sumNetAmt   += netAmt;

            String[] cells = {
                m.optString("month", ""),
                String.valueOf(openCnt), shortFmt(openAmt),
                String.valueOf(closeCnt), shortFmt(closeAmt),
                shortFmt(profit),
                String.valueOf(netCnt),  shortFmt(netAmt)
            };
            addDataRow(cells, i);
        }

        // Divider
        addDivider();

        // Totals row
        String[] totals = {
            "TOTAL",
            String.valueOf(sumOpenCnt),  shortFmt(sumOpenAmt),
            String.valueOf(sumCloseCnt), shortFmt(sumCloseAmt),
            shortFmt(sumProfit),
            String.valueOf(sumNetCnt),   shortFmt(sumNetAmt)
        };
        addTotalsRow(totals);
    }

    private void addRow(String[] cells, boolean isHeader) {
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(isHeader ? Color.parseColor("#1E2A4A") : Color.TRANSPARENT);
        String[] labels = isHeader ? HEADERS : cells;
        for (int j = 0; j < labels.length; j++) {
            TextView tv = new TextView(this);
            tv.setText(labels[j]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, isHeader ? 11 : 12);
            tv.setPadding(dp(10), dp(7), dp(10), dp(7));
            if (isHeader) {
                tv.setTextColor(Color.parseColor("#E6B800"));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
            } else {
                tv.setTextColor(j == 0 ? Color.WHITE : Color.parseColor("#CCCCCC"));
                tv.setGravity(j == 0 ? Gravity.START : Gravity.END);
            }
            tr.addView(tv);
        }
        tableMonthly.addView(tr);
    }

    private void addDataRow(String[] cells, int idx) {
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(idx % 2 == 0
            ? Color.parseColor("#16213E") : Color.parseColor("#1A2744"));
        for (int j = 0; j < cells.length; j++) {
            TextView tv = new TextView(this);
            tv.setText(cells[j]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setPadding(dp(10), dp(6), dp(10), dp(6));
            if (j == 0) {
                tv.setTextColor(Color.parseColor("#E6B800"));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.START);
            } else if (j == 1 || j == 3 || j == 6) {
                // count columns — blue
                tv.setTextColor(Color.parseColor("#64B5F6"));
                tv.setGravity(Gravity.END);
            } else if (j == 5) {
                // profit column — green
                tv.setTextColor(Color.parseColor("#A5D6A7"));
                tv.setGravity(Gravity.END);
            } else {
                tv.setTextColor(Color.WHITE);
                tv.setGravity(Gravity.END);
            }
            tr.addView(tv);
        }
        tableMonthly.addView(tr);
    }

    private void addTotalsRow(String[] cells) {
        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(Color.parseColor("#1E2A4A"));
        for (int j = 0; j < cells.length; j++) {
            TextView tv = new TextView(this);
            tv.setText(cells[j]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setPadding(dp(10), dp(8), dp(10), dp(8));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(j == 0 ? Color.parseColor("#E6B800") : Color.WHITE);
            tv.setGravity(j == 0 ? Gravity.START : Gravity.END);
            tr.addView(tv);
        }
        tableMonthly.addView(tr);
    }

    private void addDivider() {
        View v = new View(this);
        v.setLayoutParams(new TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT, dp(1)));
        v.setBackgroundColor(Color.parseColor("#33FFFFFF"));
        tableMonthly.addView(v);
    }

    private String shortFmt(double v) {
        if (v >= 10_000_000) return String.format("%.2fCr", v / 10_000_000);
        if (v >= 100_000)    return String.format("%.2fL", v / 100_000);
        if (v >= 1_000)      return String.format("%.1fK", v / 1_000);
        return fmt.format(v);
    }

    private int dp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
            getResources().getDisplayMetrics());
    }
}
