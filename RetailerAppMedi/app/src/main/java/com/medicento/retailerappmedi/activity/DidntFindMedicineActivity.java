package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.gson.Gson;
import com.medicento.retailerappmedi.OrderConfirmed;
import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.DidntFindAdapter;
import com.medicento.retailerappmedi.data.DidntFind;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class DidntFindMedicineActivity extends AppCompatActivity {

    private static final String TAG = "DidntFindAct";
    private ArrayList<DidntFind> didntFinds;
    private RecyclerView didnt_find_rv;
    private DidntFindAdapter didntFindAdapter;

    private EditText medicines_name;

    private Button submit, place_order;
    private SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_didnt_find_medicine);

        Paper.init(this);

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }
        didntFinds = new ArrayList<>();

        submit = findViewById(R.id.submit);
        medicines_name = findViewById(R.id.medicines_name);
        place_order = findViewById(R.id.place_order);

        didnt_find_rv = findViewById(R.id.didnt_find_rv);
        didnt_find_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        didntFindAdapter = new DidntFindAdapter(didntFinds, this);
        didnt_find_rv.setAdapter(didntFindAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(medicines_name.getText().toString().isEmpty()) {
                    Toast.makeText(DidntFindMedicineActivity.this, "Name Cant Be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    DidntFind didntFind = new DidntFind();
                    didntFind.setName(getCapitalizeWord(medicines_name.getText().toString()));
                    didntFind.setQuantity(1);

                    didntFinds.add(didntFind);
                    didntFindAdapter.notifyItemInserted(didntFinds.size() - 1);

                    medicines_name.setText("");
                }
            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(didntFindAdapter.getDidntFinds().size() == 0) {
                    Toast.makeText(DidntFindMedicineActivity.this, "Please Add Some Medicine First", Toast.LENGTH_SHORT).show();
                } else {
                    placeOrder();
                }
            }
        });
    }

    private String getCapitalizeWord(String s) {
        if(s.length() > 1) {
            return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return s;
    }

    private void placeOrder() {

        RequestQueue requestQueue = Volley.newRequestQueue(DidntFindMedicineActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://54.161.199.63:8080/orders/place_orders_didnt_find/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            JSONObject jsonObjec = new JSONObject(response);

                            if(JsonUtils.getJsonValueFromKey(jsonObjec, "message").equals("Order Placed")) {
                                startActivity(new Intent(DidntFindMedicineActivity.this, DidntFindActivity.class)
                                .putExtra("order_id", JsonUtils.getJsonValueFromKey(jsonObjec, "order_id"))
                                .putExtra("pharmacy", sp.getName())
                                .putExtra("orderDetails", didntFindAdapter.getDidntFinds()));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(DidntFindMedicineActivity.this, "Error In The Network.Please Try Again After Some Time", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DidntFindMedicineActivity.this, "Error In The Network.Please Try Again After Some Time ", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                JSONObject jsonObject = new JSONObject();
                JSONArray orderItems = new JSONArray();
                try {
                    for (DidntFind orderedMedicine : didntFindAdapter.getDidntFinds()) {
                        JSONObject object = new JSONObject();
                        object.put("medicine_name", orderedMedicine.getName());
                        object.put("Quantity", orderedMedicine.getQuantity());
                        orderItems.put(object);
                    }

                    jsonObject.put("items", orderItems);
                    params.put("order_items", jsonObject.toString());
                    params.put("pharmacy_id", sp.getmAllocatedPharmaId());
                    params.put("source", "Retailer App");
                    params.put("area_id", "5");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
