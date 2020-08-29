package com.medicento.retailerappmedi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.AddressAdapter;
import com.medicento.retailerappmedi.data.Address;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class ChangeAddressActivity extends AppCompatActivity implements AddressAdapter.itemClick{

    RecyclerView address_rv;
    private ArrayList<Address> addresses;
    private AddressAdapter addressAdapter;
    SalesPerson sp;
    ImageView back;
    RelativeLayout add_new_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        Paper.init(this);
        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        add_new_rl = findViewById(R.id.add_new_rl);
        address_rv = findViewById(R.id.address_rv);
        back = findViewById(R.id.back);

        addresses = new ArrayList<>();

        addressAdapter = new AddressAdapter(addresses, this);
        address_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        address_rv.setAdapter(addressAdapter);
        addressAdapter.setItemClick(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add_new_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangeAddressActivity.this, CreateNewAddressActivity.class));
            }
        });

        getAddress();
    }

    private void getAddress() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_pharmacy_address/?id=" + sp.getmAllocatedPharmaId() + "&code=" + sp.getUsercode(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject each = data.getJSONObject(i);
                                addresses.add(new Address()
                                        .setId(JsonUtils.getJsonValueFromKey(each, "id"))
                                        .setName(JsonUtils.getJsonValueFromKey(each, "name"))
                                        .setAddress(JsonUtils.getJsonValueFromKey(each, "address"))
                                        .setState(JsonUtils.getJsonValueFromKey(each, "state"))
                                        .setCity(JsonUtils.getJsonValueFromKey(each, "city"))
                                        .setArea(JsonUtils.getJsonValueFromKey(each, "area"))
                                        .setPincode(JsonUtils.getJsonValueFromKey(each, "pincode"))
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        addressAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(ChangeAddressActivity.this, CreateNewAddressActivity.class).putExtra("address_obj", addresses.get(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            addresses.clear();
            getAddress();
            isPause = false;
        }
    }

    private boolean isPause;

    @Override
    protected void onPause() {
        super.onPause();

        isPause = true;
    }

    @Override
    public void onRemove(final int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/remove_pharmacy_address/?id=" + addresses.get(position).getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        addresses.remove(position);
                        addressAdapter.notifyDataSetChanged();
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

    @Override
    public void setAddress(int position) {
        sp.setAddress(addresses.get(position).getAddress());
        sp.setState_name(addresses.get(position).getState().toString());
        sp.setCity_name(addresses.get(position).getCity().toString());
        Paper.book().write("user", new Gson().toJson(sp));
    }
}
