package com.medicento.retailerappmedi.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.RecentOrderActivity;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = "PaymentAct";
    SalesPerson salesPerson;

    TextView amount_balance, available_credit, credit_limit, next_payment_due, min_due;
    int credit, avail_credit;

    LinearLayout order_summary, new_order, payment_summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        amount_balance = findViewById(R.id.amount_balance);
        available_credit = findViewById(R.id.available_credit);
        credit_limit = findViewById(R.id.credit_limit);
        next_payment_due = findViewById(R.id.next_payment_due);
        min_due = findViewById(R.id.min_due);

        order_summary = findViewById(R.id.order_summary);
        new_order = findViewById(R.id.new_order);
        payment_summary = findViewById(R.id.payment_summary);

        Checkout.preload(getApplicationContext());

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            salesPerson = gson.fromJson(cache, SalesPerson.class);
        }

        get_payment_details();

        order_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, RecentOrderActivity.class));
            }
        });

        new_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        payment_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PaymentActivity.this, PaymentSummaryActivity.class));
            }
        });

        startPayment();
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
            options.put("description", "Reference No. #123456");
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", "100");

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    private void get_payment_details() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/pharmacy/get_payment_details/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(JsonUtils.getJsonValueFromKey(jsonObject, "message").equals("Pharmacy Found")) {

                                JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");

                                credit = getIntegerFromString(JsonUtils.getJsonValueFromKey(pharmacy, "credit"));
                                credit_limit.setText("Rs."+credit);
                                avail_credit = getIntegerFromString(JsonUtils.getJsonValueFromKey(pharmacy, "available_credit"));
                                available_credit.setText("Rs."+avail_credit);

                                amount_balance.setText("Rs."+Math.abs(credit-avail_credit));

                                min_due.setText("Rs."+JsonUtils.getJsonValueFromKey(pharmacy, "min_amount_due"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error == null || error.networkResponse == null) {
                                Toast.makeText(PaymentActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse: " + body);
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    if (jsonObject.has("message")) {
                                        Toast.makeText(PaymentActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(PaymentActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(PaymentActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", salesPerson.getmAllocatedPharmaId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private int getIntegerFromString(String value) {
        int result = 0;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.d(TAG, "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d(TAG, "onPaymentError: " + s);
    }
}
