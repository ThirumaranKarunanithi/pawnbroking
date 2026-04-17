package com.pawnbroking.app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pawnbroking.app.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.*;

public class StockDetailsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView tvCompanyName, tvSummaryCount, tvSummaryAmount, tvSummaryInterest;
    private LinearLayout layoutSummary;
    private Button btnTabCompany, btnTabRepledge;
    private EditText etSearch;
    private Spinner spinnerMaterial;

    private String companyId, companyName;
    private boolean showRepledge = false;
    private String currentMaterial = "ALL";
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
    private final List<JSONObject> bills = new ArrayList<>();
    private BillAdapter adapter;

    private final Runnable searchRunnable = this::load;
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);

        companyId   = getIntent().getStringExtra("companyId");
        companyName = getIntent().getStringExtra("companyName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Stock Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvCompanyName    = findViewById(R.id.tvCompanyName);
        tvSummaryCount   = findViewById(R.id.tvSummaryCount);
        tvSummaryAmount  = findViewById(R.id.tvSummaryAmount);
        tvSummaryInterest= findViewById(R.id.tvSummaryInterest);
        layoutSummary    = findViewById(R.id.layoutSummary);
        progressBar      = findViewById(R.id.progressBar);
        recyclerView     = findViewById(R.id.recyclerView);
        btnTabCompany    = findViewById(R.id.btnTabCompany);
        btnTabRepledge   = findViewById(R.id.btnTabRepledge);
        etSearch         = findViewById(R.id.etSearch);
        spinnerMaterial  = findViewById(R.id.spinnerMaterial);

        tvCompanyName.setText(companyName);

        // Material spinner
        ArrayAdapter<String> matAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"ALL", "GOLD", "SILVER"});
        matAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(matAdapter);
        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                currentMaterial = matAdapter.getItem(pos);
                load();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // Tabs
        btnTabCompany.setOnClickListener(v -> {
            showRepledge = false;
            btnTabCompany.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E6B800")));
            btnTabCompany.setTextColor(Color.parseColor("#0D1B2A"));
            btnTabRepledge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1E2A4A")));
            btnTabRepledge.setTextColor(Color.parseColor("#FFFFFF88"));
            load();
        });
        btnTabRepledge.setOnClickListener(v -> {
            showRepledge = true;
            btnTabRepledge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E6B800")));
            btnTabRepledge.setTextColor(Color.parseColor("#0D1B2A"));
            btnTabCompany.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1E2A4A")));
            btnTabCompany.setTextColor(Color.parseColor("#FFFFFF88"));
            load();
        });

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                handler.removeCallbacks(searchRunnable);
                handler.postDelayed(searchRunnable, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        adapter = new BillAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        load();
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        layoutSummary.setVisibility(View.GONE);
        String search = etSearch.getText().toString().trim();

        if (showRepledge) {
            ApiService.getRepledgeStock(companyId, currentMaterial, search, 0, 200,
                new ApiService.Callback<JSONObject>() {
                    @Override public void onSuccess(JSONObject data) {
                        runOnUiThread(() -> bindData(data, true));
                    }
                    @Override public void onError(String msg) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StockDetailsActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
        } else {
            ApiService.getStock(companyId, currentMaterial, search, null, null, 0, 200,
                new ApiService.Callback<JSONObject>() {
                    @Override public void onSuccess(JSONObject data) {
                        runOnUiThread(() -> bindData(data, false));
                    }
                    @Override public void onError(String msg) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StockDetailsActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
        }
    }

    private void bindData(JSONObject data, boolean repledge) {
        progressBar.setVisibility(View.GONE);
        long count      = data.optLong("total", 0);
        double amount   = data.optDouble("totalAmount", 0);
        double interest = data.optDouble("totalInterest", 0);

        tvSummaryCount.setText(count + " bills");
        tvSummaryAmount.setText("₹" + shortFmt(amount));
        tvSummaryInterest.setText("Intr ₹" + shortFmt(interest));
        layoutSummary.setVisibility(View.VISIBLE);

        bills.clear();
        JSONArray arr = data.optJSONArray("bills");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o != null) bills.add(o);
            }
        }
        adapter.setRepledge(repledge);
        adapter.notifyDataSetChanged();
    }

    private String shortFmt(double v) {
        if (v >= 100_000) return String.format("%.1fL", v / 100_000);
        if (v >= 1_000)   return String.format("%.1fK", v / 1_000);
        return fmt.format(v);
    }

    // ── RecyclerView Adapter ──────────────────────────────────────────────────

    class BillAdapter extends RecyclerView.Adapter<BillAdapter.VH> {
        private boolean isRepledge = false;

        void setRepledge(boolean r) { isRepledge = r; }

        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_bill, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(VH h, int pos) {
            JSONObject b = bills.get(pos);
            if (isRepledge) {
                h.tvBillNo.setText(b.optString("repledge_bill_id", ""));
                h.tvCustomer.setText(b.optString("repledge_name", "") + " — " + b.optString("repledge_bill_number", ""));
                h.tvItems.setText("Company Bill: " + b.optString("company_bill_number", ""));
            } else {
                h.tvBillNo.setText(b.optString("bill_number", ""));
                String cust = b.optString("customer_name", "");
                String spouseType = b.optString("spouse_type", "");
                String spouseName = b.optString("spouse_name", "");
                if (!spouseName.isEmpty()) cust += " " + spouseType + " " + spouseName;
                String area = b.optString("area", "");
                if (!area.isEmpty()) cust += "\n" + area;
                h.tvCustomer.setText(cust);
                h.tvItems.setText(b.optString("items", ""));
            }

            String mat = b.optString("material_type", "");
            h.tvMaterial.setText(mat);
            h.tvMaterial.setTextColor(mat.equalsIgnoreCase("GOLD")
                ? Color.parseColor("#E6B800") : Color.parseColor("#AAAAAA"));

            String status = b.optString("status", "");
            h.tvStatus.setText(status);
            h.tvStatus.setTextColor("LOCKED".equalsIgnoreCase(status)
                ? Color.parseColor("#FF9800") : Color.parseColor("#4CAF50"));

            h.tvAmount.setText("₹" + fmt.format(b.optDouble("amount", 0)));
            h.tvInterest.setText("₹" + fmt.format(b.optDouble("interest", 0)));
            h.tvWeight.setText(String.format("%.2f", b.optDouble("gross_weight", 0)));
            h.tvDate.setText(b.optString("opening_date", "").replace("T", " ").substring(0,
                Math.min(10, b.optString("opening_date", "").length())));
        }

        @Override public int getItemCount() { return bills.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvBillNo, tvMaterial, tvStatus, tvCustomer, tvItems,
                     tvAmount, tvInterest, tvWeight, tvDate;
            VH(View v) {
                super(v);
                tvBillNo   = v.findViewById(R.id.tvBillNo);
                tvMaterial = v.findViewById(R.id.tvMaterial);
                tvStatus   = v.findViewById(R.id.tvStatus);
                tvCustomer = v.findViewById(R.id.tvCustomer);
                tvItems    = v.findViewById(R.id.tvItems);
                tvAmount   = v.findViewById(R.id.tvAmount);
                tvInterest = v.findViewById(R.id.tvInterest);
                tvWeight   = v.findViewById(R.id.tvWeight);
                tvDate     = v.findViewById(R.id.tvDate);
            }
        }
    }
}
