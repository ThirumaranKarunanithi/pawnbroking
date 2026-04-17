package com.pawnbroking.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pawnbroking.app.models.Company;
import com.pawnbroking.app.services.ApiService;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private Spinner spinnerCompany;
    private ProgressBar progressCompany, progressDashboard;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvDate, tvOpenedToday, tvClosedToday, tvActiveBills,
                     tvTotalLoan, tvGivenToday, tvReceivedToday, tvNoCompany;
    private View layoutContent;

    private List<Company> companies = new ArrayList<>();
    private Company selectedCompany;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
    private final String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCompany   = findViewById(R.id.spinnerCompany);
        progressCompany  = findViewById(R.id.progressCompany);
        progressDashboard= findViewById(R.id.progressDashboard);
        swipeRefresh     = findViewById(R.id.swipeRefresh);
        tvDate           = findViewById(R.id.tvDate);
        tvOpenedToday    = findViewById(R.id.tvOpenedToday);
        tvClosedToday    = findViewById(R.id.tvClosedToday);
        tvActiveBills    = findViewById(R.id.tvActiveBills);
        tvTotalLoan      = findViewById(R.id.tvTotalLoan);
        tvGivenToday     = findViewById(R.id.tvGivenToday);
        tvReceivedToday  = findViewById(R.id.tvReceivedToday);
        tvNoCompany      = findViewById(R.id.tvNoCompany);
        layoutContent    = findViewById(R.id.layoutContent);

        tvDate.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));

        swipeRefresh.setOnRefreshListener(() -> {
            loadDashboard();
            swipeRefresh.setRefreshing(false);
        });

        toolbar.inflateMenu(R.menu.menu_home);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_refresh) { loadDashboard(); return true; }
            if (item.getItemId() == R.id.action_logout)  { logout(); return true; }
            return false;
        });

        // ── Bill buttons ──────────────────────────────────────────────────────
        findViewById(R.id.btnGoldBills).setOnClickListener(v   -> openBills("GOLD",   "OPENED"));
        findViewById(R.id.btnSilverBills).setOnClickListener(v -> openBills("SILVER", "OPENED"));
        findViewById(R.id.btnAllActive).setOnClickListener(v   -> openBills("ALL",    "OPENED"));
        findViewById(R.id.btnClosed).setOnClickListener(v      -> openBills("ALL",    "CLOSED"));

        // ── Stock buttons ─────────────────────────────────────────────────────
        findViewById(R.id.btnGoldStock).setOnClickListener(v   -> openStock("GOLD"));
        findViewById(R.id.btnSilverStock).setOnClickListener(v -> openStock("SILVER"));
        findViewById(R.id.btnAllStock).setOnClickListener(v    -> openStock("ALL"));

        // ── Account buttons ───────────────────────────────────────────────────
        findViewById(R.id.btnTodaysAccount).setOnClickListener(v -> openTodaysAccount());

        // ── Report buttons ────────────────────────────────────────────────────
        findViewById(R.id.btnMonthlyReport).setOnClickListener(v  -> openMonthlyReport());
        findViewById(R.id.btnTrialBalance).setOnClickListener(v   -> openTrialBalance());

        // ── Customer button ───────────────────────────────────────────────────
        findViewById(R.id.btnCustomers).setOnClickListener(v -> openCustomers());

        loadCompanies();
    }

    private void loadCompanies() {
        progressCompany.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        tvNoCompany.setVisibility(View.GONE);

        ApiService.getCompanies(new ApiService.Callback<List<Company>>() {
            @Override public void onSuccess(List<Company> result) {
                runOnUiThread(() -> {
                    progressCompany.setVisibility(View.GONE);
                    companies = result;
                    if (companies.isEmpty()) {
                        tvNoCompany.setVisibility(View.VISIBLE);
                        return;
                    }
                    layoutContent.setVisibility(View.VISIBLE);
                    ArrayAdapter<Company> adapter = new ArrayAdapter<>(
                        HomeActivity.this, android.R.layout.simple_spinner_item, companies);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCompany.setAdapter(adapter);
                    spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                            selectedCompany = companies.get(pos);
                            loadDashboard();
                        }
                        @Override public void onNothingSelected(AdapterView<?> p) {}
                    });
                    selectedCompany = companies.get(0);
                    loadDashboard();
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    progressCompany.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadDashboard() {
        if (selectedCompany == null) return;
        progressDashboard.setVisibility(View.VISIBLE);

        ApiService.getDashboard(selectedCompany.id, today, new ApiService.Callback<JSONObject>() {
            @Override public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    progressDashboard.setVisibility(View.GONE);
                    tvOpenedToday.setText(String.valueOf(data.optInt("openedToday", 0)));
                    tvClosedToday.setText(String.valueOf(data.optInt("closedToday", 0)));
                    tvActiveBills.setText(String.valueOf(data.optInt("totalOpenBills", 0)));
                    tvTotalLoan.setText("₹" + fmt.format(toDouble(data.opt("total_loan_amount"))));
                    tvGivenToday.setText("₹" + fmt.format(toDouble(data.opt("given_today"))));
                    tvReceivedToday.setText("₹" + fmt.format(toDouble(data.opt("received_today"))));
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    progressDashboard.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Dashboard error: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ── Navigation helpers ────────────────────────────────────────────────────

    private void openBills(String type, String status) {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, BillsActivity.class);
        i.putExtra("companyId",   selectedCompany.id);
        i.putExtra("companyName", selectedCompany.name);
        i.putExtra("type",   type);
        i.putExtra("status", status);
        startActivity(i);
    }

    private void openStock(String materialType) {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, StockDetailsActivity.class);
        i.putExtra("companyId",    selectedCompany.id);
        i.putExtra("companyName",  selectedCompany.name);
        i.putExtra("materialType", materialType);
        startActivity(i);
    }

    private void openTodaysAccount() {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, TodaysAccountActivity.class);
        i.putExtra("companyId",   selectedCompany.id);
        i.putExtra("companyName", selectedCompany.name);
        startActivity(i);
    }

    private void openMonthlyReport() {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, MonthlyReportActivity.class);
        i.putExtra("companyId",   selectedCompany.id);
        i.putExtra("companyName", selectedCompany.name);
        startActivity(i);
    }

    private void openTrialBalance() {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, TrialBalanceActivity.class);
        i.putExtra("companyId",   selectedCompany.id);
        i.putExtra("companyName", selectedCompany.name);
        startActivity(i);
    }

    private void openCustomers() {
        if (selectedCompany == null) return;
        Intent i = new Intent(this, CustomersActivity.class);
        i.putExtra("companyId",   selectedCompany.id);
        i.putExtra("companyName", selectedCompany.name);
        startActivity(i);
    }

    private void logout() {
        ApiService.logout(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private double toDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return 0.0; }
    }
}
