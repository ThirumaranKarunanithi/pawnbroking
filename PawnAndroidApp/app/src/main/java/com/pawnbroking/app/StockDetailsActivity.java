package com.pawnbroking.app;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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

    // ── View mode ──────────────────────────────────────────────────────────────
    private enum ViewMode { COMPANY_ALONE, REPLEDGE_ALONE, ALL_DETAILS }
    private ViewMode currentMode = ViewMode.COMPANY_ALONE;

    // ── Filter type constants ──────────────────────────────────────────────────
    private static final String FILTER_COMP_DATE     = "COMPANY OPENED DATE";
    private static final String FILTER_COMP_AMOUNT   = "COMPANY BILL AMOUNT";
    private static final String FILTER_CUSTOMER_NAME = "CUSTOMER NAME";
    private static final String FILTER_REPL_DATE     = "REPLEDGE OPENED DATE";
    private static final String FILTER_REPL_NAME     = "REPLEDGE NAME";

    // ── Active filter model ────────────────────────────────────────────────────
    private static class ActiveFilter {
        String type, label, value1, value2;
        ActiveFilter(String type, String label, String value1, String value2) {
            this.type = type; this.label = label;
            this.value1 = value1; this.value2 = value2;
        }
    }
    private final List<ActiveFilter> activeFilters = new ArrayList<>();
    private String pendingFilterType = FILTER_COMP_DATE;

    // ── Views ──────────────────────────────────────────────────────────────────
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView tvCompanyName, tvSummaryCount, tvSummaryAmount, tvSummaryInterest;
    private LinearLayout layoutSummary, layoutFilterContent, layoutMaterial;
    private LinearLayout layoutDateInput, layoutAmountInput, layoutFilterChips;
    private EditText etSearch, etDateFrom, etDateTo, etAmountFrom, etAmountTo, etNameInput;
    private Button btnToggleFilters, btnModeCompany, btnModeRepledge, btnModeAll;
    private Button btnAddFilter, btnClearAllFilters;
    private Spinner spinnerMaterial, spinnerFilterType;

    private String companyId, companyName;
    private String currentMaterial = "ALL";
    private boolean filterPanelOpen = false;

    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("en", "IN"));
    private final List<JSONObject> bills = new ArrayList<>();
    private BillAdapter adapter;

    private final Runnable searchRunnable = this::load;
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

    // ── onCreate ───────────────────────────────────────────────────────────────
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

        // ── bind views ──
        tvCompanyName      = findViewById(R.id.tvCompanyName);
        tvSummaryCount     = findViewById(R.id.tvSummaryCount);
        tvSummaryAmount    = findViewById(R.id.tvSummaryAmount);
        tvSummaryInterest  = findViewById(R.id.tvSummaryInterest);
        layoutSummary      = findViewById(R.id.layoutSummary);
        progressBar        = findViewById(R.id.progressBar);
        recyclerView       = findViewById(R.id.recyclerView);
        layoutFilterContent= findViewById(R.id.layoutFilterContent);
        layoutMaterial     = findViewById(R.id.layoutMaterial);
        layoutDateInput    = findViewById(R.id.layoutDateInput);
        layoutAmountInput  = findViewById(R.id.layoutAmountInput);
        layoutFilterChips  = findViewById(R.id.layoutFilterChips);
        etSearch           = findViewById(R.id.etSearch);
        etDateFrom         = findViewById(R.id.etDateFrom);
        etDateTo           = findViewById(R.id.etDateTo);
        etAmountFrom       = findViewById(R.id.etAmountFrom);
        etAmountTo         = findViewById(R.id.etAmountTo);
        etNameInput        = findViewById(R.id.etNameInput);
        btnToggleFilters   = findViewById(R.id.btnToggleFilters);
        btnModeCompany     = findViewById(R.id.btnModeCompany);
        btnModeRepledge    = findViewById(R.id.btnModeRepledge);
        btnModeAll         = findViewById(R.id.btnModeAll);
        btnAddFilter       = findViewById(R.id.btnAddFilter);
        btnClearAllFilters = findViewById(R.id.btnClearAllFilters);
        spinnerMaterial    = findViewById(R.id.spinnerMaterial);
        spinnerFilterType  = findViewById(R.id.spinnerFilterType);

        tvCompanyName.setText(companyName);

        // ── toggle filter panel ──
        btnToggleFilters.setOnClickListener(v -> {
            filterPanelOpen = !filterPanelOpen;
            layoutFilterContent.setVisibility(filterPanelOpen ? View.VISIBLE : View.GONE);
            btnToggleFilters.setText(filterPanelOpen ? "Filters ▲" : "Filters ▼");
        });

        // ── material spinner ──
        ArrayAdapter<String> matAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, new String[]{"ALL", "GOLD", "SILVER"});
        matAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(matAdapter);
        spinnerMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                currentMaterial = matAdapter.getItem(pos);
                load();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // ── mode buttons ──
        btnModeCompany.setOnClickListener(v  -> setMode(ViewMode.COMPANY_ALONE));
        btnModeRepledge.setOnClickListener(v -> setMode(ViewMode.REPLEDGE_ALONE));
        btnModeAll.setOnClickListener(v      -> setMode(ViewMode.ALL_DETAILS));

        // ── filter type spinner ──
        buildFilterTypeSpinner();

        // ── date field pickers ──
        etDateFrom.setOnClickListener(v -> pickDate(etDateFrom));
        etDateTo.setOnClickListener(v   -> pickDate(etDateTo));

        // ── add filter ──
        btnAddFilter.setOnClickListener(v -> addFilter());

        // ── clear all filters ──
        btnClearAllFilters.setOnClickListener(v -> {
            activeFilters.clear();
            refreshChips();
            load();
        });

        // ── search ──
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                handler.removeCallbacks(searchRunnable);
                handler.postDelayed(searchRunnable, 500);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ── recycler ──
        adapter = new BillAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        load();
    }

    // ── Mode switching ─────────────────────────────────────────────────────────
    private void setMode(ViewMode mode) {
        currentMode = mode;
        int gold     = Color.parseColor("#E6B800");
        int dark     = Color.parseColor("#0D1B2A");
        int inactive = Color.parseColor("#1E2A4A");
        int grey     = Color.parseColor("#AAAAAA");

        btnModeCompany.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                mode == ViewMode.COMPANY_ALONE  ? gold : inactive));
        btnModeCompany.setTextColor(mode == ViewMode.COMPANY_ALONE  ? dark : grey);

        btnModeRepledge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                mode == ViewMode.REPLEDGE_ALONE ? gold : inactive));
        btnModeRepledge.setTextColor(mode == ViewMode.REPLEDGE_ALONE ? dark : grey);

        btnModeAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                mode == ViewMode.ALL_DETAILS    ? gold : inactive));
        btnModeAll.setTextColor(mode == ViewMode.ALL_DETAILS    ? dark : grey);

        // Material type only relevant for Company / All
        layoutMaterial.setVisibility(mode == ViewMode.REPLEDGE_ALONE ? View.GONE : View.VISIBLE);

        // Remove filters that don't apply to the new mode
        if (mode == ViewMode.COMPANY_ALONE) {
            activeFilters.removeIf(f -> f.type.equals(FILTER_REPL_DATE) || f.type.equals(FILTER_REPL_NAME));
        } else if (mode == ViewMode.REPLEDGE_ALONE) {
            activeFilters.removeIf(f -> f.type.equals(FILTER_COMP_DATE)   ||
                                        f.type.equals(FILTER_COMP_AMOUNT) ||
                                        f.type.equals(FILTER_CUSTOMER_NAME));
        }

        buildFilterTypeSpinner();
        refreshChips();
        load();
    }

    // ── Filter type spinner ───────────────────────────────────────────────────
    private void buildFilterTypeSpinner() {
        List<String> types = new ArrayList<>();
        if (currentMode != ViewMode.REPLEDGE_ALONE) {
            types.add(FILTER_COMP_DATE);
            types.add(FILTER_COMP_AMOUNT);
            types.add(FILTER_CUSTOMER_NAME);
        }
        if (currentMode != ViewMode.COMPANY_ALONE) {
            types.add(FILTER_REPL_DATE);
            types.add(FILTER_REPL_NAME);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterType.setAdapter(adapter);
        spinnerFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                pendingFilterType = types.get(pos);
                updateFilterInputVisibility();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        if (!types.isEmpty()) {
            pendingFilterType = types.get(0);
            updateFilterInputVisibility();
        }
    }

    private void updateFilterInputVisibility() {
        boolean isDate   = FILTER_COMP_DATE.equals(pendingFilterType)
                        || FILTER_REPL_DATE.equals(pendingFilterType);
        boolean isAmount = FILTER_COMP_AMOUNT.equals(pendingFilterType);
        boolean isName   = FILTER_CUSTOMER_NAME.equals(pendingFilterType)
                        || FILTER_REPL_NAME.equals(pendingFilterType);

        layoutDateInput.setVisibility(isDate   ? View.VISIBLE : View.GONE);
        layoutAmountInput.setVisibility(isAmount ? View.VISIBLE : View.GONE);
        etNameInput.setVisibility(isName     ? View.VISIBLE : View.GONE);
    }

    // ── Date picker ────────────────────────────────────────────────────────────
    private void pickDate(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            target.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    // ── Add filter ─────────────────────────────────────────────────────────────
    private void addFilter() {
        String type = pendingFilterType;
        String v1 = "", v2 = "", label;

        if (FILTER_COMP_DATE.equals(type) || FILTER_REPL_DATE.equals(type)) {
            v1 = etDateFrom.getText().toString().trim();
            v2 = etDateTo.getText().toString().trim();
            if (v1.isEmpty() && v2.isEmpty()) {
                Toast.makeText(this, "Pick at least one date", Toast.LENGTH_SHORT).show();
                return;
            }
            String prefix = FILTER_COMP_DATE.equals(type) ? "Comp Date" : "Repl Date";
            label = prefix + ": " + (v1.isEmpty() ? "…" : v1)
                           + " → " + (v2.isEmpty() ? "…" : v2);
            etDateFrom.setText("");
            etDateTo.setText("");

        } else if (FILTER_COMP_AMOUNT.equals(type)) {
            v1 = etAmountFrom.getText().toString().trim();
            v2 = etAmountTo.getText().toString().trim();
            if (v1.isEmpty() && v2.isEmpty()) {
                Toast.makeText(this, "Enter at least one amount", Toast.LENGTH_SHORT).show();
                return;
            }
            label = "Amount: " + (v1.isEmpty() ? "0" : v1)
                               + " → " + (v2.isEmpty() ? "∞" : v2);
            etAmountFrom.setText("");
            etAmountTo.setText("");

        } else {
            // CUSTOMER_NAME or REPLEDGE_NAME
            v1 = etNameInput.getText().toString().trim();
            if (v1.isEmpty()) {
                Toast.makeText(this, "Enter a name to search", Toast.LENGTH_SHORT).show();
                return;
            }
            label = (FILTER_CUSTOMER_NAME.equals(type) ? "Customer" : "Repl Name") + ": " + v1;
            etNameInput.setText("");
        }

        // Replace existing filter of the same type
        activeFilters.removeIf(f -> f.type.equals(type));
        activeFilters.add(new ActiveFilter(type, label, v1, v2));

        refreshChips();
        load();
    }

    // ── Chips ──────────────────────────────────────────────────────────────────
    private void refreshChips() {
        layoutFilterChips.removeAllViews();
        if (activeFilters.isEmpty()) {
            btnClearAllFilters.setVisibility(View.GONE);
            return;
        }
        btnClearAllFilters.setVisibility(View.VISIBLE);
        for (ActiveFilter f : new ArrayList<>(activeFilters)) {
            layoutFilterChips.addView(makeChip(f));
        }
    }

    private View makeChip(ActiveFilter f) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setGravity(android.view.Gravity.CENTER_VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#1E2A4A"));
        bg.setStroke(dp(1), Color.parseColor("#E6B800"));
        bg.setCornerRadius(dp(14));
        chip.setBackground(bg);

        int px6 = dp(6); int px3 = dp(3); int px10 = dp(10);
        chip.setPadding(px10, px3, px6, px3);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(6));
        chip.setLayoutParams(lp);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(f.label);
        tvLabel.setTextColor(Color.WHITE);
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        chip.addView(tvLabel);

        TextView tvX = new TextView(this);
        tvX.setText("  ×");
        tvX.setTextColor(Color.parseColor("#E6B800"));
        tvX.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvX.setOnClickListener(v -> {
            activeFilters.remove(f);
            refreshChips();
            load();
        });
        chip.addView(tvX);
        return chip;
    }

    // ── Load data ──────────────────────────────────────────────────────────────
    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        layoutSummary.setVisibility(View.GONE);
        String search = etSearch.getText().toString().trim();

        // Extract active filter values
        String compDateFrom = null, compDateTo = null;
        String amountFrom   = null, amountTo   = null;
        String customerName = null;
        String repledgeName = null;
        String replDateFrom = null, replDateTo = null;

        for (ActiveFilter f : activeFilters) {
            switch (f.type) {
                case FILTER_COMP_DATE:     compDateFrom = f.value1; compDateTo = f.value2; break;
                case FILTER_COMP_AMOUNT:   amountFrom   = f.value1; amountTo   = f.value2; break;
                case FILTER_CUSTOMER_NAME: customerName = f.value1; break;
                case FILTER_REPL_DATE:     replDateFrom = f.value1; replDateTo = f.value2; break;
                case FILTER_REPL_NAME:     repledgeName = f.value1; break;
            }
        }

        ApiService.Callback<JSONObject> cb = new ApiService.Callback<JSONObject>() {
            @Override public void onSuccess(JSONObject data) {
                final boolean isRepledge = (currentMode == ViewMode.REPLEDGE_ALONE);
                runOnUiThread(() -> bindData(data, isRepledge));
            }
            @Override public void onError(String msg) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StockDetailsActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                });
            }
        };

        switch (currentMode) {
            case COMPANY_ALONE:
                ApiService.getStock(companyId, currentMaterial, search,
                    compDateFrom, compDateTo, customerName, amountFrom, amountTo,
                    0, 200, cb);
                break;
            case REPLEDGE_ALONE:
                ApiService.getRepledgeStock(companyId, currentMaterial, search,
                    repledgeName, replDateFrom, replDateTo, 0, 200, cb);
                break;
            case ALL_DETAILS:
                ApiService.getAllStock(companyId, currentMaterial, search,
                    compDateFrom, compDateTo, customerName, amountFrom, amountTo,
                    repledgeName, replDateFrom, replDateTo, 0, 200, cb);
                break;
        }
    }

    private void bindData(JSONObject data, boolean isRepledge) {
        progressBar.setVisibility(View.GONE);
        long   count    = data.optLong("total", 0);
        double amount   = data.optDouble("totalAmount", 0);
        double interest = data.optDouble("totalInterest", 0);

        tvSummaryCount.setText(count + " bill" + (count != 1 ? "s" : ""));
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
        adapter.setMode(currentMode, isRepledge);
        adapter.notifyDataSetChanged();
    }

    private String shortFmt(double v) {
        if (v >= 100_000) return String.format("%.1fL", v / 100_000);
        if (v >= 1_000)   return String.format("%.1fK", v / 1_000);
        return fmt.format(v);
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
            getResources().getDisplayMetrics());
    }

    // ── RecyclerView Adapter ────────────────────────────────────────────────────
    class BillAdapter extends RecyclerView.Adapter<BillAdapter.VH> {
        private boolean isRepledge = false;
        private ViewMode mode      = ViewMode.COMPANY_ALONE;

        void setMode(ViewMode m, boolean r) { mode = m; isRepledge = r; }

        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_bill, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(VH h, int pos) {
            JSONObject b = bills.get(pos);

            if (isRepledge) {
                // ── Repledge Alone ──
                h.tvBillNo.setText(b.optString("repledge_bill_id", ""));
                h.tvCustomer.setText(b.optString("repledge_name", "")
                    + " — " + b.optString("repledge_bill_number", ""));
                h.tvItems.setText("Company Bill: " + b.optString("company_bill_number", ""));
                h.tvDate.setText(dateStr(b.optString("opening_date", "")));
                h.tvAmount.setText("₹" + fmt.format(b.optDouble("amount", 0)));
                h.tvInterest.setText("₹" + fmt.format(b.optDouble("interest", 0)));
                h.tvWeight.setText("");
            } else {
                // ── Company Alone or All Details ──
                h.tvBillNo.setText(b.optString("bill_number", ""));

                String cust = b.optString("customer_name", "");
                String spouseType = b.optString("spouse_type", "");
                String spouseName = b.optString("spouse_name", "");
                if (!spouseName.isEmpty()) cust += " " + spouseType + " " + spouseName;
                String area = b.optString("area", "");
                if (!area.isEmpty()) cust += "\n" + area;

                // All Details: show repledge info below if present
                if (mode == ViewMode.ALL_DETAILS) {
                    String replName = b.optString("repledge_name", "");
                    if (!replName.isEmpty()) {
                        String replDate = dateStr(b.optString("repledge_date", ""));
                        cust += "\nRepledge: " + replName + (replDate.isEmpty() ? "" : " (" + replDate + ")");
                    }
                }

                h.tvCustomer.setText(cust);
                h.tvItems.setText(b.optString("items", ""));
                h.tvDate.setText(dateStr(b.optString("opening_date", "")));
                h.tvAmount.setText("₹" + fmt.format(b.optDouble("amount", 0)));
                h.tvInterest.setText("₹" + fmt.format(b.optDouble("interest", 0)));
                h.tvWeight.setText(String.format("%.2f", b.optDouble("gross_weight", 0)));
            }

            String mat = b.optString("material_type", "");
            h.tvMaterial.setText(mat);
            h.tvMaterial.setTextColor("GOLD".equalsIgnoreCase(mat)
                ? Color.parseColor("#E6B800") : Color.parseColor("#AAAAAA"));

            String status = b.optString("status", "");
            h.tvStatus.setText(status);
            h.tvStatus.setTextColor("LOCKED".equalsIgnoreCase(status)
                ? Color.parseColor("#FF9800") : Color.parseColor("#4CAF50"));
        }

        @Override public int getItemCount() { return bills.size(); }

        private String dateStr(String raw) {
            if (raw == null || raw.isEmpty()) return "";
            return raw.replace("T", " ").substring(0, Math.min(10, raw.length()));
        }

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
