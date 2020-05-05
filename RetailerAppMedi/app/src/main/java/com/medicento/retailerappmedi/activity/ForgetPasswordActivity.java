package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.AccountCredentialdsActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.SignUpActivity;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.Utils.MedicentoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email_et;
    Button sign_in_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        email_et = findViewById(R.id.email_et);
        sign_in_btn = findViewById(R.id.sign_in_btn);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MedicentoUtils.amIConnect(ForgetPasswordActivity.this)) {
                    startActivity(new Intent(ForgetPasswordActivity.this, NoInternetActivity.class));
                }
                if (email_et.getText().toString().isEmpty()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please Provide Email / Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(ForgetPasswordActivity.this);

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        "http://stage.medicento.com:8080/pharmacy/forget_email/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                                    if (message.equals("Pharmacy Found")) {

                                        JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");

                                        String code_value = JsonUtils.getJsonValueFromKey(pharmacy, "pharma_code");
                                        startActivity(new Intent(ForgetPasswordActivity.this, AccountCredentialdsActivity.class)
                                                .putExtra("number", email_et.getText().toString())
                                                .putExtra("pharmacode", code_value));

                                    } else {
                                        Toast.makeText(ForgetPasswordActivity.this, "Sorry, you have entered a wrong Email Id/Mobile Number. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ForgetPasswordActivity.this, "Sorry, you have entered a wrong Email Id/Mobile Number. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    if (error == null || error.networkResponse == null) {
                                        Toast.makeText(ForgetPasswordActivity.this, "No Response From Server", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String body;
                                    try {
                                        body = new String(error.networkResponse.data, "UTF-8");
                                        JSONObject jsonObject = new JSONObject(body);
                                        if (jsonObject.has("message")) {
                                            Toast.makeText(ForgetPasswordActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        Toast.makeText(ForgetPasswordActivity.this, "No Response From Server", Toast.LENGTH_SHORT).show();
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
                        params.put("email", email_et.getText().toString());
                        return params;
                    }

                };

                requestQueue.add(stringRequest);
            }
        });
    }

    public void signin(View view) {
        startActivity(new Intent(ForgetPasswordActivity.this, SignUpActivity.class));
    }
}
