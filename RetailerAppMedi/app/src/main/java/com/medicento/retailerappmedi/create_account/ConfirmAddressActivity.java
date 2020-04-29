package com.medicento.retailerappmedi.create_account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.medicento.retailerappmedi.AccountCredentialdsActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Register;
import com.medicento.retailerappmedi.SignUpActivity;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.ConfirmationAccountActivity;
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

public class ConfirmAddressActivity extends AppCompatActivity {

    EditText address, state, city, pincode, area;
    TextView name, message;
    String gst, drug, number;
    Button sign_in_btn;
    AlertDialog alert;
    ImageView back;
    ProgressBar progress_bar;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address);

        address = findViewById(R.id.address);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        back = findViewById(R.id.back);
        area = findViewById(R.id.area);
        pincode = findViewById(R.id.pincode);
        progress_bar = findViewById(R.id.progress_bar);
        name = findViewById(R.id.name);
        message = findViewById(R.id.message);
        sign_in_btn = findViewById(R.id.sign_in_btn);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent()  != null) {
            address.setText(getIntent().getStringExtra("address"));
            state.setText(getIntent().getStringExtra("state"));
            city.setText(getIntent().getStringExtra("city"));
            name.setText(getIntent().getStringExtra("name"));
            pincode.setText(getIntent().getStringExtra("pincode"));
            gst = getIntent().getStringExtra("gst");
            drug = getIntent().getStringExtra("drug");
            number = getIntent().getStringExtra("number");

            getAreaName();
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
                                Geocoder geocoder = new Geocoder(ConfirmAddressActivity.this, Locale.getDefault());
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

                                    Log.v("IGA", "Address" + add);
                                    Toast.makeText(ConfirmAddressActivity.this, "Address Found", Toast.LENGTH_SHORT).show();
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

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
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
                if (!amIConnect(ConfirmAddressActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmAddressActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setIcon(R.mipmap.ic_launcher_new);
                    builder.setCancelable(false);
                    builder.setMessage("Please Connect To The Internet")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sign_in_btn.performClick();
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
                "http://stage.medicento.com:8080/pharmacy/signup/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Register", response);
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");

                            if (message.equals("Pharmacy Created")) {

                                JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");
                                String code = JsonUtils.getJsonValueFromKey(pharmacy, "pharma_code");

                                JSONObject area = pharmacy.getJSONObject("area");

                                sp = new SalesPerson(
                                        JsonUtils.getJsonValueFromKey(pharmacy, "name"),
                                        0L,
                                        0,
                                        0,
                                        0,
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"),
                                        JsonUtils.getJsonValueFromKey(area, "id"),
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"));

                                sp.setmAllocatedStateId(JsonUtils.getJsonValueFromKey(area, "state"));
                                sp.setmAllocatedCityId(JsonUtils.getJsonValueFromKey(area, "city"));

                                JSONObject city = pharmacy.getJSONObject("city");
                                JSONObject state = pharmacy.getJSONObject("state");

                                sp.setAddress(JsonUtils.getJsonValueFromKey(pharmacy, "address"));
                                sp.setEmail(JsonUtils.getJsonValueFromKey(pharmacy, "email_id"));
                                sp.setArea_name(JsonUtils.getJsonValueFromKey(area, "name"));
                                sp.setCity_name(JsonUtils.getJsonValueFromKey(city, "name"));
                                sp.setState_name(JsonUtils.getJsonValueFromKey(state, "name"));

                                sp.setPhone(number);
                                sp.setUsercode(code);

                                Paper.book().write("user", new Gson().toJson(sp));

                                progress_bar.setVisibility(View.GONE);
                                sign_in_btn.setVisibility(View.VISIBLE);
                                startActivity(
                                        new Intent(ConfirmAddressActivity.this, ConfirmationAccountActivity.class)
                                            .putExtra("number", number)
                                            .putExtra("pharmacode", code));

                            } else if (message.equals("Already Created")) {
                                Toast.makeText(ConfirmAddressActivity.this, "Pharmacy Already Exists with the provided Phone Number, Kindly use FORGOT PASSWORD with the Phone Number to access your medicento Credentials", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ConfirmAddressActivity.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                            }
                            progress_bar.setVisibility(View.GONE);
                            sign_in_btn.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress_bar.setVisibility(View.GONE);
                            sign_in_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(ConfirmAddressActivity.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        sign_in_btn.setVisibility(View.VISIBLE);
                        Toast.makeText(ConfirmAddressActivity.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("area_name", area.getText().toString());
                params.put("address", address.getText().toString());
                params.put("gst", gst);
                params.put("drug", drug);
                params.put("email", "");
                params.put("phone", number);
                params.put("owner_name", "");
                params.put("pin_code", pincode.getText().toString());

                return params;
            }
        };

        progress_bar.setVisibility(View.VISIBLE);
        sign_in_btn.setVisibility(View.GONE);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                2));
        requestQueue.add(stringRequest);

    }
}
