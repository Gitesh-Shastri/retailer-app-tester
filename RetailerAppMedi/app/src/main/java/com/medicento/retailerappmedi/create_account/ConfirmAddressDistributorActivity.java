package com.medicento.retailerappmedi.create_account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.medicento.retailerappmedi.activity.ConfirmationAccountActivity;
import com.medicento.retailerappmedi.activity.CreateNewAddressActivity;
import com.medicento.retailerappmedi.data.City;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class ConfirmAddressDistributorActivity extends AppCompatActivity {

    EditText address, state, city, pincode, area;
    TextView name, message;
    String gst, drug, number, fssai, trade;
    Button sign_in_btn;
    AlertDialog alert;
    ImageView back;
    ProgressBar progress_bar;
    SalesPerson sp;

    Spinner state_spinner, city_spinner;
    ArrayList<City> city_list;
    ArrayList<State> state_list;
    RequestQueue requestQueue;
    State selected_state;
    City selected_city;
    int state_position = 0;
    int city_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_address_distributor);

        state_list = new ArrayList<>();
        city_list = new ArrayList<>();

        address = findViewById(R.id.address);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        back = findViewById(R.id.back);
        area = findViewById(R.id.area);
        pincode = findViewById(R.id.pincode);
        progress_bar = findViewById(R.id.progress_bar);
        city_spinner = findViewById(R.id.city_spinner);
        state_spinner = findViewById(R.id.state_spinner);
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
            fssai = getIntent().getStringExtra("fssai");
            trade = getIntent().getStringExtra("trade");

            getAreaName();
        }

        requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/area/state/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject state_obj = jsonArray.getJSONObject(i);

                                state_list.add(new State(JsonUtils.getJsonValueFromKey(state_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(state_obj, "id")));

                                if (JsonUtils.getJsonValueFromKey(state_obj, "name").equals(state.getText().toString())) {
                                    state_position = i;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setStateSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(stringRequest);

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
                if (!amIConnect(ConfirmAddressDistributorActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmAddressDistributorActivity.this);
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

    private void setStateSpinner() {

        HintAdapter<State> statehintAdapter = new HintAdapter<State>(
                ConfirmAddressDistributorActivity.this,
                R.layout.spinner_item,
                "Select State",
                state_list) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(state_list.get(position).getName());
                return view;
            }
        };

        HintSpinner<State> hintSpinner = new HintSpinner<>(
                state_spinner,
                statehintAdapter,
                new HintSpinner.Callback<State>() {
                    @Override
                    public void onItemSelected(int position, State itemAtPosition) {
                        selected_state = itemAtPosition;
                        state.setText(selected_state.getName());
                        fetchCity(selected_state);
                    }
                });
        hintSpinner.init();
    }

    public void fetchCity(State state) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/area/city/?state="+state.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject city_obj = jsonArray.getJSONObject(i);

                                city_list.add(new City(JsonUtils.getJsonValueFromKey(city_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "id")));

                                if (JsonUtils.getJsonValueFromKey(city_obj, "name").equals(city.getText().toString())) {
                                    city_position = i;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setCitySpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        if (city_list != null) {
            city_list = new ArrayList<>();
        } else {
            city_list.clear();
        }
        requestQueue.add(stringRequest);
    }

    private void setCitySpinner() {
        HintAdapter<City> statehintAdapter = new HintAdapter<City>(
                ConfirmAddressDistributorActivity.this,
                R.layout.spinner_item,
                "Select City",
                city_list) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(city_list.get(position).getName());
                return view;
            }
        };

        HintSpinner<City> hintSpinner = new HintSpinner<>(
                city_spinner,
                statehintAdapter,
                new HintSpinner.Callback<City>() {
                    @Override
                    public void onItemSelected(int position, City itemAtPosition) {
                        selected_city = itemAtPosition;
                        city.setText(selected_city.getName());
                    }
                });
        hintSpinner.init();
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
                "http://stage.medicento.com:8080/distributor/signup/",
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

                               JSONObject state = pharmacy.getJSONObject("state");
                                JSONObject city = pharmacy.getJSONObject("city");

                                sp = new SalesPerson(
                                        JsonUtils.getJsonValueFromKey(pharmacy, "name"),
                                        0L,
                                        0,
                                        0,
                                        0,
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"),
                                        "",
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"));

                                sp.setmAllocatedStateId(JsonUtils.getJsonValueFromKey(state, "state"));
                                sp.setmAllocatedCityId(JsonUtils.getJsonValueFromKey(city, "city"));

                                sp.setAddress(JsonUtils.getJsonValueFromKey(pharmacy, "address"));
                                sp.setEmail(JsonUtils.getJsonValueFromKey(pharmacy, "email_id"));
                                sp.setArea_name("");
                                sp.setCity_name(JsonUtils.getJsonValueFromKey(city, "name"));
                                sp.setState_name(JsonUtils.getJsonValueFromKey(state, "name"));

                                sp.setPhone(number);
                                sp.setUsercode(code);

                                sp.setType("Distributor");

                                Paper.book().write("user", new Gson().toJson(sp));
                                Paper.book().write("distributor", "yes");

                                progress_bar.setVisibility(View.GONE);
                                sign_in_btn.setVisibility(View.VISIBLE);
                                startActivity(
                                        new Intent(ConfirmAddressDistributorActivity.this, ConfirmationAccountActivity.class)
                                                .putExtra("distributor", "yes")
                                                .putExtra("number", number)
                                                .putExtra("pharmacode", code));

                            } else if (message.equals("Already Created")) {
                                Toast.makeText(ConfirmAddressDistributorActivity.this, "Distributor Already Exists with the provided Phone Number, Kindly use FORGOT PASSWORD with the Phone Number to access your medicento Credentials", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ConfirmAddressDistributorActivity.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                            }
                            progress_bar.setVisibility(View.GONE);
                            sign_in_btn.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress_bar.setVisibility(View.GONE);
                            sign_in_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(ConfirmAddressDistributorActivity.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        sign_in_btn.setVisibility(View.VISIBLE);
                        Toast.makeText(ConfirmAddressDistributorActivity.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
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
                params.put("fssai", fssai);
                params.put("trade", trade);
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

