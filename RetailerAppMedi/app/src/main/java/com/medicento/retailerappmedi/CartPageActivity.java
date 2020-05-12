package com.medicento.retailerappmedi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.ChangeAddressActivity;
import com.medicento.retailerappmedi.activity.PaymentGateWayActivity;
import com.medicento.retailerappmedi.activity.UploadPurchaseActivity;
import com.medicento.retailerappmedi.adapter.ItemCartList;
import com.medicento.retailerappmedi.create_account.RegisterMedicentoActivity;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class CartPageActivity extends AppCompatActivity implements ItemCartList.OverallCostChangeListener, PaymentResultListener {

    private RecyclerView item_rv;
    private ItemCartList itemCartList;
    private ArrayList<EssentialList> essentialLists;
    ImageView back;
    Button pay_rs, confirm, change, apply, remove_voucher;
    SalesPerson sp;
    float price = 0;
    float gst = 0;
    TextView tv_price, address, voucher_discount, total_amount_tv, price_details, gst_tv, prices;
    RelativeLayout voucher_rl, confirm_ll, rootView;
    LinearLayout address_ll;
    EditText voucher_code;
    boolean isApplied;
    float discount_amount = 0;
    int count = 0;
    CardView card;

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
                count += 1;
                essentialLists.add(essentialList);
            }
        }

        for (EssentialList essentialList : essentialLists) {
            price += essentialList.getTotalCost();
            gst += essentialList.getTotalCostWithDiscount();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            CartPageActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1001);
        }

        card = findViewById(R.id.card);
        back = findViewById(R.id.back);
        pay_rs = findViewById(R.id.pay_rs);
        confirm = findViewById(R.id.confirm);
        item_rv = findViewById(R.id.item_rv);
        tv_price = findViewById(R.id.price);
        address = findViewById(R.id.address);
        gst_tv = findViewById(R.id.gst);
        prices = findViewById(R.id.prices);
        change = findViewById(R.id.change);
        confirm_ll = findViewById(R.id.confirm_ll);
        address_ll = findViewById(R.id.address_ll);
        rootView = findViewById(R.id.root_view);
        apply = findViewById(R.id.apply);
        remove_voucher = findViewById(R.id.remove_voucher);
        price_details = findViewById(R.id.price_details);
        voucher_rl = findViewById(R.id.voucher_rl);
        voucher_code = findViewById(R.id.voucher_code);
        total_amount_tv = findViewById(R.id.total_amount_tv);
        voucher_discount = findViewById(R.id.voucher_discount);

        total_amount_tv.setText(String.format("₹ %.2f", (price + gst)));
        tv_price.setText(String.format("₹ %.2f", price));
        gst_tv.setText(String.format("₹ %.2f", gst));
        pay_rs.setText(String.format("Pay ₹ %.2f", (price + gst)));

        price_details.setText("Price Details: ("+ count+" Items)");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            prices.setText(Html.fromHtml("Prices are negotiable basis the Order Quantity. Please click on <strong>Call to Order</strong> for clarification.", Html.FROM_HTML_MODE_LEGACY));
        } else {
            prices.setText(Html.fromHtml("Prices are negotiable basis the Order Quantity. Please click on <strong>Call to Order</strong> for clarification."));
        }
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
                if (sp.getType().equals("Pharmacy")) {
                    startPayment();
                } else {
                    startActivity(new Intent(CartPageActivity.this, UploadPurchaseActivity.class));
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent share_intent = new Intent();
                    String url = "https://api.whatsapp.com/send?phone=+919731785240&text=" + URLEncoder.encode("Regarding Essential Order", "UTF-8");
                    share_intent.setPackage("com.whatsapp");
                    share_intent.setData(Uri.parse(url));
                    startActivity(share_intent);
                } catch (Exception e) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+919731785240"));
                    startActivity(callIntent);
                }
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartPageActivity.this, ChangeAddressActivity.class));
            }
        });

        voucher_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                card.setVisibility(View.GONE);
                address_ll.setVisibility(View.GONE);
                pay_rs.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                remove_voucher.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                card.setVisibility(View.GONE);
                address_ll.setVisibility(View.GONE);
                pay_rs.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                remove_voucher.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (voucher_code.getText().toString().trim().length() == 0) {
                    apply.setEnabled(false);
                    apply.setAlpha(0.5f);
                    card.setVisibility(View.VISIBLE);
                    address_ll.setVisibility(View.VISIBLE);
                    pay_rs.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.VISIBLE);
                } else {
                    if (!isApplied) {
                        apply.setEnabled(true);
                        apply.setAlpha(1f);
                    }
                }
            }
        });

        remove_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove_voucher.setVisibility(View.GONE);
                voucher_code.setText("");
                if (price > 0) {
                    price += 10;
                }
                isApplied = false;
                discount_amount = 0;
                pay_rs.setText(String.format("Pay ₹ %.2f", (price + gst)));
                total_amount_tv.setText(String.format("₹ %.2f",  + (price + gst)));
                voucher_discount.setText("₹ 0");
                voucher_rl.setVisibility(View.GONE);
                voucher_code.setEnabled(true);
                apply.setText("APPLY");
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setVisibility(View.VISIBLE);
                address_ll.setVisibility(View.VISIBLE);
                pay_rs.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                String code = voucher_code.getText().toString().trim().toLowerCase();
                checkPromo(code);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
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
            options.put("amount", ((price + gst) * 100) + "");

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("data", "Error in starting Razorpay Checkout", e);
        }
    }

    private void checkPromo(String code_name) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_voucher_code/?code_name=" + code_name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (JsonUtils.getJsonValueFromKey(jsonObject, "message").equals("found")) {
                                remove_voucher.setVisibility(View.VISIBLE);
                                isApplied = true;
                                voucher_code.setEnabled(false);
                                voucher_rl.setVisibility(View.VISIBLE);
                                apply.setText("APPLIED");
                                apply.setAlpha(0.5f);
                                price -= JsonUtils.getDoubleValue(jsonObject, "amount");
                                if (price < 0) {
                                    price = 0;
                                }
                                discount_amount = -JsonUtils.getDoubleValue(jsonObject, "amount");
                                pay_rs.setText(String.format("Pay ₹ %.2f", (price + gst)));
                                total_amount_tv.setText(String.format("₹ %.2f",  + (price + gst)));
                                voucher_discount.setText("-₹ " + JsonUtils.getDoubleValue(jsonObject, "amount"));
                            } else {
                                Toast.makeText(CartPageActivity.this, "Invalid Voucher Code", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CartPageActivity.this, "Invalid Voucher Code", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartPageActivity.this, "Please Try Again Later!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused) {
            Paper.init(this);
            Gson gson = new Gson();
            String cache = Paper.book().read("user");
            if (cache != null && !cache.isEmpty()) {
                sp = gson.fromJson(cache, SalesPerson.class);
                address.setText(sp.getAddress());
            }
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
        price = 0;
        count = 0;
        gst = 0;

        for (EssentialList essentialList : itemCartList.getEssentialLists()) {
            if (essentialList.getQty() > 0) {
                count += 1;
                price += essentialList.getTotalCost();
                gst += essentialList.getTotalCostWithDiscount();
            }
            if (essentialLists != null && essentialLists.size() > 0) {
                for (int i=0;i<essentialLists.size();i++) {
                    if (essentialLists.get(i).getId().equals(essentialList.getId())) {
                        essentialLists.get(i).setQty(essentialList.getQty());
                        break;
                    }
                }
            }
        }

        price -= discount_amount;
        tv_price.setText(String.format("₹ %.2f",  (price + gst)));
            pay_rs.setText(String.format("Pay ₹ %.2f",  (price + gst)));
        total_amount_tv.setText(String.format("₹ %.2f", (price + gst)));
        price_details.setText("Price Details: ("+ count+" Items)");

        String json = new Gson().toJson(essentialLists);
        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        saveData(s);
        Log.d("data", "onPaymentSuccess: " + s);
    }

    private boolean isPaused = false;

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;

        if (essentialLists != null && essentialLists.size() > 0) {
            for (int i=0;i<essentialLists.size();i++) {
                for (EssentialList essential: itemCartList.getEssentialLists()) {
                    if (essentialLists.get(i).getId().equals(essential.getId())) {
                        essentialLists.get(i).setQty(essential.getQty());
                        break;
                    }
                }
            }
        }

        String json = new Gson().toJson(essentialLists);
        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(sp.getUsercode(), json);
                Paper.book().write("essential_saved_json", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("data", "onPaymentError: " + s);
    }

    @Override
    public void onBackPressed() {
        if (card.getVisibility() == View.GONE) {
            card.setVisibility(View.VISIBLE);
            address_ll.setVisibility(View.VISIBLE);
            pay_rs.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
