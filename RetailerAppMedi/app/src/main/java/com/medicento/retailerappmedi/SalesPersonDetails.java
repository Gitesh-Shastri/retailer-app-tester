package com.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;


public class SalesPersonDetails extends AppCompatActivity {

    SalesPerson salesPerson;

    TextView name, total_sales, no_of_orders;

    EditText email, contact, owner_name, address;
    Button edit_profile;
    private String TAG = "SalesPersonAct";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_person_details);

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if(cache != null && !cache.isEmpty()) {
            salesPerson = gson.fromJson(cache, SalesPerson.class);
        }

        name = findViewById(R.id.name);

        total_sales = findViewById(R.id.total_sales);

        no_of_orders = findViewById(R.id.orders);

        address = findViewById(R.id.address);

        email = findViewById(R.id.email);

        owner_name = findViewById(R.id.owner);

        contact = findViewById(R.id.contact);
        edit_profile = findViewById(R.id.edit_profile);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        fetchDetails();

    }

    private void fetchDetails() {

        final ProgressDialog progressDialog = new ProgressDialog(SalesPersonDetails.this);
        progressDialog.setMessage("Loading Details");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(SalesPersonDetails.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/view_profile/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject spo = new JSONObject(response);

                            JSONObject pharmacy = spo.getJSONObject("pharmacy");

                            owner_name.setText(JsonUtils.getJsonValueFromKey(pharmacy, "owner_name"));
                            contact.setText(JsonUtils.getJsonValueFromKey(pharmacy, "mobile_no"));
                            address.setText(JsonUtils.getJsonValueFromKey(pharmacy, "address"));
                            email.setText(JsonUtils.getJsonValueFromKey(pharmacy, "email_id"));

                            total_sales.setText(JsonUtils.getStringValueFromJsonKey(spo, "total_sales"));
                            no_of_orders.setText(JsonUtils.getStringValueFromJsonKey(spo, "order_count"));                            no_of_orders.setText(JsonUtils.getStringValueFromJsonKey(spo, "order_count"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", salesPerson.getId());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(SalesPersonDetails.this);
        progressDialog.setMessage("Updating Details");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(SalesPersonDetails.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/update_profile/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject spo = new JSONObject(response);

                            if(JsonUtils.getJsonValueFromKey(spo, "message").equals("Profile Saved")) {
                                Toast.makeText(SalesPersonDetails.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SalesPersonDetails.this, "Connection Issue. Try Again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SalesPersonDetails.this, "Connection Issue. Try Again Later", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                        progressDialog.dismiss();
                        finish();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", salesPerson.getId());
                params.put("address", address.getText().toString());
                params.put("mobile_no", contact.getText().toString());
                params.put("email", email.getText().toString());
                params.put("owner_name", owner_name.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}

