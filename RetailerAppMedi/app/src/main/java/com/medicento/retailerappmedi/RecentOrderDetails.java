package com.medicento.retailerappmedi;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.gson.Gson;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.MainActivity;
import com.medicento.retailerappmedi.activity.ReturnActivity;
import com.medicento.retailerappmedi.adapter.OrderBounceItemsAdapter;
import com.medicento.retailerappmedi.adapter.SubOrderAdapter;
import com.medicento.retailerappmedi.data.RecentOrderDelivered;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.order_related.OrderItem;
import com.medicento.retailerappmedi.data.order_related.SubOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.JsonUtils.getIntegerValueFromJsonKey;
import static com.medicento.retailerappmedi.Utils.JsonUtils.getJsonValueFromKey;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class RecentOrderDetails extends AppCompatActivity {

    private static final String TAG = "RecentOrderAct";
    RecentOrderDelivered recentOrderDelivered;

    RecyclerView recyclerView;
    RecyclerView sub_order_rv, return_order_rv;
    OrderBounceItemsAdapter recentOrderItems;
    SubOrderAdapter subOrderAdapter, returnSubOrderAdapter;

    private ArrayList<OrderItem> bounce_Items;
    private ArrayList<OrderItem> orderItems;
    private ArrayList<SubOrder> subOrders, return_sub_order;
    CardView bounced_items_card, return_items_card;
    int total_items_in_order = 0;
    int total_bounced = 0;

    TextView order_id, grand_total, created, total_items, total_item_bounced, total_fulfilment;
    Button go_home, re_order, return_items;

    CardView no_items_card;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_order_details);

        recyclerView = findViewById(R.id.bounce_order_rv);
        no_items_card = findViewById(R.id.no_items_card);
        return_sub_order = new ArrayList<>();
        return_items_card = findViewById(R.id.return_items_card);
        return_items = findViewById(R.id.return_items);
        sub_order_rv = findViewById(R.id.sub_order_rv);
        return_order_rv = findViewById(R.id.return_order_rv);
        total_items = findViewById(R.id.total_items);
        order_id = findViewById(R.id.order_id);
        total_item_bounced = findViewById(R.id.total_item_bounced);
        go_home = findViewById(R.id.go_home);
        bounced_items_card = findViewById(R.id.bounced_items_card);
        total_fulfilment = findViewById(R.id.total_fulfilment);
        re_order = findViewById(R.id.re_order);

        grand_total = findViewById(R.id.grand_total);

        created = findViewById(R.id.created);

        if (getIntent().hasExtra("id")) {
            recentOrderDelivered = new RecentOrderDelivered(
                    getIntent().getStringExtra("id"),
                    "",
                    0
            );

            order_id.append(recentOrderDelivered.getpOrderId());

            grand_total.setText("Rs. 0");

        }

        if (getIntent().hasExtra("order")) {
            recentOrderDelivered = (RecentOrderDelivered) getIntent().getSerializableExtra("order");

            order_id.append(recentOrderDelivered.getpOrderId());

            grand_total.setText("Rs. "+recentOrderDelivered.getTotal()+"");

        }

        bounce_Items = new ArrayList<>();
        subOrders = new ArrayList<>();

        recentOrderItems = new OrderBounceItemsAdapter(bounce_Items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(recentOrderItems);

        subOrderAdapter = new SubOrderAdapter( subOrders, this);
        sub_order_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sub_order_rv.setAdapter(subOrderAdapter);

        returnSubOrderAdapter = new SubOrderAdapter( return_sub_order, this);
        return_order_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        return_order_rv.setAdapter(returnSubOrderAdapter);

        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecentOrderDetails.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        orderItems = new ArrayList<>();

        re_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subOrderAdapter != null) {
                    ArrayList<SubOrder> subOrders1 = subOrderAdapter.getmRecentOrderItems();
                    for (SubOrder subOrder: subOrders1) {
                        for (OrderItem orderItem: subOrder.getOrderItems()) {
                            if(orderItem.isSelected()) {
                                orderItems.add(orderItem);
                            }
                        }
                    }
                    if(orderItems.size() == 0) {
                        Toast.makeText(RecentOrderDetails.this, "Please Select Some medicine", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(RecentOrderDetails.this, HomeActivity.class);
                        intent.putExtra("re_order_items", orderItems);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        return_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subOrderAdapter != null) {
                    ArrayList<SubOrder> subOrders1 = subOrderAdapter.getmRecentOrderItems();
                    for (SubOrder subOrder: subOrders1) {
                        for (OrderItem orderItem: subOrder.getOrderItems()) {
                            if(orderItem.isSelected()) {
                                orderItems.add(orderItem);
                            }
                        }
                    }
                    if(orderItems.size() == 0) {
                        Toast.makeText(RecentOrderDetails.this, "Please Select Some medicine", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(RecentOrderDetails.this, ReturnActivity.class);
                        intent.putExtra("order", recentOrderDelivered);
                        intent.putExtra("return_items", subOrders);
                        startActivity(intent);
                    }
                }
            }
        });
        getOrderDetails();
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

                            JSONArray order_items = order_details.getJSONArray("order_items");
                            JSONArray sub = order_details.getJSONArray("sub_order");

                            created.setText(JsonUtils.getJsonValueFromKey(order_details, "created_at"));

                            grand_total.setText("Rs. "+recentOrderDelivered.getTotal()+"");

                            total_items_in_order = order_items.length();

                            for (int i=0;i<order_items.length();i++) {
                                JSONObject order_item = order_items.getJSONObject(i);

                                if(JsonUtils.getIntegerValueFromJsonKey(order_item, "bounced_count") > 0) {
                                    bounce_Items.add(new OrderItem()
                                            .setName(getJsonValueFromKey(order_item, "medicine_name"))
                                            .setCompany(getJsonValueFromKey(order_item, "manfc_name"))
                                            .setPrice(getIntegerValueFromJsonKey(order_item, "price"))
                                            .setQty(getIntegerValueFromJsonKey(order_item, "bounced_count"))
                                            .createOrderItem());
                                    recentOrderItems.notifyItemInserted(bounce_Items.size()-1);
                                    if (JsonUtils.getIntegerValueFromJsonKey(order_item, "bounced_count") == getIntegerValueFromJsonKey(order_item, "quantity")) {
                                        total_bounced += 1;
                                    }
                                }
                            }

                            JSONObject sub_object = null;
                            JSONObject status = null;
                            JSONObject distributor = null;

                            for (int i=0;i<sub.length();i++) {

                                sub_object = sub.getJSONObject(i);
                                status = sub_object.getJSONObject("status");
                                distributor = sub_object.getJSONObject("distributor");

                                ArrayList<OrderItem> orderItems = new ArrayList<>();
                                ArrayList<OrderItem> re_orderItems = new ArrayList<>();

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

                                        re_orderItems.add(orderItem);
                                    } else {
                                        orderItems.add(orderItem);
                                    }
                                }

                                subOrder.setOrderItems(orderItems);
                                subOrders.add(subOrder);

                                if(re_orderItems.size() > 0) {

                                    SubOrder re_sub_order = new SubOrder()
                                            .setSuplier_name(getJsonValueFromKey(distributor, "name"))
                                            .setId(getJsonValueFromKey(sub_object, "sub_ord_id"))
                                            .setStatus(getJsonValueFromKey(status, "name"))
                                            .setTotal(getIntegerValueFromJsonKey(sub_object, "grand_total"))
                                            .createSubOrder();

                                    re_sub_order.setStatus("Returned");
                                    re_sub_order.setId(re_sub_order.getId() + " - R");

                                    int total = 0;
                                    for (OrderItem orderItem: re_orderItems) {
                                        total += orderItem.getQty()*orderItem.getPrice();
                                    }

                                    re_sub_order.setOrderItems(re_orderItems);
                                    re_sub_order.setTotal(total);
                                    return_sub_order.add(re_sub_order);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        subOrderAdapter.notifyDataSetChanged();
                        returnSubOrderAdapter.notifyDataSetChanged();

                        total_item_bounced.setText((bounce_Items.size())+"");
                        total_items.setText(total_items_in_order+"");

                        if (total_items_in_order == 0 ) {
                            return_items_card.setVisibility(View.GONE);
                            no_items_card.setVisibility(View.VISIBLE);
                            sub_order_rv.setVisibility(View.GONE);
                        } else {
                            sub_order_rv.setVisibility(View.VISIBLE);
                            return_items_card.setVisibility(View.VISIBLE);
                            no_items_card.setVisibility(View.GONE);
                            try {
                                float fulfilment = (total_items_in_order-total_bounced)/total_items_in_order;
                                total_fulfilment.setText(((int)Math.floor(fulfilment*100))+" %");
                            } catch (Exception e) {
                                total_fulfilment.setText("100 %");
                                e.printStackTrace();
                            }
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
                params.put("id", recentOrderDelivered.getpOrderId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void showVolleyError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Toast.makeText(RecentOrderDetails.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                return;
            }
            String body;
            try {
                body = new String(error.networkResponse.data, "UTF-8");
                Log.d(TAG, "showVolleyError: " + body);
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    if (jsonObject.has("message")) {
                        Toast.makeText(RecentOrderDetails.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RecentOrderDetails.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(RecentOrderDetails.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(RecentOrderDetails.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
        }
    }

    JSONObject activityObject;

    @Override
    protected void onResume() {
        super.onResume();

        activityObject = new JSONObject();
        try {
            activityObject.put("activity_name", "OrderDetails");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        final String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = new Gson().fromJson(cache, SalesPerson.class);
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
                "http://stage.medicento.com:8080/api/app/record_activity/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
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

}
