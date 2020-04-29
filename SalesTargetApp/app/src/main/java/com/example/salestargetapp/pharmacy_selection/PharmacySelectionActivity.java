package com.example.salestargetapp.pharmacy_selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.salestargetapp.R;
import com.example.salestargetapp.adapter.PharmacySelectionAdapter;
import com.example.salestargetapp.data.Area;
import com.example.salestargetapp.data.Constants;
import com.example.salestargetapp.data.Pharmacy;
import com.example.salestargetapp.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.paperdb.Paper;

public class PharmacySelectionActivity extends AppCompatActivity {

    private static final String TAG = "PharmacySelection";
    private ArrayList<Area> areas;
    private ArrayList<Pharmacy> pharmacies;
    private Spinner area_spinner;
    private RecyclerView pharmacy_rv;
    private PharmacySelectionAdapter pharmacySelectionAdapter;
    private boolean isLoading = false;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_selection);

        Paper.init(this);

        area_spinner = findViewById(R.id.area_spinner);
        pharmacy_rv = findViewById(R.id.pharmacy_rv);
        progress = findViewById(R.id.progress);

        String areas_saved = Paper.book().read("areas_saved");
        if (areas_saved != null && !areas_saved.isEmpty()) {
            Type type = new TypeToken<ArrayList<Area>>() {
            }.getType();
            areas = new Gson().fromJson(areas_saved, type);
        }

        if (getIntent() != null && getIntent().hasExtra("areas")) {
            areas = (ArrayList<Area>) getIntent().getSerializableExtra("areas");
            String json = new Gson().toJson(areas);
            Paper.book().write("areas_saved", json);
        }

        ArrayAdapter areaAdapter = new ArrayAdapter(this, R.layout.spinner, areas);
        area_spinner.setAdapter(areaAdapter);

        area_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fetchPharmacy(areas.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fetchPharmacy(String id) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.BASE_URL + "pharmacy/pharmacy_by_area/?area=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray pharmacy = jsonObject.getJSONArray("pharmacy");

                            for (int i=0;i<pharmacy.length();i++) {
                                JSONObject pharma = pharmacy.getJSONObject(i);

                                pharmacies.add(new Pharmacy(
                                        pharma.getString("name"),
                                        pharma.getInt("id")+"",
                                        pharma.getString("pharma_code"),
                                        pharma.getString("mobile_no")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pharmacySelectionAdapter = new PharmacySelectionAdapter(pharmacies, PharmacySelectionActivity.this);
                        pharmacy_rv.setLayoutManager(new LinearLayoutManager(PharmacySelectionActivity.this));
                        pharmacy_rv.setHasFixedSize(true);
                        pharmacy_rv.setAdapter(pharmacySelectionAdapter);
                        isLoading = false;
                        progress.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        progress.setVisibility(View.GONE);
                        CommonUtils.showVolleyError(error);
                    }
                }
        );

        if (!isLoading) {
            progress.setVisibility(View.VISIBLE);
            isLoading = true;
            pharmacies = new ArrayList<>();
            requestQueue.add(stringRequest);
        }
    }
}
