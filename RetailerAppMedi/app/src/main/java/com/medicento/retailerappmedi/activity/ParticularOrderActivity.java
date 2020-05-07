package com.medicento.retailerappmedi.activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.CartPageActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.GroupAdapter;
import com.medicento.retailerappmedi.adapter.ImageAdapter;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.Essential;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.paperdb.Paper;

public class ParticularOrderActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> images;
    ArrayList<EssentialList> essentialLists;
    ArrayList<Category> groups;
    ImageAdapter imageAdapter;
    RecyclerView image_rv, group_rv;
    GroupAdapter groupAdapter;
    Button add_to_cart;
    ImageView back, cart, add, sub;
    EssentialList essentialList;
    TextView name, ptr;
    EditText qty;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_order);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        qty = findViewById(R.id.qty);
        ptr = findViewById(R.id.ptr);
        add = findViewById(R.id.add);
        sub = findViewById(R.id.sub);
        back = findViewById(R.id.back);
        cart = findViewById(R.id.cart);
        name = findViewById(R.id.name);
        image_rv = findViewById(R.id.image_rv);
        group_rv = findViewById(R.id.group_rv);
        add_to_cart = findViewById(R.id.add_to_cart);

        back.setOnClickListener(this);
        cart.setOnClickListener(this);

        images = new ArrayList<>();
        groups = new ArrayList<>();

        images.add("");
        images.add("");
        images.add("");
        images.add("");
        images.add("");

        if (getIntent() != null && getIntent().hasExtra("item")) {
            essentialList = (EssentialList) getIntent().getSerializableExtra("item");
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        imageAdapter = new ImageAdapter(images, this, metrics);
        image_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        image_rv.setAdapter(imageAdapter);

        groupAdapter = new GroupAdapter(groups, this);
        group_rv.setLayoutManager(new GridLayoutManager(this, 2));
        group_rv.setAdapter(groupAdapter);

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_to_cart.getText().toString().equalsIgnoreCase("Go To Cart")) {
                    ArrayList<EssentialList> essentialLists = new ArrayList<>();
                    essentialLists.add(new EssentialList());
                    startActivity(new Intent(ParticularOrderActivity.this, CartPageActivity.class)
                            .putExtra("list", essentialLists));
                }
                add_to_cart.setText("Go To Cart");
            }
        });

        name.setText(essentialList.getName());
        qty.setText(essentialList.getQty() + "");
        ptr.setText("₹ " + essentialList.getCost());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                essentialList.setQty(essentialList.getQty() + 1);
                qty.setText(essentialList.getQty() + "");
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (essentialList.getQty() >= 1) {
                    essentialList.setQty(essentialList.getQty() - 1);
                    qty.setText(essentialList.getQty() + "");
                }
            }
        });

        getCategory();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.cart:
                ArrayList<EssentialList> essentialLists = new ArrayList<>();
                essentialLists.add(new EssentialList().setQty(1));
                startActivity(new Intent(ParticularOrderActivity.this, CartPageActivity.class)
                        .putExtra("list", essentialLists));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (essentialLists != null && essentialLists.size() > 0) {
            for (int i=0;i<essentialLists.size();i++) {
                if (essentialLists.get(i).getId().equals(essentialList.getId())) {
                    essentialLists.get(i).setQty(essentialList.getQty());
                    break;
                }
            }
        }
        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                jsonObject.put(sp.getUsercode(), "");
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(sp.getUsercode(), "");
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onBackPressed();
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
                                if (essentialList.getCategory() != JsonUtils.getIntegerValueFromJsonKey(each, "id")) {
                                    groups.add(new Category(JsonUtils.getJsonValueFromKey(each, "name"))
                                            .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url")));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        groupAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        requestQueue.add(stringRequest);
    }

}
