package com.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.data.Area;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class ProfileNew extends AppCompatActivity {

    TextInputLayout address, email, number, username;

    SalesPerson sp;

    ProgressDialog progressDialog;

    ArrayAdapter<String> stateadapter, cityeadapter;

    String state_name, city_name, area_name;

    TextView total_sales, no_of_orders;

    ArrayList<Area> areas, tempareas;
    ArrayList<String> states, cities;

    EditText drug, gst;
    TextView state, city, pincode;

    String area_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);
        username = findViewById(R.id.username);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        pincode = findViewById(R.id.pincode);

        drug = findViewById(R.id.drug);
        gst = findViewById(R.id.gst);


        total_sales = findViewById(R.id.total_sales);
        no_of_orders = findViewById(R.id.orders);

        Paper.init(this);
        Gson gson = new Gson();


        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.show();
        progressDialog.setCancelable(true);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/view_profile/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject spo = new JSONObject(response);

                            JSONObject pharmacy = spo.getJSONObject("pharmacy");

                            username.getEditText().setText(JsonUtils.getJsonValueFromKey(pharmacy, "owner_name"));
                            number.getEditText().setText(JsonUtils.getJsonValueFromKey(pharmacy, "mobile_no"));
                            address.getEditText().setText(JsonUtils.getJsonValueFromKey(pharmacy, "address"));
                            email.getEditText().setText(JsonUtils.getJsonValueFromKey(pharmacy, "email_id"));

                            total_sales.setText(JsonUtils.getStringValueFromJsonKey(spo, "total_sales"));
                            no_of_orders.setText(JsonUtils.getStringValueFromJsonKey(spo, "order_count"));                            no_of_orders.setText(JsonUtils.getStringValueFromJsonKey(spo, "order_count"));

                            drug.setText(JsonUtils.getJsonValueFromKey(pharmacy,"Tan"));
                            gst.setText(JsonUtils.getJsonValueFromKey(pharmacy,"gst"));

                            JSONObject state_obj = pharmacy.getJSONObject("state");
                            state.setText(JsonUtils.getJsonValueFromKey(state_obj,"name"));

                            JSONObject city_obj = pharmacy.getJSONObject("city");
                            city.setText(JsonUtils.getJsonValueFromKey(city_obj,"name"));

                            JSONObject area_obj = pharmacy.getJSONObject("area");
                            pincode.setText(JsonUtils.getJsonValueFromKey(area_obj,"pincode"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileNew.this, "Connection Issue. Try Again Later!", Toast.LENGTH_SHORT).show();
                        }
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONObject user = jsonObject.getJSONObject("user");
//                            JSONObject pharma = jsonObject.getJSONObject("Allocated_Pharma");
//
//                            if(user.has("Allocated_Area")) {
//                                JSONObject area1 = jsonObject.getJSONObject("Allocated_Area");
//                                state_name = area1.getString("area_state");
//                                city_name = area1.getString("area_city");
//                                area_name = area1.getString("area_name");
//                                state.setText(state_name);
//                                area.setText(area_name);
//                                city.setText(city_name);
//                            }
//                            if(user.has("first")) {
//                                firstName.getEditText().setText(user.getString("first"));
//                                lastName.getEditText().setText(user.getString("second"));
//                            }
//                            drug.setText(pharma.getString("drug_license"));
//                            gst.setText(pharma.getString("gst_license"));
//                            pan.setText(pharma.getString("pan_card"));
//                            address.getEditText().setText(pharma.getString("pharma_address"));
//                            email.getEditText().setText(user.getString("useremail"));
//                            number.getEditText().setText(pharma.getString("contact"));
//                            username.getEditText().setText(user.getString("username"));
//                            state.setText("Karnataka");
//                            area.setText("Abbur B.O");
//                            city.setText("Ramnagar");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(ProfileNew.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileNew.this, "Connection Issue. Try Again Later!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", sp.getId());
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.save) {
            updateProfile();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileNew.this);
        progressDialog.setMessage("Updating Details");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileNew.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/update_profile/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject spo = new JSONObject(response);

                            if(JsonUtils.getJsonValueFromKey(spo, "message").equals("Profile Saved")) {
                                Toast.makeText(ProfileNew.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileNew.this, "Connection Issue. Try Again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileNew.this, "Connection Issue. Try Again Later", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                        progressDialog.dismiss();
                        finish();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", sp.getId());
                params.put("address", address.getEditText().getText().toString());
                params.put("mobile_no", number.getEditText().getText().toString());
                params.put("email", email.getEditText().getText().toString());
                params.put("owner_name", username.getEditText().getText().toString());
                params.put("gst", gst.getText().toString());
                params.put("Tan", drug.getText().toString());


                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
