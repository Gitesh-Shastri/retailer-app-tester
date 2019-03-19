package com.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;


public class SalesPersonDetails extends AppCompatActivity {

    SalesPerson salesPerson;

    TextView name, total_sales, no_of_orders;

    EditText email, contact, owner_name, address;

    @Override
    public void onBackPressed()
    {
        finish();
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

        updateDetails();

    }

    private void updateDetails() {

        final ProgressDialog progressDialog = new ProgressDialog(SalesPersonDetails.this);
        progressDialog.setMessage("Loading Details");
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(SalesPersonDetails.this);

        String url = "https://retailer-app-api.herokuapp.com/user/profile?id="+salesPerson.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject spo = new JSONObject(response);
                            owner_name.setText(spo.getString("owner"));
                            contact.setText(spo.getString("phone"));
                            address.setText(spo.getString("address"));
                            email.setText(spo.getString("email"));

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(stringRequest);

    }
}

