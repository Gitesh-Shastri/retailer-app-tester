package com.medicento.retailerappmedi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.ChangeAddressActivity;
import com.medicento.retailerappmedi.activity.PaymentGateWayActivity;
import com.medicento.retailerappmedi.activity.PaymentMethodActivity;
import com.medicento.retailerappmedi.activity.UploadPurchaseActivity;
import com.medicento.retailerappmedi.adapter.ItemCartList;
import com.medicento.retailerappmedi.data.Essential;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class CartPageActivity extends AppCompatActivity implements ItemCartList.OverallCostChangeListener, PaymentResultListener {

    private RecyclerView item_rv;
    private ItemCartList itemCartList;
    private ArrayList<EssentialList> essentialLists;
    ImageView back;
    Button pay_rs, confirm, change;
    SalesPerson sp;
    float price = 0;
    TextView tv_price, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_cart_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        essentialLists = new ArrayList<>();
        for (EssentialList essentialList : (ArrayList<EssentialList>) getIntent().getSerializableExtra("list")) {
            if (essentialList.getQty() > 0) {
                essentialLists.add(essentialList);
            }
        }

        for (EssentialList essentialList : essentialLists) {
            price += essentialList.getCost() * essentialList.getQty();
        }


        back = findViewById(R.id.back);
        pay_rs = findViewById(R.id.pay_rs);
        confirm = findViewById(R.id.confirm);
        item_rv = findViewById(R.id.item_rv);
        tv_price = findViewById(R.id.price);
        address = findViewById(R.id.address);
        change = findViewById(R.id.change);

        tv_price.setText("₹ " + price);
        pay_rs.setText("₹ " + price);

        address.setText(sp.getAddress());

        itemCartList = new ItemCartList(essentialLists, this);
        item_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        item_rv.setAdapter(itemCartList);
        itemCartList.setmOverallCostChangeListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pay_rs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData("");
//                startPayment();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartPageActivity.this, UploadPurchaseActivity.class));
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartPageActivity.this, ChangeAddressActivity.class));
            }
        });
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.mdl);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "mediclick heathcare services private limited");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #" + System.currentTimeMillis());
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", (price * 100) + "");

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("data", "Error in starting Razorpay Checkout", e);
        }
    }

    private void saveData(final String s) {

        final JSONObject data = new JSONObject();
        final JSONArray jsonArray = new JSONArray();

        for (EssentialList essentialList : essentialLists) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", essentialList.getName());
                jsonObject.put("cost", essentialList.getCost());
                jsonObject.put("qty", essentialList.getQty());
                price += essentialList.getCost() + essentialList.getQty();
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final JSONObject items = new JSONObject();
        try {
            items.put("items", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("Order Saved")) {
                                startActivity(new Intent(CartPageActivity.this, PaymentGateWayActivity.class)
                                        .putExtra("id", JsonUtils.getJsonValueFromKey(jsonObject, "id")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_items", items.toString());
                params.put("response", s);
                params.put("id", sp.getmAllocatedPharmaId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onCostChanged() {
        for (EssentialList essentialList : itemCartList.getEssentialLists()) {
            price += essentialList.getCost() * essentialList.getQty();
        }

        tv_price.setText("₹ " + price);
        pay_rs.setText("₹ " + price);

        String json = new Gson().toJson(essentialLists);
        Paper.book().write("essential_saved", json);
    }

    @Override
    public void onPaymentSuccess(String s) {
        saveData(s);
        Log.d("data", "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("data", "onPaymentError: " + s);
    }
}
