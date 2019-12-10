package com.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignUpPharmacyActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout sign_up_layout;

    Button create_account, terms_of_service;

    TextView login_here;

    ProgressDialog progressDialog;

    EditText shop_name, shop_address, pin_code, owner_name, phone_number, email, drug_license, gst_license;

    ArrayList<State> states;

    Spinner state_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_pharmacy);

        sign_up_layout = findViewById(R.id.sign_up_layout);

        create_account = findViewById(R.id.create_account);
        terms_of_service = findViewById(R.id.termsOfService);

        login_here = findViewById(R.id.login_here);

        shop_name = findViewById(R.id.shop_name);
        shop_address = findViewById(R.id.shop_address);
        pin_code = findViewById(R.id.owner_pincode);
        owner_name = findViewById(R.id.owner_name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        drug_license = findViewById(R.id.drug_license);
        gst_license = findViewById(R.id.gst_license);

        state_sp = findViewById(R.id.state_spinner);

        create_account.setOnClickListener(this);
        terms_of_service.setOnClickListener(this);
        login_here.setOnClickListener(this);

        getState();
    }

    private void getState() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading areas");
        progressDialog.show();

        states = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.STATE_FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("state")) {
                                JSONArray stateArray = jsonObject.getJSONArray("state");
                                for(int i=0;i<stateArray.length();i++) {
                                    JSONObject stateObj = stateArray.getJSONObject(i);
                                    if(stateObj.has("name")
                                        && stateObj.has("_id")) {
                                        states.add(new State(stateObj.getString("name"),
                                                stateObj.getString("_id")));
                                    }
                                }
                            }
                            ArrayAdapter<State> adapter = new ArrayAdapter<>(SignUpPharmacyActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    states);
                            state_sp.setAdapter(adapter);
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            Snackbar.make(sign_up_layout, "Something Went Wrong !" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(sign_up_layout, "Something Went Wrong !" + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.create_account:
                if (shop_name.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter Shop Name", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (shop_address.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter Shop Adress", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (phone_number.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (email.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter Email Address", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (drug_license.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter Drug License", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (gst_license.getText().toString().isEmpty()) {
                    Snackbar.make(sign_up_layout, "Please Enter GST License", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    sendDetailsToServer();
                }
                break;

            case R.id.termsOfService:
                startActivity(new Intent(SignUpPharmacyActivity.this, TermsAndCondition.class));
                break;

            case R.id.login_here:
                startActivity(new Intent(SignUpPharmacyActivity.this, SignUpActivity.class));
                finish();
                break;

        }

    }

    private void sendDetailsToServer() {



    }
}
