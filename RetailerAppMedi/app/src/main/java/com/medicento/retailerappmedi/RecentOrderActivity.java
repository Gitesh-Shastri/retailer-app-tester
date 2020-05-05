package com.medicento.retailerappmedi;

import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.medicento.retailerappmedi.Utils.MedicentoUtils;
import com.medicento.retailerappmedi.activity.ReturnActivity;
import com.medicento.retailerappmedi.data.OrderAdapterDelivered;
import com.medicento.retailerappmedi.data.RecentOrderDelivered;
import com.medicento.retailerappmedi.data.SalesPerson;

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

public class RecentOrderActivity extends AppCompatActivity implements OrderAdapterDelivered.OnItemClickListener {

    private static final String TAG = "RecentOrderAct";

    private static ArrayList<RecentOrderDelivered> mRecentOrder;

    SalesPerson salesPerson;

    private static OrderAdapterDelivered mOrder;
    RecyclerView iv;
    ProgressBar progressBar;
    int page = 1;

    boolean isLoading = false;
    boolean isScrolling = false;
    SwipeRefreshLayout swipe_refresh;

    LinearLayoutManager linearLayoutManager;
    Dialog dialog;

    RadioGroup reason;
    RadioButton radioButton;
    EditText other_reasons;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_order);

        Paper.init(this);

        final String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
        }

        progressBar = findViewById(R.id.progress_bar);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        iv = (RecyclerView) findViewById(R.id.listview3);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        iv.setLayoutManager(linearLayoutManager);
        iv.setHasFixedSize(true);

        mRecentOrder = new ArrayList<>();

        mOrder = new OrderAdapterDelivered(mRecentOrder);
        mOrder.setOnItemClicklistener(this);
        iv.setAdapter(mOrder);

        iv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int currentItems = linearLayoutManager.getChildCount();
                int totalItems = linearLayoutManager.getItemCount();

                int scrolledItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrolledItems == totalItems)) {
                    isScrolling = false;
                    getOrders();
                }
            }
        });


        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    page = 1;
                    mRecentOrder.clear();
                    mOrder.notifyDataSetChanged();
                    getOrders();
                }
                swipe_refresh.setRefreshing(false);
            }
        });

        getOrders();
    }

    private void getOrders() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/get_pharmacy_orders/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = getJsonValueFromKey(jsonObject, "message");

                            if (message.equals("Data Found")) {
                                JSONArray order_details = jsonObject.getJSONArray("order_details");

                                for (int i = 0; i < order_details.length(); i++) {
                                    JSONObject order = order_details.getJSONObject(i);
                                    RecentOrderDelivered recentOrderDelivered = new RecentOrderDelivered(
                                            getJsonValueFromKey(order, "id"),
                                            getJsonValueFromKey(order, "created_at"),
                                            getIntegerValueFromJsonKey(order, "grand_total"));

                                    JSONObject status = order.getJSONObject("status");
                                    String status_name = getJsonValueFromKey(status, "name");

                                    recentOrderDelivered.setStatus(status_name);
                                    mRecentOrder.add(recentOrderDelivered);
                                    mOrder.notifyItemInserted(mRecentOrder.size() - 1);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        page += 1;
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (error == null || error.networkResponse == null) {
                                Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.d(TAG, "onErrorResponse: " + body);
                                try {
                                    JSONObject jsonObject = new JSONObject(body);
                                    if (jsonObject.has("message")) {
                                        Toast.makeText(RecentOrderActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pharmacy_id", salesPerson.getId());
                params.put("page", page + "");
                return params;
            }
        };

        if (!isLoading) {
            isLoading = true;
            requestQueue.add(stringRequest);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick1(int position) {

        Intent intent = new Intent(RecentOrderActivity.this, RecentOrderDetails.class);
        intent.putExtra("order", mRecentOrder.get(position));
        startActivity(intent);
    }

    @Override
    public void onCancelItem(final int position) {

        String status = mRecentOrder.get(position).getStatus();
        if(MedicentoUtils.isStringEquals(status, "Cancelled")) {
            Toast.makeText(this, "Sorry, Order cannot be cancelled as order is already cancelled.", Toast.LENGTH_SHORT).show();
            return;
        } else if(MedicentoUtils.isStringEquals(status, "Delivered")) {
            Toast.makeText(this, "Sorry, Order cannot be cancelled as order is delivered.", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_cancel_order);

        Button cancel, back;
        back = dialog.findViewById(R.id.back);
        cancel = dialog.findViewById(R.id.cancel_order);

        reason = dialog.findViewById(R.id.reason);
        other_reasons = dialog.findViewById(R.id.other_reasons);

        radioButton = dialog.findViewById(R.id.other);

        other_reasons.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                radioButton.setChecked(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(RecentOrderActivity.this);
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        "http://stage.medicento.com:8080/orders/cancel_order/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse: " + response);
                                dialog.dismiss();
                                showOkayDialog();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    if (error == null || error.networkResponse == null) {
                                        Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String body;
                                    try {
                                        body = new String(error.networkResponse.data, "UTF-8");
                                        Log.d(TAG, "onErrorResponse: " + body);
                                        try {
                                            JSONObject jsonObject = new JSONObject(body);
                                            if (jsonObject.has("message")) {
                                                Toast.makeText(RecentOrderActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(RecentOrderActivity.this, "No Response from server. Please try again after some time", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", mRecentOrder.get(position).getpOrderId());
                        radioButton = dialog.findViewById(reason.getCheckedRadioButtonId());

                        if (reason.getCheckedRadioButtonId() == R.id.other) {
                            params.put("order_cancelation_reason", "Other -> " + other_reasons.getText().toString());
                        } else {
                            params.put("order_cancelation_reason", radioButton.getText().toString());
                        }

                        return params;
                    }
                };
                if(reason.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RecentOrderActivity.this, "Please select a reason to cancel the order", Toast.LENGTH_SHORT).show();
                    return;
                }
                requestQueue.add(stringRequest);
            }
        });

        dialog.show();
    }

    @Override
    public void onReturnItem(int position) {
        String status = mRecentOrder.get(position).getStatus();
        if(MedicentoUtils.isStringEquals(status, "Cancelled")) {
            Toast.makeText(this, "Sorry, Items cannot be returned as order is already cancelled.", Toast.LENGTH_SHORT).show();
            return;
        } else if(MedicentoUtils.isStringEquals(status, "Placed")) {
            Toast.makeText(this, "Sorry, Items cannot be returned without being delivered.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, ReturnActivity.class)
                .putExtra("order", mRecentOrder.get(position)));
    }

    private void showOkayDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Button okay;
        okay = dialog.findViewById(R.id.okay);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                page = 1;
                mRecentOrder.clear();
                mOrder.notifyDataSetChanged();
                getOrders();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
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

    JSONObject activityObject;

    @Override
    protected void onResume() {
        super.onResume();

        activityObject = new JSONObject();
        try {
            activityObject.put("activity_name", "RecentOrder");
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

        try {
            activityObject.put("end_time", System.currentTimeMillis() + "");
            activityObject.put("android_id", androidId);
            if (salesPerson != null && salesPerson.getmAllocatedPharmaId() != null) {
                activityObject.put("pharmacy_id", salesPerson.getmAllocatedPharmaId());
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
