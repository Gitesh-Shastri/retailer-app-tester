package com.developer.medicento.retailerappmedi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.medicento.retailerappmedi.data.Area;
import com.developer.medicento.retailerappmedi.data.Constants;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText shop_name, shop_address, pin_code, owner_name, phone_number, email, drug_license, gst_license;
    Spinner area_spinner;

    RelativeLayout relativeLayout;
    Snackbar snackbar;

    ArrayList<Area> areas;

    Button create, terms_of_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relativeLayout = findViewById(R.id.relative);

        areas = new ArrayList<>();

        shop_name = findViewById(R.id.shop_name);
        shop_address  = findViewById(R.id.shop_address);
        pin_code = findViewById(R.id.owner_pincode);
        owner_name = findViewById(R.id.owner_name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        drug_license = findViewById(R.id.drug_license);
        gst_license = findViewById(R.id.gst_license);

        getArea();

        terms_of_service = findViewById(R.id.termsOfService);
        area_spinner = findViewById(R.id.area_spinner);
        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shop_name.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Name", Snackbar.LENGTH_SHORT).show();
                } else if(shop_address.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Adress", Snackbar.LENGTH_SHORT).show();
                } else if(phone_number.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                } else if(drug_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Drug License", Snackbar.LENGTH_SHORT).show();
                } else if(gst_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter GST License", Snackbar.LENGTH_SHORT).show();
                } else {
                    sendDetailsToServer();
                }
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
                Constants.CREATE_ACCOUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Register", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if(message.equals("user created !")) {
                                String code = jsonObject.getString("code");
                                AlertDialog alertDialog;
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Register.this);

                                builder.setTitle("Thank You For Registering!");
                                builder.setIcon(R.mipmap.ic_launcher_new);

                                builder.setMessage("Pharma Code :" + code);

                                android.app.AlertDialog alert = builder.create();
                                alert.show();
                            }
                            Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(relativeLayout, "Something Went Wrong !"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(relativeLayout, "Something Went Wrong !", Snackbar.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){

                String area_id = areas.get(area_spinner.getSelectedItemPosition()).getId();

                Map<String, String> params = new HashMap<>();
                params.put("pharma_name",shop_name.getText().toString());
                params.put("area_id", area_id);
                params.put("pharma_address", shop_address.getText().toString());
                params.put("gst", gst_license.getText().toString());
                params.put("drug", drug_license.getText().toString());
                params.put("email", email.getText().toString());
                params.put("phone", phone_number.getText().toString());
                params.put("name", owner_name.getText().toString());
                params.put("pincode", pin_code.getText().toString());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void getArea() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.AREA_FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject areaOnject = new JSONObject(response);
                            JSONArray areasArray = areaOnject.getJSONArray("areas");
                            for(int i=0;i<areasArray.length();i++) {
                                JSONObject jsonObject = areasArray.getJSONObject(i);
                                areas.add(new Area(jsonObject.getString("area_name"),
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("area_state"),
                                        jsonObject.getString("area_city")));
                            }
                            ArrayAdapter<Area> areaArrayAdapter = new ArrayAdapter<>(Register.this,
                                    R.layout.support_simple_spinner_dropdown_item,
                                    areas);
                            areaArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                            area_spinner.setAdapter(areaArrayAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(relativeLayout, "Something Went Wrong !"+e.getMessage() , Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(relativeLayout, "Something Went Wrong !", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);

    }

}
