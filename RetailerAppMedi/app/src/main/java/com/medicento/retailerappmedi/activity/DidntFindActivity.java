package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.medicento.retailerappmedi.HomeActivity;
import com.medicento.retailerappmedi.OrderConfirmed;
import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.adapter.DidntFindAdapter;
import com.medicento.retailerappmedi.data.DidntFind;
import com.medicento.retailerappmedi.data.OrderedMedicine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DidntFindActivity extends AppCompatActivity {

    TextView mOrderIdTv, mSelectedPharmacyTv, order_items_tv, delivery_date_tv, kindly;
    ArrayList<DidntFind> orderedMedicines;
    RecyclerView mRecyclerView;
    private DidntFindAdapter didntFindAdapter;
    Button mShareBtn;

    String orderShareDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_didnt_find);

        mOrderIdTv = findViewById(R.id.order_id_tv);
        mSelectedPharmacyTv = findViewById(R.id.selected_pharmacy_tv);
        order_items_tv = findViewById(R.id.order_items_tv);
        kindly = findViewById(R.id.kindly);

        kindly.setText(Html.fromHtml("<b>Kindly Note</b>: We will try procuring these items from other distributors &amp; if available, we shall be notifying you within 5 business hours."));

        delivery_date_tv = findViewById(R.id.delivery_date_tv);
        mShareBtn = findViewById(R.id.share_order_btn);

        Intent i = getIntent();

        if(i != null && i.hasExtra("order_id")) {
            mOrderIdTv.setText(i.getStringExtra("order_id"));
            mSelectedPharmacyTv.setText(i.getStringExtra("pharmacy"));

            orderedMedicines = (ArrayList<DidntFind>) getIntent().getSerializableExtra("orderDetails");
            order_items_tv.setText((orderedMedicines != null ? orderedMedicines.size():0)+"");
        }

        didntFindAdapter = new DidntFindAdapter(orderedMedicines, this, true);

        mRecyclerView = findViewById(R.id.order_confirmed_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(didntFindAdapter);
        mRecyclerView.setHasFixedSize(true);

        Date tomorrow;

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        tomorrow = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm a - dd/MMM/yyyy");
        String tomorrowAsString = dateFormat.format(tomorrow);

        delivery_date_tv.setText(tomorrowAsString);

        orderShareDetails = "*"+getResources().getString(R.string.order_mode)+"* \n"+
                "*"+getResources().getString(R.string.order_person_name) + "* : Retailer Partner\n"+
                "*Pharmacy Name*: " + i.getStringExtra("pharmacy") +
                "\n\n*Order ID*: " + mOrderIdTv.getText().toString() +
                "\n*Total Items*: " + "" + order_items_tv.getText().toString() +
                "\n*Expected Delivery*: " + tomorrowAsString + "\n\n" +
                "*Medicento Retailer Order Summary:* \n"+
                "Medicine Name | Qty \n";

        for (DidntFind orderedMedicine: orderedMedicines) {
            orderShareDetails += orderedMedicine.getName() +
                    " | " + orderedMedicine.getQuantity() + "\n";
        }

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SUBJECT, "Medicento Order details");
                intent.putExtra(Intent.EXTRA_TEXT, orderShareDetails);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }


    public void new_order(View view) {
        Intent intent = new Intent(DidntFindActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
