package com.medicento.retailerappmedi.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.adapter.PaymentCartAdapter;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.paperdb.Paper;

public class PaymentGateWayActivity extends AppCompatActivity {

    TextView id, cart, cost;
    ArrayList<EssentialList> essentialLists;
    RecyclerView cart_rv;
    PaymentCartAdapter paymentCartAdapter;
    SalesPerson sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        setContentView(R.layout.activity_payment_gate_way);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        id = findViewById(R.id.id);
        cart = findViewById(R.id.cart);
        cost = findViewById(R.id.cost);
        cart_rv = findViewById(R.id.cart_rv);

        if (getIntent() != null && getIntent().hasExtra("id")) {
            id.append(" "+getIntent().getStringExtra("id"));
        }

        essentialLists = new ArrayList<>();

        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                String data = jsonObject.getString(sp.getUsercode());
                Type type = new TypeToken<ArrayList<EssentialList>>() {
                }.getType();
                essentialLists = new Gson().fromJson(data, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int count_num = 0;
        float price = 0;
        float gst = 0;
        for (EssentialList essentialList: essentialLists) {
            if (essentialList.getQty() > 0) {
                count_num += 1;
                price += essentialList.getQty()*essentialList.getCost();
                gst += (essentialList.getCost() * essentialList.getQty()*essentialList.getDiscount()*0.01);
            }
        }

        cart.setText(Html.fromHtml("Cart Sub Total (" + count_num + " Items ) : <b>" + String.format("INR %.2f</b>", (price+gst))));
        cost.setText("₹ "+price);

        paymentCartAdapter = new PaymentCartAdapter(essentialLists, this);
        cart_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cart_rv.setAdapter(paymentCartAdapter);
    }
}
