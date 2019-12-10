package com.medicento.retailerappmedi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.PaymentAdapter;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.order_related.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class PaymentSummaryActivity extends AppCompatActivity {

    private static final String TAG = "PaySummaryAct";
    SalesPerson sp;

    private ArrayList<Payment> payments;
    private String date = "";

    PaymentAdapter paymentAdapter;
    RecyclerView payment_summary_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary);
        Paper.init(this);

        payment_summary_rv = findViewById(R.id.payment_summary_rv);

        payments = new ArrayList<>();

        paymentAdapter = new PaymentAdapter(payments, this);
        payment_summary_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        payment_summary_rv.setAdapter(paymentAdapter);

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
            getPaymentSummary();
        }
    }

    private void getPaymentSummary() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/get_payment_summary/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray payment_summary = jsonObject.getJSONArray("payment_summary");

                            Payment payment;
                            for (int i=0;i<payment_summary.length();i++) {
                                JSONObject jsonObject1 = payment_summary.getJSONObject(i);

                                if(!date.equals(JsonUtils.getJsonValueFromKey(jsonObject1, "created_at"))) {
                                    payments.add(
                                            new Payment()
                                                    .setDate(JsonUtils.getJsonValueFromKey(jsonObject1, "created_at"))
                                                    .setIsDate(true)
                                                    .createPayment()
                                    );
                                    date = JsonUtils.getJsonValueFromKey(jsonObject1, "created_at");
                                }
                                payment = new Payment()
                                        .setDate(JsonUtils.getJsonValueFromKey(jsonObject1, "created_at"))
                                        .setGrand_total(JsonUtils.getJsonValueFromKey(jsonObject1, "amount"))
                                        .setOrder_id(JsonUtils.getJsonValueFromKey(jsonObject1, "order_id"))
                                        .setSummary(JsonUtils.getJsonValueFromKey(jsonObject1, "order_id_summary"))
                                        .setContent(JsonUtils.getJsonValueFromKey(jsonObject1, "content"))
                                        .setIsDate(false)
                                        .createPayment();

                                payments.add(payment);

                                paymentAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", sp.getmAllocatedPharmaId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
