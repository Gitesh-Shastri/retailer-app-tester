package com.medicento.retailerappmedi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.OrderMedicineAdapter;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.OrderedMedicineAdapter;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.SalesPharmacy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class OrderConfirmed extends AppCompatActivity {

    TextView mOrderIdTv, mSelectedPharmacyTv, mDeliveryDateTv, mTotalCostTv;
    SalesPharmacy mSelectedPharmacy;
    RecyclerView mRecyclerView;
    OrderMedicineAdapter mAdapter;
    Button mShareBtn,mNewOrder;
    ListView listview = null;

    float overallCost;

    String mOrderId, mDeliveryDate;
    SalesPerson sp;

    String orderShareDetails;
    JSONObject activityObject;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderConfirmed.this, PlaceOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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


        mRecyclerView = findViewById(R.id.order_confirmed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ArrayList<OrderedMedicine> orderedMedicines = (ArrayList<OrderedMedicine>) getIntent().getSerializableExtra("orderDetails");

        mAdapter = new OrderMedicineAdapter(this, orderedMedicines);
        mRecyclerView.setAdapter(mAdapter);

        for(OrderedMedicine orderedMedicine: orderedMedicines) {
            overallCost += orderedMedicine.getCost();
        }


        if(i != null && i.hasExtra("order_id")) {
            mOrderIdTv.setText(i.getStringExtra("order_id"));
            mDeliveryDateTv.setText(i.getStringExtra("slots"));
            mTotalCostTv.setText(String.format("Rs.%.2f", i.getFloatExtra("TotalCost", 0f)));

            mSelectedPharmacyTv.setText(i.getStringExtra("pharmacy"));
        }

        mShareBtn = findViewById(R.id.share_order_btn);

        orderShareDetails = "*"+getResources().getString(R.string.order_mode)+"* \n"+
                "*"+getResources().getString(R.string.order_person_name) + "* : Retailer Partner\n"+
                "*Pharmacy Name*: " + i.getStringExtra("pharmacy") +
                "\n\n*Order ID*: " + mOrderIdTv.getText().toString() +
                "\n*Total Cost*: " + "Rs. " + mTotalCostTv.getText().toString() +
                "\n*Expected Delivery*: " + mDeliveryDateTv.getText().toString() + "\n\n" +
                "*Medicento Retailer Order Summary:* \n"+
                "Medicine Name | Qty | Approx. Cost \n";

        for (OrderedMedicine orderedMedicine: orderedMedicines) {
            orderShareDetails += orderedMedicine.getMedicineName() +
                    " | " + orderedMedicine.getQty() +
                    " | Rs." + orderedMedicine.getCost() + "\n";
        }

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
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Paper.init(this);
        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        try {
            activityObject.put("end_time", System.currentTimeMillis() + "");
            activityObject.put("android_id", androidId);
            if (sp != null && sp.getmAllocatedPharmaId() != null) {
                activityObject.put("pharmacy_id", sp.getmAllocatedPharmaId());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://54.161.199.63:8080/api/app/record_activity/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("activity_name", activityObject.getString("activity_name"));
                    params.put("start_time", activityObject.getString("start_time"));
                    params.put("end_time", activityObject.getString("end_time"));
                    params.put("android_id", activityObject.getString("android_id"));
                    params.put("pharmacy_id", activityObject.getString("pharmacy_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityObject = new JSONObject();
        try {
            activityObject.put("activity_name", "OrderConfirmed");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
