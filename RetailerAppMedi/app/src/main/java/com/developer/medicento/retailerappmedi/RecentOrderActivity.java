package com.developer.medicento.retailerappmedi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.developer.medicento.retailerappmedi.data.OrderAdapterDelivered;
import com.developer.medicento.retailerappmedi.data.RecentOrderDelivered;
import com.developer.medicento.retailerappmedi.data.RecentOrderMedicine;
import com.developer.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class RecentOrderActivity extends AppCompatActivity implements OrderAdapterDelivered.OnItemClickListener {

    ProgressDialog progressDialog;

    private static ArrayList<RecentOrderDelivered> mRecentOrder;

    SharedPreferences sharedPreferences;

    SalesPerson salesPerson;

    OrderAdapterDelivered orderAdapterC;
    SharedPreferences mSharedPreferences;

    String url = "";

    RecyclerView recyclerView;

    BottomNavigationView bottomNavigationView;

    CanceledFragment canceledFragment;

    FragmentTransaction fragmentTransaction;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_order);

        Paper.init(this);

        final String cache = Paper.book().read("user");

        if(cache != null && !cache.isEmpty()) {
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
        }

        url = "https://retailer-app-api.herokuapp.com/product/recent_order/" + salesPerson.getmAllocatedPharmaId() + "?status=Delivered";

        bottomNavigationView = findViewById(R.id.bottom);

        frameLayout = findViewById(R.id.main_nav);

        canceledFragment = new CanceledFragment();

        mRecentOrder = new ArrayList<>();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()) {

                    case R.id.pending:
                        canceledFragment.addOrders(mRecentOrder);

                        bottomNavigationView.setItemBackgroundResource(R.color.official_color);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_nav, canceledFragment).commit();
                        return  true;

                    case R.id.undelivered:
                        canceledFragment.addOrders(mRecentOrder);

                        bottomNavigationView.setItemBackgroundResource(R.color.colorRedDark);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_nav, canceledFragment).commit();
                        canceledFragment.addOrders(mRecentOrder);
                        return  true;

                    case R.id.completed:
                        canceledFragment.addOrders(mRecentOrder);

                        bottomNavigationView.setItemBackgroundResource(R.color.colorAccent);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_nav, canceledFragment).commit();
                        canceledFragment.addOrders(mRecentOrder);
                        return  true;

                    default:
                            return true;
                }
            }
        });

        new GetOrder().execute();
    }

    @Override
    public void onItemClick1(int position) {

        Intent intent = new Intent(RecentOrderActivity.this, RecentOrderDetails.class);
        intent.putExtra("order", mRecentOrder.get(position));
        startActivity(intent);
    }

    public class GetOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecentOrder = new ArrayList<>();
            progressDialog = new ProgressDialog(RecentOrderActivity.this);
            progressDialog.setMessage("Loading Orders");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("urls", url);

            JsonParser sh = new JsonParser();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e("Gitesh", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObj.getJSONArray("orders");
                    for (int i =0;i<jsonArray.length();i++) {
                        JSONObject order = jsonArray.getJSONObject(i);
                        RecentOrderDelivered recentOrderDelivered = new RecentOrderDelivered(order.getString("order_id"),
                                order.getString("created_at"),
                                Integer.valueOf(order.getString("grand_total")));
                        recentOrderDelivered.setMedicines(new ArrayList<RecentOrderMedicine>());
                        JSONArray medicine = order.getJSONArray("order_items");
                        for(int j=0;j<medicine.length();j++) {
                            JSONObject ordermedicine = medicine.getJSONObject(j);
                            recentOrderDelivered.getMedicines().add(new RecentOrderMedicine(ordermedicine.getString("medicento_name"),
                                    ordermedicine.getInt("quantity")+"",
                                    ordermedicine.getString("total_amount")));
                        }
                        mRecentOrder.add(recentOrderDelivered);
                    }
                } catch (final JSONException e) {
                    Log.e("Gitesh", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("Gitesh", "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            canceledFragment.addOrders(mRecentOrder);

            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_nav, canceledFragment).commit();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(RecentOrderActivity.this, PlaceOrderActivity.class));
    }
}
