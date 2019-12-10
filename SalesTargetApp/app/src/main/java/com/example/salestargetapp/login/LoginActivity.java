package com.example.salestargetapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.salestargetapp.R;
import com.example.salestargetapp.dashboard.DashboardActivity;
import com.example.salestargetapp.data.Constants;
import com.example.salestargetapp.data.SalesPerson;
import com.example.salestargetapp.pharmacy_selection.PharmacySelectionActivity;
import com.example.salestargetapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.example.salestargetapp.utils.CommonUtils.showVolleyError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SalesPerson salesPerson;

    private EditText email_edit_tv, password_edit_tv;

    private Button sign_in_btn;

    private ProgressBar sign_in_progress;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }

        initView();
    }

    private void initView() {
        email_edit_tv = findViewById(R.id.email_edit_tv);
        password_edit_tv = findViewById(R.id.password_edit_tv);

        sign_in_btn = findViewById(R.id.sign_in_btn);

        sign_in_progress = findViewById(R.id.sign_in_progress);

        requestQueue = Volley.newRequestQueue(this);

        sign_in_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.sign_in_btn:
                if (checkForEmptyText(email_edit_tv)) {
                    showToast("Please Enter Sales Username/Id");
                } else if (checkForEmptyText(password_edit_tv)) {
                    showToast("Please Enter Password");
                } else if (!CommonUtils.amIConnect(LoginActivity.this)) {
                    showToast("Please Check Your Internet Connection.");
                } else {
                    login();
                }
                break;
        }
    }

    private void login() {

        String url = Constants.BASE_URL + "sales_person_login/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                        sign_in_progress.setVisibility(View.GONE);
                        try {
                            JSONObject baseObject = new JSONObject(response);
                            if(baseObject.has("message") && baseObject.getString("message").equals("User Logged In")) {
                                JSONObject user = baseObject.getJSONObject("salesPerson");
                                salesPerson = new SalesPerson(user.getString("name"),
                                        user.getLong("Total_sales"),
                                        user.getInt("No_of_order"),
                                        user.getInt("Returns"),
                                        user.getLong("Earnings"),
                                        user.getInt("id")+"",
                                        user.getInt("area")+"",
                                        "123");
                                startActivity(new Intent(LoginActivity.this, PharmacySelectionActivity.class));
                            } else {
                                showToast("Invalid Details!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast( "JSON Parsing Error ");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                        sign_in_progress.setVisibility(View.GONE);
                        showToast("Something Went Wrong  Try Again .. ");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", email_edit_tv.getText().toString());
                params.put("password", password_edit_tv.getText().toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean checkForEmptyText(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
