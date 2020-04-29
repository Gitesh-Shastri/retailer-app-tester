package com.medicento.retailerappmedi.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.RecentOrderActivity;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.MedicinesOrderedReturnAdapter;
import com.medicento.retailerappmedi.adapter.SubOrderAdapter;
import com.medicento.retailerappmedi.data.MedicineOrdered;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.RecentOrderDelivered;
import com.medicento.retailerappmedi.data.order_related.OrderItem;
import com.medicento.retailerappmedi.data.order_related.SubOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.medicento.retailerappmedi.Utils.JsonUtils.getIntegerValueFromJsonKey;
import static com.medicento.retailerappmedi.Utils.JsonUtils.getJsonValueFromKey;

public class ReturnActivity extends AppCompatActivity {

    private static final String TAG = "ReturnAct";
    RecentOrderDelivered recentOrderDelivered;
    ArrayList<MedicineOrdered> medicineOrdereds;

    MedicinesOrderedReturnAdapter medicinesOrderedReturnAdapter;
    RecyclerView rv_return_items;

    TextView order_id_tv, order_status, order_date, total_cost_tv;
    Button return_items;

    Dialog dialog;

    SubOrderAdapter subOrderAdapter;
    boolean return_order, already_returned, is_item_have_reason;

    private ArrayList<SubOrder> subOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        subOrders = new ArrayList<>();

        rv_return_items = findViewById(R.id.rv_return_items);
        order_id_tv = findViewById(R.id.order_id_tv);
        order_status = findViewById(R.id.order_status);
        order_date = findViewById(R.id.order_date);
        total_cost_tv = findViewById(R.id.total_cost_tv);
        return_items = findViewById(R.id.return_items);

        subOrderAdapter = new SubOrderAdapter( subOrders, this);
        rv_return_items.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_return_items.setAdapter(subOrderAdapter);
        subOrderAdapter.setOnReturnScreen(true);

        if(getIntent() != null && getIntent().hasExtra("order")) {

            recentOrderDelivered = (RecentOrderDelivered) getIntent().getSerializableExtra("order");

            order_id_tv.setText(recentOrderDelivered.getpOrderId());
            order_status.setText(recentOrderDelivered.getStatus());
            order_date.setText(recentOrderDelivered.getpDate());
            total_cost_tv.setText("Rs. " + recentOrderDelivered.getTotal());

            if(getIntent().hasExtra("return_items")) {
                subOrders.addAll((ArrayList<SubOrder>)getIntent().getSerializableExtra("return_items"));
                subOrderAdapter.notifyDataSetChanged();
            } else {
                getOrderDetails();
            }
        }

        return_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                return_order = false;
                is_item_have_reason = true;
                already_returned = false;

                for (SubOrder subOrder: subOrders) {
                    for (OrderItem orderItem : subOrder.getOrderItems()) {
                        if(orderItem.isReturned() && !orderItem.isAlreadyReturned()) {
                            if(orderItem.getReason().equals("Select Return Reason")) {
                                is_item_have_reason = false;
                            }
                            return_order = true;
                        }
                    }
                }

                if(!return_order) {
                    Toast.makeText(ReturnActivity.this, "Please select the item to Return.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!is_item_have_reason) {
                    Toast.makeText(ReturnActivity.this, "Please select the return reason.", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new Dialog(ReturnActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_return);

                Button yes, no;
                yes = dialog.findViewById(R.id.yes);
                no = dialog.findViewById(R.id.no);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        returnOrder();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    private void getOrderDetails() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/get_order_details/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject order_details = jsonObject.getJSONObject("order_details");

                            JSONArray sub = order_details.getJSONArray("sub_order");

                            JSONObject sub_object = null;
                            JSONObject status = null;
                            JSONObject distributor = null;

                            for (int i=0;i<sub.length();i++) {

                                sub_object = sub.getJSONObject(i);
                                status = sub_object.getJSONObject("status");
                                distributor = sub_object.getJSONObject("distributor");

                                ArrayList<OrderItem> orderItems = new ArrayList<>();

                                SubOrder subOrder = new SubOrder()
                                        .setSuplier_name(getJsonValueFromKey(distributor, "name"))
                                        .setId(getJsonValueFromKey(sub_object, "sub_ord_id"))
                                        .setStatus(getJsonValueFromKey(status, "name"))
                                        .setTotal(getIntegerValueFromJsonKey(sub_object, "grand_total"))
                                        .createSubOrder();

                                JSONArray order_items_array = sub_object.getJSONArray("order_items");

                                for (int item=0;item<order_items_array.length();item++) {

                                    JSONObject jsonObject1 = order_items_array.getJSONObject(item);

                                    OrderItem orderItem = new OrderItem()
                                            .setName(getJsonValueFromKey(jsonObject1, "medicine_name"))
                                            .setCompany(getJsonValueFromKey(jsonObject1, "manfc_name"))
                                            .setPrice(getIntegerValueFromJsonKey(jsonObject1, "price"))
                                            .setQty(getIntegerValueFromJsonKey(jsonObject1, "quantity"))
                                            .createOrderItem();

                                    orderItem.setItem_code(JsonUtils.getJsonValueFromKey(jsonObject1, "item_code"));
                                    orderItem.setId(JsonUtils.getJsonValueFromKey(jsonObject1, "id"));

                                    orderItem.setAlreadyReturned(false);

                                    if(!JsonUtils.getStringValueFromJsonKey(jsonObject1, "reason").isEmpty() && !JsonUtils.getStringValueFromJsonKey(jsonObject1, "reason").equals("-")) {
                                        orderItem.setReason(JsonUtils.getStringValueFromJsonKey(jsonObject1, "reason"));
                                        orderItem.setReturned(true);
                                        orderItem.setAlreadyReturned(true);
                                    }

                                    orderItems.add(orderItem);
                                }
                                subOrder.setOrderItems(orderItems);
                                subOrders.add(subOrder);
                            }

                            subOrderAdapter.notifyDataSetChanged();

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
                                Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    if (jsonObject.has("message")) {
                                        Toast.makeText(ReturnActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", recentOrderDelivered.getpOrderId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void returnOrder() {
        RequestQueue requestQueue = Volley.newRequestQueue(ReturnActivity.this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/return_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ReturnActivity.this, "Return request successfully submitted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error == null || error.networkResponse == null) {
                                Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    if (jsonObject.has("message")) {
                                        Toast.makeText(ReturnActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ReturnActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                JSONArray orderItems = new JSONArray();
                try {
                    for (SubOrder subOrder: subOrderAdapter.getmRecentOrderItems()) {
                        for (OrderItem orderItem : subOrder.getOrderItems()) {
                            if(orderItem.isReturned()) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("id", orderItem.getId());
                                jsonObject1.put("reason", orderItem.getReason());
                                orderItems.put(jsonObject1);
                            }
                        }
                    }
                    jsonObject.put("items", orderItems);
                    params.put("order_items", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

}
