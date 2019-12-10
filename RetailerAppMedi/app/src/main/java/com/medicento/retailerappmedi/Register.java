package com.medicento.retailerappmedi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.data.Area;
import com.medicento.retailerappmedi.data.City;
import com.medicento.retailerappmedi.data.Constants;
import com.google.gson.JsonObject;
import com.medicento.retailerappmedi.data.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class Register extends AppCompatActivity {

    EditText shop_name, shop_address, pin_code, owner_name, phone_number, email, drug_license, gst_license, area_name, area_pincode;
    Spinner area_spinner, state_spinner, city_spinner;

    RelativeLayout relativeLayout;
    Snackbar snackbar;

    ProgressDialog progressDialog;

    ArrayList<Area> areas;
    ArrayList<State> state;
    ArrayList<City> city;

    String id, selectedState, selectedCity;

    Button create, terms_of_service, login, add;

    Area selected_area;
    State selected_state;
    City selected_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relativeLayout = findViewById(R.id.relative);

        areas = new ArrayList<>();

        selected_area = null;
        selected_city = null;
        selected_state = null;

        shop_name = findViewById(R.id.shop_name);
        shop_address = findViewById(R.id.shop_address);
        pin_code = findViewById(R.id.owner_pincode);
        owner_name = findViewById(R.id.owner_name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        drug_license = findViewById(R.id.drug_license);
        gst_license = findViewById(R.id.gst_license);
        login = findViewById(R.id.login);
        state_spinner = findViewById(R.id.state_spinner);
        city_spinner = findViewById(R.id.city_spinner);
        add = findViewById(R.id.add);

        city = new ArrayList<>();

        selectedCity = "";
        selectedState = "";

        hideSoftKeyboard();

        terms_of_service = findViewById(R.id.termsOfService);
        area_spinner = findViewById(R.id.area_spinner);
        create = findViewById(R.id.create);

        getState();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shop_name.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Name", Snackbar.LENGTH_SHORT).show();
                } else if (shop_address.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Adress", Snackbar.LENGTH_SHORT).show();
                } else if (phone_number.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                } else if (drug_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Drug License", Snackbar.LENGTH_SHORT).show();
                } else if (gst_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter GST License", Snackbar.LENGTH_SHORT).show();
                } else if(selected_state == null ){
                    Snackbar.make(relativeLayout, "Please Select State", Snackbar.LENGTH_SHORT).show();
                } else if(selected_city == null ){
                    Snackbar.make(relativeLayout, "Please Select City", Snackbar.LENGTH_SHORT).show();
                } else if(selected_area == null ){
                    Snackbar.make(relativeLayout, "Please Select Area", Snackbar.LENGTH_SHORT).show();
                } else {
                    sendDetailsToServer();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, SignUpActivity.class));
                finish();
            }
        });

        terms_of_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, TermsAndCondition.class));
            }
        });
    }

    private void sendDetailsToServer() {

        snackbar = Snackbar.make(relativeLayout, "Please Wait Creating Account", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/signup/",
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

                                final Dialog dialog1 = new Dialog(Register.this);
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog1.setContentView(R.layout.congo_registered);

                                TextView phone = dialog1.findViewById(R.id.phone);
                                phone.setText("Your Registered Mobile Number : " + phone_number.getText().toString());

                                TextView email1 = dialog1.findViewById(R.id.email);
                                email1.setText("Your Registered Email Address : " + email.getText().toString());

                                TextView code1 = dialog1.findViewById(R.id.code);
                                code1.setText("Your Pharma Code : " + code);

                                Button back1 = dialog1.findViewById(R.id.back);
                                back1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog1.dismiss();
                                        startActivity(new Intent(Register.this, SignUpActivity.class));
                                        finish();
                                    }
                                });

                                dialog1.show();
                                Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();
                            } else if (message.equals("Already Created")) {

                                JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");

                                String email_id = JsonUtils.getJsonValueFromKey(pharmacy, "email_id");
                                if (email_id.equals(email.getText().toString())) {
                                    Snackbar.make(relativeLayout, "Pharmacy Already Exists with the provided Email Id, Kindly use FORGOT PASSWORD with the Email ID to access your medicento Credentials", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(relativeLayout, "Pharmacy Already Exists with the provided Phone Number, Kindly use FORGOT PASSWORD with the Phone Number to access your medicento Credentials", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(relativeLayout, "Something Went Wrong !", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(relativeLayout, "Something Went Wrong !" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(relativeLayout, "Error in connecting to server. Please try again after some time", Snackbar.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("name", shop_name.getText().toString());
                params.put("area", selected_area.getId());
                params.put("address", shop_address.getText().toString());
                params.put("gst", gst_license.getText().toString());
                params.put("drug", drug_license.getText().toString());
                params.put("email", email.getText().toString());
                params.put("phone", phone_number.getText().toString());
                params.put("owner_name", owner_name.getText().toString());
                params.put("pin_code", pin_code.getText().toString());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                1,
                2));
        requestQueue.add(stringRequest);

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void getState() {

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading State");
        progressDialog.show();

        state = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://54.161.199.63:8080/api/area/state/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject state_obj = jsonArray.getJSONObject(i);

                                state.add(new State(JsonUtils.getJsonValueFromKey(state_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(state_obj, "id")));
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
                        if(progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(Register.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    public void setStateSpinner() {

        HintAdapter<State> statehintAdapter = new HintAdapter<State>(
                Register.this,
                R.layout.spinner_item,
                "Select State",
                state) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(state.get(position).getName());
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
                        fetchCity(itemAtPosition);
                    }
                });
        hintSpinner.init();

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void fetchCity(State state) {

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading City");
        progressDialog.show();

        city = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://54.161.199.63:8080/api/area/city/?state="+state.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject city_obj = jsonArray.getJSONObject(i);

                                city.add(new City(JsonUtils.getJsonValueFromKey(city_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "id")));
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
                        if(progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(Register.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void setCitySpinner() {
        HintAdapter<City> statehintAdapter = new HintAdapter<City>(
                Register.this,
                R.layout.spinner_item,
                "Select City",
                city) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(city.get(position).getName());
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
                        fetchArea(itemAtPosition);
                    }
                });
        hintSpinner.init();

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
            if(areas != null) {
                areas.clear();
                setAreaSpinner();
            }
        }
    }

    public void fetchArea(City city) {

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Area");
        progressDialog.show();

        areas = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://54.161.199.63:8080/api/area/area/?city="+city.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject city_obj = jsonArray.getJSONObject(i);

                                areas.add(new Area(
                                        JsonUtils.getJsonValueFromKey(city_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "id"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "city"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "state")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setAreaSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        Toast.makeText(Register.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void setAreaSpinner() {
        HintAdapter<Area> statehintAdapter = new HintAdapter<Area>(
                Register.this,
                R.layout.spinner_item,
                "Select Area",
                areas) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(areas.get(position).getName());
                return view;
            }
        };

        HintSpinner<Area> hintSpinner = new HintSpinner<>(
                area_spinner,
                statehintAdapter,
                new HintSpinner.Callback<Area>() {
                    @Override
                    public void onItemSelected(int position, Area itemAtPosition) {
                        selected_area = itemAtPosition;
                    }
                });
        hintSpinner.init();

        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
