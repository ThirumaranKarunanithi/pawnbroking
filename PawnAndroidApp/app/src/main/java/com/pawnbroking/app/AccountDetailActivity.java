package com.pawnbroking.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pawnbroking.app.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccountDetailActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TableLayout tableDetail;
    private TextView tvCount;

    private String companyId, companyName, date, type, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        companyId   = getIntent().getStringExtra("companyId");
        companyName = getIntent().getStringExtra("companyName");
        date        = getIntent().getStringExtra("date");
        type        = getIntent().getStringExtra("type");
        title       = getIntent().getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : "Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.tvCompanyName)).setText(companyName);
        ((TextView) findViewById(R.id.tvDate)).setText(date);

        progressBar = findViewById(R.id.progressBar);
        tableDetail = findViewById(R.id.tableDetail);
        tvCount     = findViewById(R.id.tvCount);

        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        tableDetail.removeAllViews();
        tvCount.setVisibility(View.GONE);

        ApiService.getTodaysAccountDetails(companyId, date, type, new ApiService.Callback<JSONObject>() {
            @Override public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    bind(data);
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AccountDetailActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void bind(JSONObject data) {
        JSONArray headers = data.optJSONArray("headers");
        JSONArray rows    = data.optJSONArray("rows");
        int count         = data.optInt("count", 0);

        tvCount.setText(count + " record" + (count != 1 ? "s" : ""));
        tvCount.setVisibility(View.VISIBLE);

        if (headers == null) return;

        // Header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#1E2A4A"));
        for (int i = 0; i < headers.length(); i++) {
            headerRow.addView(makeHeader(headers.optString(i, "")));
        }
        tableDetail.addView(headerRow);

        // Divider
        View div = new View(this);
        TableLayout.LayoutParams divLp = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, dp(1));
        div.setLayoutParams(divLp);
        div.setBackgroundColor(Color.parseColor("#33FFFFFF"));
        tableDetail.addView(div);

        if (rows == null) return;

        // Data rows
        for (int i = 0; i < rows.length(); i++) {
            JSONArray row = rows.optJSONArray(i);
            if (row == null) continue;

            TableRow tr = new TableRow(this);
            int bgColor = (i % 2 == 0)
                    ? Color.parseColor("#16213E")
                    : Color.parseColor("#1A2744");
            tr.setBackgroundColor(bgColor);

            for (int j = 0; j < row.length(); j++) {
                String cellText = row.optString(j, "");
                // Right-align if it looks like a number
                boolean isAmount = false;
                try { Double.parseDouble(cellText.replace(",", "")); isAmount = true; }
                catch (Exception ignored) {}
                tr.addView(makeCell(cellText, isAmount));
            }
            tableDetail.addView(tr);
        }
    }

    private TextView makeHeader(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#E6B800"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(dp(10), dp(8), dp(10), dp(8));
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    private TextView makeCell(String text, boolean rightAlign) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setPadding(dp(10), dp(6), dp(10), dp(6));
        tv.setGravity(rightAlign ? Gravity.END : Gravity.START);
        return tv;
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                getResources().getDisplayMetrics());
    }
}
