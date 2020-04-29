package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.text.Line;
import com.medicento.retailerappmedi.CartPageActivity;
import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.GroupAdapter;
import com.medicento.retailerappmedi.adapter.ImageAdapter;
import com.medicento.retailerappmedi.adapter.ImagesAdapter;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.Group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParticularOrderActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<String> images;
    ArrayList<Category> groups;
    ImageAdapter imageAdapter;
    RecyclerView image_rv, group_rv;
    GroupAdapter groupAdapter;
    Button add_to_cart;
    ImageView back, cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_order);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        back = findViewById(R.id.back);
        cart = findViewById(R.id.cart);
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

                            for (int i=0;i<data.length();i++) {
                                JSONObject each = data.getJSONObject(i);
                                groups.add(new Category(JsonUtils.getJsonValueFromKey(each, "name")));
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
