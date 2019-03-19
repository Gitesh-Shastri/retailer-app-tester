package com.medicento.retailerappmedi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.OrderMedicineAdapter;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.OrderedMedicineAdapter;
import com.medicento.retailerappmedi.data.SalesPharmacy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderConfirmed extends AppCompatActivity {

    TextView mOrderIdTv, mSelectedPharmacyTv, mDeliveryDateTv, mTotalCostTv;
    SalesPharmacy mSelectedPharmacy;
    RecyclerView mRecyclerView;
    BottomSheetBehavior mBottomSheetBehavior;
    OrderMedicineAdapter mAdapter;
    Button mShareBtn,mNewOrder;
    ListView listview = null;

    float overallCost;

    String mOrderId, mDeliveryDate;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderConfirmed.this, PlaceOrderActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);
        Intent i = getIntent();


        mOrderIdTv = findViewById(R.id.order_id_tv);
        mSelectedPharmacyTv = findViewById(R.id.selected_pharmacy_tv);
        mDeliveryDateTv = findViewById(R.id.delivery_date_tv);
        mTotalCostTv = findViewById(R.id.total_cost_tv);
        View bottomView = findViewById(R.id.bottom_sheet);


        mRecyclerView = findViewById(R.id.order_confirmed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ArrayList<OrderedMedicine> orderedMedicines = (ArrayList<OrderedMedicine>) getIntent().getSerializableExtra("orderDetails");

        mAdapter = new OrderMedicineAdapter(this, orderedMedicines);
        mRecyclerView.setAdapter(mAdapter);



        for(OrderedMedicine orderedMedicine: orderedMedicines) {

            overallCost += orderedMedicine.getCost();
        }

        mOrderIdTv.setText(mOrderId);
        mDeliveryDateTv.setText(mDeliveryDate);
        mSelectedPharmacyTv.setText(getIntent().getStringExtra("pharmacy"));
        mTotalCostTv.setText(String.valueOf(overallCost));
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomView);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mShareBtn = findViewById(R.id.share_order_btn);
        mSelectedPharmacyTv.setText(i.getStringExtra("pharmacy"));
        mTotalCostTv.setText(i.getStringExtra("TotalCost"));
        String json = i.getStringExtra("json");
        String details[] = extractIdAndDateFromJson(json);
        mOrderIdTv.setText(details[0]);
        mDeliveryDateTv.setText(i.getStringExtra("slots"));
        mTotalCostTv.setText(details[2]);
        String date = details[1];
        String time = date.substring(0,10);
        final String orderShareDetails = "*"+getResources().getString(R.string.order_mode)+"* \n"+
                "*"+getResources().getString(R.string.order_person_name) + "* : " +i.getStringExtra("pharmacy") +"\n"+
                "*"+getResources().getString(R.string.summary) +"*\n"+
                "*Pharmacy Name*: " + i.getStringExtra("pharmacy") +
                "\n*Order id*: " + details[0] +
                "\n*Total Cost*: " + "Rs. " + mTotalCostTv.getText().toString() +
                "\n*Delivery schedule*: " + i.getStringExtra("slots");
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SUBJECT, "Medicento Order details");
                intent.putExtra(Intent.EXTRA_TEXT, orderShareDetails);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    public void new_order(View view) {
        Intent intent = new Intent(OrderConfirmed.this, PlaceOrderActivity.class);
        finish();
        startActivity(intent);
    }

    private static String[] extractIdAndDateFromJson(String jsonResponse) {
        String details[] = new String[3];
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            details[0] = jsonObject.getString("order_id");
            details[1] = jsonObject.getString("delivery_date");
            details[2] = ""+jsonObject.getInt("grand_total");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }
}
