package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.UserWebLoginAdapter;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.WebCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class RetailerWebLogOut extends AppCompatActivity {

    SalesPerson sp;
    private ArrayList<WebCodes> webCodes;
    private UserWebLoginAdapter webLoginAdapter;
    RecyclerView web_codes_rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_web_logout);

        Paper.init(this);

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        web_codes_rv = findViewById(R.id.web_codes_rv);

        webCodes = new ArrayList<>();
        webLoginAdapter = new UserWebLoginAdapter(webCodes, this);

        web_codes_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        web_codes_rv.setAdapter(webLoginAdapter);

        getAllUsersLoggedIn();
    }

    private void getAllUsersLoggedIn() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/pharmacy/get_code_details/?pharmacy_id=" + sp.getmAllocatedPharmaId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                            if(message.equals("Logged In")) {

                                JSONArray web_codes = jsonObject.getJSONArray("web_codes");

                                WebCodes webCode;
                                for (int i=0;i<web_codes.length();i++) {
                                    JSONObject jsonObject1 = web_codes.getJSONObject(i);
                                    webCode = new WebCodes()
                                            .setCodes(JsonUtils.getJsonValueFromKey(jsonObject1, "code"))
                                            .setAgent(JsonUtils.getJsonValueFromKey(jsonObject1, "agent"))
                                            .setUser_os(JsonUtils.getJsonValueFromKey(jsonObject1, "user_os"));

                                    webCodes.add(webCode);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        webLoginAdapter.notifyDataSetChanged();
                        if(webCodes.size() == 0) {
                            startRetailerWeb();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.retailer_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_web:
                startRetailerWeb();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startRetailerWeb() {
        Intent intent = new Intent(RetailerWebLogOut.this, RetailerWeb.class);
        startActivity(intent);
    }
}
