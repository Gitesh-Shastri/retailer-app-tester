package com.medicento.retailerappmedi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

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
import com.medicento.retailerappmedi.data.Notification;
import com.medicento.retailerappmedi.data.OrderItem;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.order_related.Payment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.JsonUtils.getIntegerValueFromJsonKey;
import static com.medicento.retailerappmedi.Utils.JsonUtils.getJsonValueFromKey;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class PaymentSummaryActivity extends AppCompatActivity {

    private static final String TAG = "PaySummaryAct";
    SalesPerson sp;

    private ArrayList<Payment> payments;
    private ArrayList<String> dates;
    private String date = "";
    private TextView amount_balance, due_date, min_amount_balance;

    PaymentAdapter paymentAdapter;
    RecyclerView payment_summary_rv;
    private ArrayList<Integer> net_dues;
    private int total_due = 0;
    int k = 0, i=0;
    private Notification notification;
    private int discount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_summary);
        Paper.init(this);

        due_date = findViewById(R.id.due_date);
        payment_summary_rv = findViewById(R.id.payment_summary_rv);
        min_amount_balance = findViewById(R.id.min_amount_balance);

        payments = new ArrayList<>();
        net_dues = new ArrayList<>();

        if (getIntent() != null && getIntent().hasExtra("notification")) {
            notification = (Notification) getIntent().getSerializableExtra("notification");
        }

        amount_balance = findViewById(R.id.amount_balance);

        paymentAdapter = new PaymentAdapter(payments, this);
        payment_summary_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        payment_summary_rv.setAdapter(paymentAdapter);

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
            get_payment();
        }
    }

    private void get_payment() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_payment/?id="+sp.getmAllocatedPharmaId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray payment_summary = jsonObject.getJSONArray("data");
                            JSONArray invoices = jsonObject.getJSONArray("invoices");
                            due_date.setText(jsonObject.getString("next_payment_due").substring(0, 9));
                            min_amount_balance.setText(jsonObject.getInt("min_amount_due")+"");
                            discount = jsonObject.getInt("discount");

                            paymentAdapter.setDiscount(discount);

                            while (i<payment_summary.length() || k <invoices.length()) {
                                if (i == payment_summary.length()) {
                                    JSONObject jsonObject2 = invoices.getJSONObject(k);
                                    payments.add(getPayment(jsonObject2));
                                } else if (k == invoices.length()) {
                                    JSONObject jsonObject1 = payment_summary.getJSONObject(i);
                                    payments.add(getPayment(jsonObject1, "- Rs. "));
                                } else {
                                    JSONObject jsonObject2 = invoices.getJSONObject(k);
                                    JSONObject jsonObject1 = payment_summary.getJSONObject(i);
                                    try {
                                        Date date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(getJsonValueFromKey(jsonObject1, "created_at"));
                                        Date date2 = new SimpleDateFormat("dd-MMM-yyyy").parse(getJsonValueFromKey(jsonObject2, "created_at"));

                                        if (date2.after(date1)) {
                                            payments.add(getPayment(jsonObject2));
                                        } else if (getJsonValueFromKey(jsonObject1, "created_at").equals(getJsonValueFromKey(jsonObject2, "created_at"))) {
                                            payments.add(getPayment(jsonObject1, "- Rs. "));
                                            payments.add(getPayment(jsonObject2));
                                        } else {
                                            payments.add(getPayment(jsonObject1, "- Rs. "));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (notification != null) {
                            paymentAdapter.setNotification(notification);
                        }
                        k=payments.size()-1;
                        for (int i=net_dues.size()-1;i>=0;i--) {
                            total_due += net_dues.get(i);
                            if (!payments.get(k).isDate()) {
                                payments.get(k).setNet_due(String.valueOf(total_due));
                                k-=1;
                            } else {
                                k-=1;
                                payments.get(k).setNet_due(String.valueOf(total_due));
                                k-=1;
                            }
                        }
                        amount_balance.setText("Rs. "+total_due);
                        paymentAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    private void setDate(JSONObject jsonObject2) {
        if (!date.equals(getJsonValueFromKey(jsonObject2, "created_at"))) {
            payments.add(
                    new Payment()
                            .setDate(getJsonValueFromKey(jsonObject2, "created_at"))
                            .setIsDate(true)
                            .createPayment()
            );
            date = getJsonValueFromKey(jsonObject2, "created_at");
        }
    }

    private Payment getPayment(JSONObject jsonObject2) {
        setDate(jsonObject2);
        net_dues.add(-getIntegerValueFromJsonKey(jsonObject2, "amount_recived"));
        k += 1;
        return new Payment()
                .setDate(getJsonValueFromKey(jsonObject2, "created_at"))
                .setGrand_total("+ Rs. " + getJsonValueFromKey(jsonObject2, "amount_recived"))
                .setInvoice_ids("Payment made via " + getJsonValueFromKey(jsonObject2, "collected_by"))
                .setSummary(getJsonValueFromKey(jsonObject2, "collection_type"))
                .setContent("")
                .setIsDate(false)
                .createPayment();
    }

    private Payment getPayment(JSONObject jsonObject1, String s) throws JSONException {
        setDate(jsonObject1);
        net_dues.add(getIntegerValueFromJsonKey(jsonObject1, "amount"));

        i+=1;

        Payment payment =  new Payment()
                .setDate(getJsonValueFromKey(jsonObject1, "created_at"))
                .setGrand_total(s + getJsonValueFromKey(jsonObject1, "amount"))
                .setInvoice_ids(getJsonValueFromKey(jsonObject1, "invoice_code"))
                .setSummary("Medicine Purchase")
                .setContent("")
                .setIsDate(false)
                .createPayment();

        JSONArray order_ids = jsonObject1.getJSONArray("order_ids");
        payment.setOrder_id("");
        payment.setPaid_status("");
        for (int m=0;m<order_ids.length();m++) {
            JSONObject order_id = order_ids.getJSONObject(m);
            if (m < order_ids.length()-1) {
                payment.setOrder_id(payment.getOrder_id()+order_id.getInt("order_id")+", ");
            } else {
                payment.setOrder_id(payment.getOrder_id()+order_id.getInt("order_id"));
            }
            payment.setPaid_status(order_id.getString("paid_status"));
            payment.setDays(order_id.getInt("days")+" days");
        }

        JSONArray order_items = jsonObject1.getJSONArray("orders");
        for (int l=0;l<order_items.length();l++) {
            JSONObject jsonObject2 = order_items.getJSONObject(l);

            OrderItem orderItem = new OrderItem()
                    .setName(getJsonValueFromKey(jsonObject2, "medicine_name"))
                    .setCompany(getJsonValueFromKey(jsonObject2, "manfc_name"))
                    .setPrice(getIntegerValueFromJsonKey(jsonObject2, "price"))
                    .setQty(getIntegerValueFromJsonKey(jsonObject2, "quantity"))
                    .createOrderItem();

            orderItem.setScheme(getJsonValueFromKey(jsonObject2, "scheme"));
            orderItem.setPacking(getJsonValueFromKey(jsonObject2, "packing"));
            orderItem.setItem_code(getJsonValueFromKey(jsonObject2, "item_code"));
            orderItem.setId(getJsonValueFromKey(jsonObject2, "id"));
            orderItem.setAlreadyReturned(false);
            orderItem.setReason(getJsonValueFromKey(jsonObject2, "reason"));
            orderItem.setReturned(!(getIntegerValueFromJsonKey(jsonObject2, "bounced_count") == orderItem.getQty()));
            orderItem.setDispute(getJsonValueFromKey(jsonObject2, "dispute_reason"));
            orderItem.setIs_executed(JsonUtils.getBooleanValueFromJsonKey(jsonObject2, "is_executed"));

            if (getIntegerValueFromJsonKey(jsonObject2, "bounced_count") == 0 && orderItem.getReason().equals("-")) {
                payment.getOrderItems().add(orderItem);
            }
        }

        return  payment;
    }
}
