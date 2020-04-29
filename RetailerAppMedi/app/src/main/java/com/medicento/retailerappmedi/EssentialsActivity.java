package com.medicento.retailerappmedi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.medicento.retailerappmedi.activity.PaymentGateWayActivity;
import com.medicento.retailerappmedi.activity.PaymentSummaryActivity;
import com.medicento.retailerappmedi.adapter.EssentialListAdapter;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.Medicine;
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
    private RecyclerView essential_rv;
    RelativeLayout go_to_cart_rl, cart_rl;
    TextView  essentials_text;
    ImageView essentials_image, back;
    LinearLayout home_ll, notification, payment, recentorder;
    String category = "Mask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_essentials);

        count = findViewById(R.id.count);
        price = findViewById(R.id.price);
        number = findViewById(R.id.number);
        back = findViewById(R.id.back);
        pharma_name = findViewById(R.id.pharma_name);
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        essentials_text.setTextColor(Color.parseColor("#18989e"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            essentials_image.setElevation(5f);
        }
        essentials_image.setColorFilter(Color.parseColor("#18989e"));

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
            pharma_name.setText(sp.getName());
        }

        essentialLists = new ArrayList<>();

        String essential_saved = Paper.book().read("essential_saved");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            Type type = new TypeToken<ArrayList<EssentialList>>() {
            }.getType();
            essentialLists = new Gson().fromJson(essential_saved, type);
        }

        essentialListAdapter= new EssentialListAdapter(essentialLists, this);
        essential_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        essential_rv.setAdapter(essentialListAdapter);
        essentialListAdapter.setmOverallCostChangeListener(this);

        go_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialListAdapter.getEssentialLists()));
            }
        });
        
        if (getIntent() != null && getIntent().hasExtra("category")) {
            category = getIntent().getStringExtra("category");
            fetchProduct();
        }
    }

    private void fetchProduct() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/get_product_lists/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            boolean found = false;
                            for (int i=0;i<data.length();i++) {
                                JSONObject each = data.getJSONObject(i);
                                found = false;
                                for (EssentialList essentialList: essentialLists) {
                                    if (essentialList.getName().equalsIgnoreCase(JsonUtils.getJsonValueFromKey(each, "name"))) {
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    essentialLists.add(new EssentialList(JsonUtils.getJsonValueFromKey(each, "name"))
                                            .setId(JsonUtils.getJsonValueFromKey(each, "id"))
                                            .setCost(JsonUtils.getDoubleValue(each, "ptr"))
                                            .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        essentialListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

        requestQueue.add(stringRequest);
    }

    @Override
    public void onCostChanged() {
        int count_num = 0;
        float cost = 0;
        for (EssentialList essentialList: essentialListAdapter.getEssentialLists()) {
            if (essentialList.getQty() > 0) {
                count_num += 1;
                cost += essentialList.getQty()*essentialList.getCost();
            }
        }
        count.setText("( " + count_num +" Items )");
        number.setText(""+count_num);
        price.setText("₹ "+cost);
        if (count_num > 0) {
            number.setVisibility(View.VISIBLE);
            go_to_cart_rl.setVisibility(View.VISIBLE);
        } else {
            number.setVisibility(View.GONE);
            go_to_cart_rl.setVisibility(View.GONE);
        }

        String json = new Gson().toJson(essentialLists);
        Paper.book().write("essential_saved", json);
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
                startActivity(new Intent(EssentialsActivity.this, HomeActivity.class));
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.cart_rl:
                startActivity(new Intent(EssentialsActivity.this, CartPageActivity.class).putExtra("list", essentialListAdapter.getEssentialLists()));
                break;
        }
    }
}
