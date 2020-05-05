package com.medicento.retailerappmedi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.medicento.retailerappmedi.create_account.ConfirmAddressActivity;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class CreateNewAddressActivity extends AppCompatActivity {

    EditText address, state, city, pincode, area;
    TextView name, message;
    Button submit;
    AlertDialog alert;
    ImageView back;
    ProgressBar progress_bar;
    SalesPerson sp;
    com.medicento.retailerappmedi.data.Address address_obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_address);

        Paper.init(this);
        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }
        address = findViewById(R.id.address);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        back = findViewById(R.id.back);
        area = findViewById(R.id.area);
        pincode = findViewById(R.id.pincode);
        progress_bar = findViewById(R.id.progress_bar);
        name = findViewById(R.id.name);
        message = findViewById(R.id.message);
        submit = findViewById(R.id.submit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent() != null && getIntent().hasExtra("address_obj")) {

            address_obj = (com.medicento.retailerappmedi.data.Address) getIntent().getSerializableExtra("address_obj");

            address.setText(address_obj.getAddress());
            state.setText(address_obj.getState());
            city.setText(address_obj.getCity());
            name.setText(address_obj.getName());
            pincode.setText(address_obj.getPincode());
            area.setText(address_obj.getArea());

        } else {
            name.setText(sp.getName());
        }

        if (address != null && address.getText().toString().isEmpty()) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    "https://api.ipgeolocation.io/ipgeo?apiKey=6e112e58ec4f42b19d98fdf4cc68fbe9",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("data", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Geocoder geocoder = new Geocoder(CreateNewAddressActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")), 1);
                                    Address obj = addresses.get(0);
                                    String add = obj.getAddressLine(0);
                                    add = add + "\n" + obj.getCountryName();
                                    add = add + "\n" + obj.getCountryCode();
                                    add = add + "\n" + obj.getAdminArea();
                                    add = add + "\n" + obj.getPostalCode();
                                    add = add + "\n" + obj.getSubAdminArea();
                                    add = add + "\n" + obj.getLocality();
                                    add = add + "\n" + obj.getSubThoroughfare();

                                    city.setText(obj.getAdminArea());
                                    state.setText(obj.getSubAdminArea());
                                    address.setText(obj.getAddressLine(0));
                                    pincode.setText(obj.getPostalCode());

                                    getAreaName();
                                    Log.v("IGA", "Address" + add);
                                    Toast.makeText(CreateNewAddressActivity.this, "Address Found", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showVolleyError(error);
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setVisibility(View.GONE);
                message.setText("Please fill the mandatory fields");
                if (address.getText().toString().isEmpty()) {
                    address.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (state.getText().toString().isEmpty()) {
                    state.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (city.getText().toString().isEmpty()) {
                    city.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (pincode.getText().toString().isEmpty()) {
                    pincode.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (area.getText().toString().isEmpty()) {
                    area.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (message.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (!amIConnect(CreateNewAddressActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewAddressActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setIcon(R.mipmap.ic_launcher_new);
                    builder.setCancelable(false);
                    builder.setMessage("Please Connect To The Internet")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submit.performClick();
                                    alert.dismiss();
                                }
                            });
                    alert = builder.create();
                    alert.show();
                } else {
                    sendDetailsToServer();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getAreaName() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/area/get_area_from_address/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject results = new JSONObject(response);
                            area.setText(results.getString("results"));
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
                                Log.d("error_message", "showVolleyError: no Response");
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("error_message", "showVolleyError: " + body);
                            } catch (UnsupportedEncodingException e) {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String address_line = address.getText().toString();
                params.put("address", address_line);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendDetailsToServer() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_and_edit_pharmacy_address/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_bar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(CreateNewAddressActivity.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("area", area.getText().toString());
                params.put("state", state.getText().toString());
                params.put("city", city.getText().toString());
                params.put("address", address.getText().toString());
                params.put("pin_code", pincode.getText().toString());
                params.put("pharmacy_id", sp.getId());
                params.put("code", sp.getUsercode());
                if (address_obj != null) {
                    params.put("id", address_obj.getId());
                }

                Log.d("data", "getParams: " + params.toString());
                return params;
            }
        };

        progress_bar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                2));
        requestQueue.add(stringRequest);
    }
}
