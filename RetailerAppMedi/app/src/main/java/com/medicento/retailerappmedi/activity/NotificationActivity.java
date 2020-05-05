package com.medicento.retailerappmedi.activity;

import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.NotificationAdapter;
import com.medicento.retailerappmedi.data.Notification;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationAct";
    RecyclerView notification_rv;
    ProgressBar progress_bar;

    NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notifications;

    SalesPerson sp;
    int page = 1;

    LinearLayoutManager linearLayoutManager;
    boolean isScrolling;

    Animation mAnimation;
    JSONObject activityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Paper.init(this);
        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        notification_rv = findViewById(R.id.notification_rv);
        progress_bar = findViewById(R.id.progress_bar);

        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notifications, this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        notification_rv.setLayoutManager(linearLayoutManager);
        notification_rv.setAdapter(notificationAdapter);

        notification_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    fetchNotificatiion();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                Log.d(TAG, "onSwiped: " + pos + " : " + notifications.get(pos).getId());
                removeNotification(notifications.get(pos).getId());
                notifications.remove(pos);
                notificationAdapter.notifyItemRemoved(pos);
            }
        }).attachToRecyclerView(notification_rv);
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);

        fetchNotificatiion();
    }

    private void fetchNotificatiion() {

        RequestQueue requestQueue = Volley.newRequestQueue(NotificationActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/pharmacy/get_notifications/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_bar.setVisibility(View.GONE);
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                            if(message.equals("Notification Found")) {

                                JSONArray notification_array = jsonObject.getJSONArray("notifications");
                                Notification notification;
                                for (int i=0;i<notification_array.length();i++) {
                                    JSONObject notificationObj = notification_array.getJSONObject(i);
                                    notification = new Notification()
                                            .setId(JsonUtils.getJsonValueFromKey(notificationObj, "id"))
                                            .setMessage(JsonUtils.getJsonValueFromKey(notificationObj, "message"))
                                            .setTitle(JsonUtils.getJsonValueFromKey(notificationObj, "title"))
                                            .setStatus(JsonUtils.getJsonValueFromKey(notificationObj, "status"))
                                            .setOrder_id(JsonUtils.getJsonValueFromKey(notificationObj, "order_id"))
                                            .setCreated_at(JsonUtils.getJsonValueFromKey(notificationObj, "created_at"))
                                            .setTime(JsonUtils.getJsonValueFromKey(notificationObj, "time"))
                                            .createNotification();

                                    notifications.add(notification);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        notificationAdapter.notifyDataSetChanged();
                        page += 1;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        Toast.makeText(NotificationActivity.this, "Error In The Network.Please Try Again After Some Time ", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", sp.getmAllocatedPharmaId());
                params.put("page", page+"");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void removeNotification(final String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(NotificationActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/remove_notifications/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NotificationActivity.this, "Error In The Network.Please Try Again After Some Time ", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d(TAG, "getParams: " + sp.getmAllocatedPharmaId() + " : " + id);
                params.put("pharmacy_id", sp.getmAllocatedPharmaId());
                params.put("notification_id", id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                3,
                2));
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityObject = new JSONObject();
        try {
            activityObject.put("activity_name", "Notification");
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
