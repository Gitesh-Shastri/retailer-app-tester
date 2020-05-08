package com.medicento.retailerappmedi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.NotificationActivity;
import com.medicento.retailerappmedi.activity.PaymentSummaryActivity;
import com.medicento.retailerappmedi.adapter.EssentialAdapter;
import com.medicento.retailerappmedi.adapter.EssentialListAdapter;
import com.medicento.retailerappmedi.data.Category;
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

public class EssentialsActivity extends AppCompatActivity implements View.OnClickListener, EssentialListAdapter.OverallCostChangeListener{

    TextView pharma_name, count, price, number;
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

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
            pharma_name.setText(sp.getName());
        }

        if (sp.getType().equals("Pharmacy")) {
            essentials_text.setTextColor(Color.parseColor("#18989e"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                essentials_image.setElevation(5f);
            }
            essentials_image.setColorFilter(Color.parseColor("#18989e"));
        } else {
            essentials_text.setText("Log Out");
            home_text.setTextColor(Color.parseColor("#18989e"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                home_image.setElevation(5f);
            }
            home_image.setColorFilter(Color.parseColor("#18989e"));
        }

        essentialLists = new ArrayList<>();
        essentials = new ArrayList<>();

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
        }

        int count_num = getCount_num(essentialLists);
        if (count_num > 0) {
            number.setVisibility(View.VISIBLE);
            go_to_cart_rl.setVisibility(View.VISIBLE);
        } else {
            number.setVisibility(View.GONE);
            go_to_cart_rl.setVisibility(View.GONE);
        }

        essentialListAdapter= new EssentialListAdapter(essentialLists, this);
        essential_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        essential_rv.setAdapter(essentialListAdapter);
        essentialListAdapter.setmOverallCostChangeListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        essentialAdapter = new EssentialAdapter(essentials, this, metrics);
        essential_cat_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        essential_cat_rv.setAdapter(essentialAdapter);

        go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialListAdapter.getEssentialLists()));
            }
        });
        
        if (getIntent() != null && getIntent().hasExtra("category")) {
            category = getIntent().getStringExtra("category");
        }
        fetchProduct();

        getCategory();
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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            boolean found = false;
                            for (int i=0;i<data.length();i++) {
                                JSONObject each = data.getJSONObject(i);
                                found = false;
                                for (j=0;j<essentialLists.size();j++) {
                                    if (essentialLists.get(j).getName().equalsIgnoreCase(JsonUtils.getJsonValueFromKey(each, "name"))) {
                                        essentialLists.get(j).setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url"));
                                        essentialLists.get(j).setCost(JsonUtils.getDoubleValue(each, "ptr"));
                                        essentialLists.get(j).setCategory(JsonUtils.getIntegerValueFromJsonKey(each, "category"));
                                        essentialLists.get(i).setDiscount(JsonUtils.getIntegerValueFromJsonKey(each, "discount"));
                                        essentialLists.get(i).setCost_100(JsonUtils.getDoubleValue(each, "qty_100_ptr"))
                                                .setCost_200(JsonUtils.getDoubleValue(each, "qty_200_ptr"))
                                                .setCost_500(JsonUtils.getDoubleValue(each, "qty_500_ptr"))
                                                .setCost_1000(JsonUtils.getDoubleValue(each, "qty_1000_ptr"))
                                                .setCost_10000(JsonUtils.getDoubleValue(each, "qty_10000_ptr"));
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    essentialLists.add(new EssentialList(JsonUtils.getJsonValueFromKey(each, "name"))
                                            .setId(JsonUtils.getJsonValueFromKey(each, "id"))
                                            .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                            .setCost_100(JsonUtils.getDoubleValue(each, "qty_100_ptr"))
                                            .setCost_200(JsonUtils.getDoubleValue(each, "qty_200_ptr"))
                                            .setCost_500(JsonUtils.getDoubleValue(each, "qty_500_ptr"))
                                            .setCost_1000(JsonUtils.getDoubleValue(each, "qty_1000_ptr"))
                                            .setCost_10000(JsonUtils.getDoubleValue(each, "qty_10000_ptr"))
                                            .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                            .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                            .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                            .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url"))
                                            .setDiscount(JsonUtils.getIntegerValueFromJsonKey(each, "discount"))
                                            .setCategory(JsonUtils.getIntegerValueFromJsonKey(each, "category")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        int count_num = getCount_num(essentialListAdapter.getEssentialLists());
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
        String json = new Gson().toJson(essentialListAdapter.getEssentialLists());
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
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialListAdapter.getEssentialLists()));
                break;
        }
    }
}
