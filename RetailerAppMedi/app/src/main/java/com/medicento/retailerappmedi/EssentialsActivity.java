package com.medicento.retailerappmedi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.Utils.MedicentoUtils;
import com.medicento.retailerappmedi.activity.NotificationActivity;
import com.medicento.retailerappmedi.activity.PaymentSummaryActivity;
import com.medicento.retailerappmedi.adapter.EssentialAdapter;
import com.medicento.retailerappmedi.adapter.EssentialListAdapter;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.Essential;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.getDeviceModel;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class EssentialsActivity extends AppCompatActivity implements View.OnClickListener, EssentialListAdapter.OverallCostChangeListener, EssentialAdapter.setOnClickListener {

    private static final String TAG = "EssentialsAct";
    TextView pharma_name, count, price, number, prices;
    Button go_to_cart;
    private SalesPerson sp;
    private EssentialListAdapter essentialListAdapter;
    private ArrayList<EssentialList> essentialLists;
    private ArrayList<Category> essentials;
    private RecyclerView essential_rv, essential_cat_rv;
    ProgressBar progress_bar, progress_bar_essen;
    RelativeLayout go_to_cart_rl, cart_rl;
    TextView  essentials_text, home_text;
    ImageView essentials_image, back, home_image;
    LinearLayout home_ll, notification, payment, recentorder, essential;
    String category = "Mask";
    EssentialAdapter essentialAdapter;
    ArrayList<EssentialList> essentialList_temp;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_essentials);

        count = findViewById(R.id.count);
        price = findViewById(R.id.price);
        number = findViewById(R.id.number);
        home_text = findViewById(R.id.home_text);
        home_image = findViewById(R.id.home_image);
        progress_bar =findViewById(R.id.progress_bar);
        essential_cat_rv = findViewById(R.id.essential_cat_rv);
        progress_bar_essen = findViewById(R.id.progress_bar_essen);
        back = findViewById(R.id.back);
        pharma_name = findViewById(R.id.pharma_name);
        essential = findViewById(R.id.essential);
        essentials_text = findViewById(R.id.essentials_text);
        essentials_image = findViewById(R.id.essentials_image);
        go_to_cart = findViewById(R.id.go_to_cart);
        cart_rl = findViewById(R.id.cart_rl);
        essential_rv = findViewById(R.id.essential_rv);
        go_to_cart_rl = findViewById(R.id.go_to_cart_rl);

        notification = findViewById(R.id.notification);
        payment = findViewById(R.id.payment);
        recentorder = findViewById(R.id.recentorder);
        home_ll = findViewById(R.id.home_ll);

        prices = findViewById(R.id.prices);

        notification.setOnClickListener(this);
        payment.setOnClickListener(this);
        recentorder.setOnClickListener(this);
        home_ll.setOnClickListener(this);
        back.setOnClickListener(this);
        cart_rl.setOnClickListener(this);
        essential.setOnClickListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            prices.setText(Html.fromHtml("Prices are negotiable basis the Order Quantity. Please click on <strong>Call to Order</strong> for clarification.", Html.FROM_HTML_MODE_LEGACY));
        } else {
            prices.setText(Html.fromHtml("Prices are negotiable basis the Order Quantity. Please click on <strong>Call to Order</strong> for clarification."));
        }

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
            pharma_name.setText(sp.getName() +" - " + sp.getUsercode());
        }

        if (sp.getType().equals("Pharmacy")) {
            essentials_text.setTextColor(Color.parseColor("#18989e"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                essentials_image.setElevation(5f);
            }
            essentials_image.setColorFilter(Color.parseColor("#18989e"));
        } else {
            back.setVisibility(View.GONE);
            essentials_text.setText("Log Out");
            essentials_image.setImageResource(R.drawable.ic_exit_to_app_black_48dp);
            home_text.setTextColor(Color.parseColor("#18989e"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                home_image.setElevation(5f);
            }
            home_image.setColorFilter(Color.parseColor("#18989e"));
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        token = task.getResult().getToken();

                        sentFirebaseToken();
                    }
                });

        essentialLists = new ArrayList<>();
        essentials = new ArrayList<>();
        essentialList_temp = new ArrayList<>();

        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            Log.d(TAG, "onCreate: " + essential_saved);
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                String data = jsonObject.getString(sp.getUsercode());
                Type type = new TypeToken<ArrayList<EssentialList>>() {
                }.getType();
                ArrayList<EssentialList> lists = new Gson().fromJson(data, type);

                if (lists == null) {
                    lists = new ArrayList<>();
                }

                boolean isFound;
                for (int i=0;i<lists.size();i++) {
                    isFound = false;
                    for (int j=0;j<essentialLists.size();j++) {
                        if (essentialLists.get(j).getId().equals(lists.get(i).getId())) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound) {
                        essentialLists.add(lists.get(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int count_num = getCount_num(essentialLists);
        if (count_num > 0) {
            number.setVisibility(View.VISIBLE);
            go_to_cart_rl.setVisibility(View.VISIBLE);
        } else {
            number.setVisibility(View.GONE);
            go_to_cart_rl.setVisibility(View.GONE);
        }

        essentialList_temp.addAll(essentialLists);

        essentialListAdapter= new EssentialListAdapter(essentialList_temp, this);
        essential_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        essential_rv.setAdapter(essentialListAdapter);
        essentialListAdapter.setmOverallCostChangeListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        essentialAdapter = new EssentialAdapter(essentials, this, metrics,  true);
        essential_cat_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        essential_cat_rv.setAdapter(essentialAdapter);

        essentialAdapter.setSetOnClickListener(this);

        go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialLists));
            }
        });

        if (getIntent() != null && getIntent().hasExtra("category")) {
            category = getIntent().getStringExtra("category");
        }
        fetchProduct();

        getCategory();
    }

    private void sentFirebaseToken() {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String finalAndroidId = androidId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_user_info/",
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("android_id", finalAndroidId);
                params.put("reg_id", token);
                params.put("android_version", Constants.VERSION);
                params.put("manufacture_name", MedicentoUtils.getDeviceManufacture());
                params.put("model_name", getDeviceModel());
                params.put("user_ip", "");

                if (sp != null && sp.getmAllocatedPharmaId() != null) {
                    params.put("pharmacy_id", sp.getmAllocatedPharmaId());
                }

                return params;
            }
        };
        requestQueue.add(stringRequest);

        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest1 = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/register_device/",
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dev_id", finalAndroidId);
                params.put("reg_id", token);
                return params;
            }
        };
        requestQueue.add(stringRequest1);

    }

    private void fetchProduct() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/get_product_lists/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int j=0;
                        Log.d("data", "onResponse: " + response);
                        essentialList_temp.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            boolean found = false;
                            for (int i=0;i<data.length();i++) {
                                JSONObject each = data.getJSONObject(i);
                                essentialList_temp.add(new EssentialList(JsonUtils.getJsonValueFromKey(each, "name"))
                                        .setId(JsonUtils.getJsonValueFromKey(each, "id"))
                                        .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                        .setCost_100(JsonUtils.getDoubleValue(each, "qty_100_ptr"))
                                        .setCost_200(JsonUtils.getDoubleValue(each, "qty_200_ptr"))
                                        .setCost_500(JsonUtils.getDoubleValue(each, "qty_500_ptr"))
                                        .setCost_1000(JsonUtils.getDoubleValue(each, "qty_1000_ptr"))
                                        .setCost_10000(JsonUtils.getDoubleValue(each, "qty_10000_ptr"))
                                        .setMinimum_qty(JsonUtils.getIntegerValueFromJsonKey(each, "minimum_qty"))
                                        .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                        .setMrp(JsonUtils.getDoubleValue(each, "mrp"))
                                        .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url"))
                                        .setDiscount(JsonUtils.getIntegerValueFromJsonKey(each, "discount"))
                                        .setCategory(JsonUtils.getIntegerValueFromJsonKey(each, "category")));
                                for (j=0;j<essentialLists.size();j++) {
                                    if (essentialLists.get(j).getId().equalsIgnoreCase(JsonUtils.getJsonValueFromKey(each, "id"))) {
                                        essentialList_temp.get(i).setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url"))
                                                .setQty(essentialLists.get(j).getQty())
                                                .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                                .setMrp(JsonUtils.getDoubleValue(each, "mrp"))
                                                .setCategory(JsonUtils.getIntegerValueFromJsonKey(each, "category"))
                                                .setDiscount(JsonUtils.getIntegerValueFromJsonKey(each, "discount"))
                                                .setCost_100(JsonUtils.getDoubleValue(each, "qty_100_ptr"))
                                                .setCost_200(JsonUtils.getDoubleValue(each, "qty_200_ptr"))
                                                .setCost_500(JsonUtils.getDoubleValue(each, "qty_500_ptr"))
                                                .setCost_1000(JsonUtils.getDoubleValue(each, "qty_1000_ptr"))
                                                .setCost_10000(JsonUtils.getDoubleValue(each, "qty_10000_ptr"))
                                                .setMinimum_qty(JsonUtils.getIntegerValueFromJsonKey(each, "minimum_qty"))
                                                .setName(JsonUtils.getJsonValueFromKey(each, "name"));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        essentialLists.clear();
                        essentialLists.addAll(essentialList_temp);

                        progress_bar.setVisibility(View.GONE);
                        essentialListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("category", category);
                return params;
            }
        };
        progress_bar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    private void getCategory() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_category_lists/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject each = data.getJSONObject(i);
                                essentials.add(new Category(JsonUtils.getJsonValueFromKey(each, "name"))
                                        .setId(JsonUtils.getJsonValueFromKey(each, "id"))
                                        .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progress_bar_essen.setVisibility(View.GONE);
                        essentialAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar_essen.setVisibility(View.GONE);
                    }
                }
        );

        progress_bar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onCostChanged() {
        if (essentialLists != null && essentialLists.size() > 0) {
            for (int i=0;i<essentialLists.size();i++) {
                for (EssentialList essential: essentialListAdapter.getEssentialLists()) {
                    if (essentialLists.get(i).getId().equals(essential.getId())) {
                        essentialLists.get(i).setQty(essential.getQty());
                        break;
                    }
                }
            }
        }

        int count_num = getCount_num(essentialLists);
        if (count_num > 0) {
            number.setVisibility(View.VISIBLE);
            go_to_cart_rl.setVisibility(View.VISIBLE);
        } else {
            number.setVisibility(View.GONE);
            go_to_cart_rl.setVisibility(View.GONE);
        }

        String json = new Gson().toJson(essentialLists);
        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private int getCount_num(ArrayList<EssentialList> essentialLists) {
        int count_num = 0;
        float cost = 0;
        for (EssentialList essentialList: essentialLists) {
            if (essentialList.getQty() > 0) {
                count_num += 1;
                cost += essentialList.getTotalCost();
            }
        }
        count.setText("( " + count_num +" Items )");
        number.setText(""+count_num);
        price.setText("₹ "+cost);
        return count_num;
    }

    private boolean isPause = false;

    @Override
    protected void onPause() {
        super.onPause();

        if (essentialLists != null && essentialLists.size() > 0) {
            for (int i=0;i<essentialLists.size();i++) {
                for (EssentialList essential: essentialListAdapter.getEssentialLists()) {
                    if (essentialLists.get(i).getId().equals(essential.getId())) {
                        essentialLists.get(i).setQty(essential.getQty());
                        break;
                    }
                }
            }
        }

        String json = new Gson().toJson(essentialLists);
        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            Paper.init(this);
            Gson gson = new Gson();
            String cache = Paper.book().read("user");
            if (cache != null && !cache.isEmpty()) {
                sp = gson.fromJson(cache, SalesPerson.class);
            }
            String essential_saved = Paper.book().read("essential_saved_json");
            if (essential_saved != null && !essential_saved.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(essential_saved);
                    String data = jsonObject.getString(sp.getUsercode());
                    Type type = new TypeToken<ArrayList<EssentialList>>() {
                    }.getType();
                    essentialLists = new Gson().fromJson(data, type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (essentialLists == null) {
                essentialLists = new ArrayList<>();
            } else {
                int count_num = getCount_num(essentialLists);
                if (count_num > 0) {
                    number.setVisibility(View.VISIBLE);
                    go_to_cart_rl.setVisibility(View.VISIBLE);
                } else {
                    number.setVisibility(View.GONE);
                    go_to_cart_rl.setVisibility(View.GONE);
                }
            }
            fetchProduct();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.notification:
                startActivity(new Intent(EssentialsActivity.this, NotificationActivity.class));
                break;
            case R.id.payment:
                startActivity(new Intent(EssentialsActivity.this, PaymentSummaryActivity.class));
                break;
            case R.id.recentorder:
                startActivity(new Intent(EssentialsActivity.this, RecentOrderActivity.class));
                break;
            case R.id.home_ll:
                if (sp.getType().equals("Pharmacy")) {
                    startActivity(new Intent(EssentialsActivity.this, HomeActivity.class));
                }
                break;
            case R.id.back:
                onBackPressed();
                break;
            case  R.id.essential:
                if (!sp.getType().equals("Pharmacy")) {
                    Paper.book().delete("user");
                    Intent intent = new Intent(EssentialsActivity.this, SignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.cart_rl:
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialLists));
                break;
        }
    }

    @Override
    public void onClickCategory(String id) {
        if (essentialList_temp == null) {
            essentialList_temp = new ArrayList<>();

            essentialListAdapter= new EssentialListAdapter(essentialList_temp, this);
            essential_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            essential_rv.setAdapter(essentialListAdapter);
            essentialListAdapter.setmOverallCostChangeListener(this);

        } else {
            essentialList_temp.clear();
        }
        for (int i=0;i<essentialLists.size();i++) {
            if ((essentialLists.get(i).getCategory()+"").equals(id)) {
                essentialList_temp.add(essentialLists.get(i));
            }
        }

        essentialListAdapter.notifyDataSetChanged();
    }
}
