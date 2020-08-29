package com.medicento.retailerappmedi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.ItemOrderedCartList;
import com.medicento.retailerappmedi.adapter.MedicineAdapter;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import io.paperdb.Paper;

public class CartPageOrderActivity extends AppCompatActivity {

    ArrayList<OrderedMedicine> orderedMedicines;
    RecyclerView item_rv;
    MedicineAdapter medicineAdapter;
    Button pay_rs, confirm;
    TextView price, total_amount_tv;
    float cost = 0;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_cart_page_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        if (getIntent() != null && getIntent().hasExtra("orders")) {
            orderedMedicines = (ArrayList<OrderedMedicine>) getIntent().getSerializableExtra("orders");
        }

        pay_rs = findViewById(R.id.pay_rs);
        price = findViewById(R.id.price);
        item_rv = findViewById(R.id.item_rv);
        confirm = findViewById(R.id.confirm);
        total_amount_tv = findViewById(R.id.total_amount_tv);

        for (int i=0;i<orderedMedicines.size();i++) {
            cost += orderedMedicines.get(i).getCost();
        }

        item_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        medicineAdapter = new MedicineAdapter(orderedMedicines, this);
        item_rv.setAdapter(medicineAdapter);

        total_amount_tv.setText("₹  " + cost);
        price.setText("₹  "+ cost);
        pay_rs.setText("Pay ₹ " + cost);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String data = "*Partner Type*: ";
                    if (sp.getType().equals("Pharmacy")) {
                        data += "Retailer\n";
                    } else {
                        data += "Distributor\n";
                    }
                    data += "*Name*: " + sp.getName() + "\n*City*: " + sp.getCity_name() + "\n*State*: " +sp.getState_name() + "\n*Query regarding below Medicines *:\n";
                    for (int i=0;i<orderedMedicines.size();i++) {
                        data += (i+1) + ". " + orderedMedicines.get(i).getMedicineName();
                    }

                    Intent share_intent = new Intent();
                    String url = "https://api.whatsapp.com/send?phone=+919731785240&text=" + URLEncoder.encode(data, "UTF-8");
                    share_intent.setPackage("com.whatsapp");
                    share_intent.setData(Uri.parse(url));
                    startActivity(share_intent);
                } catch (Exception e) {
                    if (ActivityCompat.checkSelfPermission(CartPageOrderActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(CartPageOrderActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat
                                .requestPermissions(
                                        CartPageOrderActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        1001);
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:+919731785240"));
                        startActivity(callIntent);
                    }
                }
            }
        });

        pay_rs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMinimum();
            }
        });
    }

    String cost_value = "";
    private void checkMinimum() {

        RequestQueue requestQueue = Volley.newRequestQueue(CartPageOrderActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/check_cost/?cost="+cost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            cost_value = JsonUtils.getJsonValueFromKey(jsonObject, "cost");
                            if (JsonUtils.getBooleanValueFromJsonKey(jsonObject, "is_valid")) {
                                startPayment();
                            } else {
                                Toast.makeText(CartPageOrderActivity.this, "Minimum Cart Value: Rs." + cost_value + " is needed to place an order! Please add Medicines accordingly.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(CartPageOrderActivity.this, "Minimum Cart Value: Rs." + cost_value + " is needed to place an order! Please add Medicines accordingly.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartPageOrderActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.mdl);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "mediclick heathcare services private limited");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #" + System.currentTimeMillis());
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", ((cost) * 100) + "");

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("data", "Error in starting Razorpay Checkout", e);
        }
    }

}