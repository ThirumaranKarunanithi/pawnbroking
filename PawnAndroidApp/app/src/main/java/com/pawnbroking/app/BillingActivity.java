package com.pawnbroking.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pawnbroking.app.services.ApiService;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class BillingActivity extends AppCompatActivity {

    private String companyId;
    private String selectedMaterialType = "GOLD";

    // Gold/Silver toggle
    private TextView chipGold, chipSilver;

    // Bill search
    private EditText     etBillNumber;
    private Button       btnSearchBill;
    private LinearLayout layoutBillDetails;   // entire detail body, hidden until search

    // Bill info (read-only)
    private TextView tvMaterialType, tvOpeningDate, tvStatus;
    private TextView tvCreatedBy, tvCreatedAt;
    private LinearLayout layoutCreatedInfo;

    // Customer (read-only)
    private TextView tvCustomerName, tvGender, tvSpouseType, tvSpouseName;
    private TextView tvMobile, tvDoorNo, tvStreet, tvArea, tvCity;

    // Jewel (read-only)
    private TextView tvItems, tvGrossWt, tvNetWt, tvPurity, tvAmount;

    // Calculated amounts (read-only, from DB)
    private TextView tvInterest, tvDocCharge, tvTakenAmt, tvToGive;
    private TextView tvGivenAmount, tvIntPerMonth, tvRatePerGm;

    // Additional (read-only)
    private TextView tvNominee, tvIdProofType, tvIdProofNumber;
    private TextView tvReferredBy, tvCustomerId, tvOccupation;
    private TextView tvPhysicalLocation, tvNote, tvAcceptedDate;

    private final NumberFormat inFmt =
        NumberFormat.getNumberInstance(new Locale("en", "IN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        companyId = getIntent().getStringExtra("companyId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Billing Screen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        bindViews();
        setupChips();
        setupSearchBar();
    }

    // ── View binding ──────────────────────────────────────────────────────────

    private void bindViews() {
        chipGold          = findViewById(R.id.chipGold);
        chipSilver        = findViewById(R.id.chipSilver);
        etBillNumber      = findViewById(R.id.etBillNumber);
        btnSearchBill     = findViewById(R.id.btnSearchBill);
        layoutBillDetails = findViewById(R.id.layoutBillDetails);

        // Bill info
        tvMaterialType    = findViewById(R.id.tvMaterialType);
        tvOpeningDate     = findViewById(R.id.tvOpeningDate);
        tvStatus          = findViewById(R.id.tvStatus);
        layoutCreatedInfo = findViewById(R.id.layoutCreatedInfo);
        tvCreatedBy       = findViewById(R.id.tvCreatedBy);
        tvCreatedAt       = findViewById(R.id.tvCreatedAt);

        // Customer
        tvCustomerName    = findViewById(R.id.tvCustomerName);
        tvGender          = findViewById(R.id.tvGender);
        tvSpouseType      = findViewById(R.id.tvSpouseType);
        tvSpouseName      = findViewById(R.id.tvSpouseName);
        tvMobile          = findViewById(R.id.tvMobile);
        tvDoorNo          = findViewById(R.id.tvDoorNo);
        tvStreet          = findViewById(R.id.tvStreet);
        tvArea            = findViewById(R.id.tvArea);
        tvCity            = findViewById(R.id.tvCity);

        // Jewel
        tvItems           = findViewById(R.id.tvItems);
        tvGrossWt         = findViewById(R.id.tvGrossWt);
        tvNetWt           = findViewById(R.id.tvNetWt);
        tvPurity          = findViewById(R.id.tvPurity);
        tvAmount          = findViewById(R.id.tvAmount);

        // Calculated
        tvInterest        = findViewById(R.id.tvInterest);
        tvDocCharge       = findViewById(R.id.tvDocCharge);
        tvTakenAmt        = findViewById(R.id.tvTakenAmt);
        tvToGive          = findViewById(R.id.tvToGive);
        tvGivenAmount     = findViewById(R.id.tvGivenAmount);
        tvIntPerMonth     = findViewById(R.id.tvIntPerMonth);
        tvRatePerGm       = findViewById(R.id.tvRatePerGm);

        // Additional
        tvNominee         = findViewById(R.id.tvNominee);
        tvIdProofType     = findViewById(R.id.tvIdProofType);
        tvIdProofNumber   = findViewById(R.id.tvIdProofNumber);
        tvReferredBy      = findViewById(R.id.tvReferredBy);
        tvCustomerId      = findViewById(R.id.tvCustomerId);
        tvOccupation      = findViewById(R.id.tvOccupation);
        tvPhysicalLocation= findViewById(R.id.tvPhysicalLocation);
        tvNote            = findViewById(R.id.tvNote);
        tvAcceptedDate    = findViewById(R.id.tvAcceptedDate);
    }

    // ── Material type chips ───────────────────────────────────────────────────

    private void setupChips() {
        chipGold.setOnClickListener(v -> selectMaterial("GOLD"));
        chipSilver.setOnClickListener(v -> selectMaterial("SILVER"));
        selectMaterial("GOLD");
    }

    private void selectMaterial(String type) {
        selectedMaterialType = type;
        boolean gold = "GOLD".equals(type);
        chipGold.setBackgroundResource(gold ? R.drawable.chip_selected : R.drawable.chip_unselected);
        chipGold.setTextColor(getResources().getColor(gold ? R.color.bg_dark : R.color.gold, getTheme()));
        chipSilver.setBackgroundResource(gold ? R.drawable.chip_unselected : R.drawable.chip_selected);
        chipSilver.setTextColor(getResources().getColor(gold ? R.color.gold : R.color.bg_dark, getTheme()));
        layoutBillDetails.setVisibility(View.GONE);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    private void setupSearchBar() {
        btnSearchBill.setOnClickListener(v -> searchBill());
        etBillNumber.setOnEditorActionListener((v, actionId, event) -> {
            searchBill(); return true;
        });
    }

    private void searchBill() {
        String billNo = etBillNumber.getText().toString().trim();
        if (billNo.isEmpty()) {
            Toast.makeText(this, "Enter a bill number", Toast.LENGTH_SHORT).show();
            return;
        }
        btnSearchBill.setEnabled(false);
        btnSearchBill.setText("…");
        layoutBillDetails.setVisibility(View.GONE);

        ApiService.findBill(companyId, billNo, selectedMaterialType, new ApiService.Callback<JSONObject>() {
            @Override public void onSuccess(JSONObject r) {
                runOnUiThread(() -> {
                    btnSearchBill.setEnabled(true);
                    btnSearchBill.setText("SEARCH");
                    populateFromBill(r);
                });
            }
            @Override public void onError(String msg) {
                runOnUiThread(() -> {
                    btnSearchBill.setEnabled(true);
                    btnSearchBill.setText("SEARCH");
                    Toast.makeText(BillingActivity.this,
                        "Not found: " + msg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Populate all fields from fetched bill (save-mode-off view) ────────────

    private void populateFromBill(JSONObject r) {
        // Material type badge
        tvMaterialType.setText(r.optString("material_type", ""));

        // Bill info
        tvOpeningDate.setText(r.optString("opening_date", ""));
        tvStatus.setText(r.optString("status", ""));

        String createdAt = r.optString("created_date", "");
        if (!createdAt.isEmpty() && !"null".equals(createdAt)) {
            tvCreatedAt.setText(createdAt);
            tvCreatedBy.setText(r.optString("created_user_id", ""));
            layoutCreatedInfo.setVisibility(View.VISIBLE);
        }

        // Customer
        tvCustomerName.setText(r.optString("customer_name", ""));
        tvGender.setText(r.optString("gender", ""));
        tvSpouseType.setText(r.optString("spouse_type", ""));
        tvSpouseName.setText(r.optString("spouse_name", ""));
        tvMobile.setText(r.optString("mobile_number", ""));
        tvDoorNo.setText(r.optString("door_number", ""));
        tvStreet.setText(r.optString("street", ""));
        tvArea.setText(r.optString("area", ""));
        tvCity.setText(r.optString("city", ""));

        // Jewel
        tvItems.setText(r.optString("items", ""));
        tvGrossWt.setText(r.optString("gross_weight", ""));
        tvNetWt.setText(r.optString("net_weight", ""));
        tvPurity.setText(r.optString("purity", ""));
        tvAmount.setText(fmt(r.optDouble("amount", 0)));

        // Calculated amounts — exact values stored in DB (same as desktop save-mode-off)
        double interest    = r.optDouble("interest",        0);
        double docCharge   = r.optDouble("document_charge", 0);
        double takenAmt    = r.optDouble("taken_amount",    0);
        double toGive      = r.optDouble("to_give_amount",  0);
        double givenAmt    = r.optDouble("given_amount",    0);
        double intPerMonth = takenAmt - docCharge;
        double grossWt     = r.optDouble("gross_weight",    0);
        double amount      = r.optDouble("amount",          0);
        double ratePerGm   = grossWt > 0 ? Math.round(amount / grossWt) : 0;

        tvInterest.setText(   fmt(interest));
        tvDocCharge.setText(  fmt(docCharge));
        tvTakenAmt.setText(   fmt(takenAmt));
        tvToGive.setText(     fmt(toGive));
        tvGivenAmount.setText(fmt(givenAmt));
        tvIntPerMonth.setText(fmt(intPerMonth));
        tvRatePerGm.setText(  fmt(ratePerGm));

        // Additional
        tvNominee.setText(        r.optString("nominee_name",        ""));
        tvIdProofType.setText(    r.optString("cust_id_proof_type",  ""));
        tvIdProofNumber.setText(  r.optString("cust_id_proof_number",""));
        tvReferredBy.setText(     r.optString("refered_by_name",     ""));
        tvCustomerId.setText(     r.optString("customer_id",         ""));
        tvOccupation.setText(     r.optString("customer_occupation", ""));
        tvPhysicalLocation.setText(r.optString("physical_location",  ""));
        tvNote.setText(           r.optString("note",                ""));
        String accDate = r.optString("accepted_closing_date", "");
        tvAcceptedDate.setText("null".equals(accDate) ? "—" : accDate);

        layoutBillDetails.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Loaded: " + r.optString("bill_number", ""), Toast.LENGTH_SHORT).show();
    }

    private String fmt(double v) { return inFmt.format(v); }
}
