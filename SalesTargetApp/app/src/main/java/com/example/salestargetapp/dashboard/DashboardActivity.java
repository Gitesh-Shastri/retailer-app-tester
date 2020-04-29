package com.example.salestargetapp.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.salestargetapp.R;
import com.example.salestargetapp.adapter.SalesAndPharmacyAdapter;
import com.example.salestargetapp.data.Constants;
import com.example.salestargetapp.data.PharmacyWithSales;
import com.example.salestargetapp.data.SalesPerson;
import com.example.salestargetapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import io.paperdb.Paper;

public class DashboardActivity extends AppCompatActivity {

    SalesPerson salesPerson;

    RecyclerView pharmacy_sales_rv;
    ArrayList<PharmacyWithSales> pharmacyWithSales;

    SalesAndPharmacyAdapter salesAndPharmacyAdapter;

    TextView total, target, days_left;
    private float sales = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Paper.init(this);

        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
        }

        pharmacy_sales_rv = findViewById(R.id.pharmacy_sales_rv);
        days_left = findViewById(R.id.days_left);
        total = findViewById(R.id.total);
        target = findViewById(R.id.target);

        pharmacy_sales_rv.setLayoutManager(new LinearLayoutManager(this));
        pharmacyWithSales = new ArrayList<>();
        salesAndPharmacyAdapter = new SalesAndPharmacyAdapter(pharmacyWithSales, this);
        pharmacy_sales_rv.setAdapter(salesAndPharmacyAdapter);

        Calendar calendar = Calendar.getInstance();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        days_left.setText("Days left - " + (lastDay - currentDay));

        getPharmacyData();
    }

    private void getPharmacyData() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.BASE_URL + "pharmacy/fetch_salesperson_order/?sales_code=" + salesPerson.getUsercode(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray pharmacy = new JSONArray(jsonObject.getString("pharmacy"));

                            for (int i=0;i<pharmacy.length();i++) {
                                JSONObject jsonObject1 = pharmacy.getJSONObject(i);
                                Iterator<String> keysItr = jsonObject1.keys();
                                String name = keysItr.next();
                                pharmacyWithSales.add(new PharmacyWithSales(name, jsonObject1.getString(name)));

                                sales += Float.parseFloat(jsonObject1.getString(name));
                            }

                            target.setText(String.format("Target Rs.%,d", jsonObject.getInt("target")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        salesAndPharmacyAdapter.notifyDataSetChanged();
                        total.setText(String.format("Total = Rs.%,.2f", sales));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtils.showVolleyError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }
}
