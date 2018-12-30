package com.developer.medicento.retailerappmedi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.medicento.retailerappmedi.data.CountDrawable;
import com.developer.medicento.retailerappmedi.data.ListViewAdapter;
import com.developer.medicento.retailerappmedi.data.MenuItems;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.developer.medicento.retailerappmedi.data.AutoCompleteAdapter;
import com.developer.medicento.retailerappmedi.data.Constants;
import com.developer.medicento.retailerappmedi.data.MakeYourOwn;
import com.developer.medicento.retailerappmedi.data.Medicine;
import com.developer.medicento.retailerappmedi.data.MedicineAuto;
import com.developer.medicento.retailerappmedi.data.OrderedMedicine;
import com.developer.medicento.retailerappmedi.data.OrderedMedicineAdapter;
import com.developer.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class PlaceOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OrderedMedicineAdapter.OverallCostChangeListener{

    TextView pharmacyName;
    private Timer timer;

    NavigationView mNavigationView;
    public static ArrayList<MakeYourOwn> makeYourOwns;
    RecyclerView mOrderedMedicinesListView;
    public static String strcode;
    ProgressBar progressBar;
    SharedPreferences mSharedPreferences;
    String usercode;
    SalesPerson sp;
    AutoCompleteTextView mMedicineList;
    Toolbar mToolbar;
    Button filter,notify;

    ListView listView;

    MaterialCardView materialCardView;

    TextView cart_sub;

    public static OrderedMedicineAdapter mOrderedMedicineAdapter;
    public static String url = "https://retailer-app-api.herokuapp.com/product/medimap";
    public static String url1 = "https://retailer-app-api.herokuapp.com/product/updateApp";
    String versionUpdate;
    private ProgressDialog pDialog;
    LinearLayout mCostLayout;
    TextView mTotalTv;
    int Count;
    public String name,type,content;
    ArrayList<String> medicine1;
    public static ArrayList<Medicine> MedicineDataList;

    Boolean isLoading;

    Menu menu;

    Animation mAnimation;
    int TCost;
    private final int uniqueId = 12345;
    NotificationCompat.Builder notification;
    public static int code;
    ArrayAdapter<String> mMedicineAdapter;
    AlertDialog alert;

    ArrayList<MedicineAuto> medicineAuto;

    AutoCompleteAdapter medicineAdapter;

    ArrayList<MenuItems> menuItems;

    int count;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressBar = findViewById(R.id.progressBar);

        isLoading = false;

        materialCardView = findViewById(R.id.card);

        cart_sub = findViewById(R.id.tv2);

        notify = findViewById(R.id.notify);
        mMedicineList = (AutoCompleteTextView) findViewById(R.id.medicine_edit_tv);
        pharmacyName = findViewById(R.id.pharmacy_edit_tv);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToolbar = findViewById(R.id.toolbar);
        mCostLayout = findViewById(R.id.cost_layout);
        filter = findViewById(R.id.filter);
        mTotalTv = findViewById(R.id.total_cost);
        name = type = content = " ";
        makeYourOwns = new ArrayList<>();
        mOrderedMedicinesListView = findViewById(R.id.ordered_medicines_list);
        mOrderedMedicinesListView.setLayoutManager(new LinearLayoutManager(this));
        mOrderedMedicinesListView.setHasFixedSize(true);
        setSupportActionBar(mToolbar);
        Count =0;

        Paper.init(this);

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if(cache != null && !cache.isEmpty()) {

            sp = gson.fromJson(cache, SalesPerson.class);
        }

        listView = findViewById(R.id.list_menu_items);

        menuItems = new ArrayList<>();

        menuItems.add(new MenuItems("Profile", "1", getResources().getDrawable(R.drawable.ic_man)));
        menuItems.add(new MenuItems("Recent Order", "2", getResources().getDrawable(R.drawable.ic_trolley)));
        menuItems.add(new MenuItems("Sign Out", "1", getResources().getDrawable(R.drawable.ic_logout)));

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, menuItems);

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(PlaceOrderActivity.this, ProfileNew.class);
                        intent.putExtra("usercode", usercode);
                        intent.putExtra("SalesPerson", sp);
                        startActivity(intent);
                        break;
                    case 1:
                        if(!IamConnect()) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                            builder.setTitle("No Internet Connection");
                            builder.setIcon(R.mipmap.ic_launcher_new);
                            builder.setCancelable(false);
                            builder.setMessage("Please Connect To The Internet")
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alert.dismiss();
                                        }
                                    });
                            alert = builder.create();
                            alert.show();
                        } else {
                            intent = new Intent(PlaceOrderActivity.this, RecentOrderActivity.class);
                            intent.putExtra("SalesPerson", sp);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        clearUserDetails();
                        intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
                        startActivityForResult(intent, Constants.RC_SIGN_IN);
                        break;
                }
            }
        });

        String json = mSharedPreferences.getString("saved", null);
        Type type = new TypeToken<ArrayList<Medicine>>() {}.getType();
        MedicineDataList = gson.fromJson(json, type);
        if(MedicineDataList == null ) {
            MedicineDataList = new ArrayList<>();
            new GetNames().execute();
        } else {
            medicine1 = new ArrayList<>();
            medicineAuto = new ArrayList<>();
            for(Medicine med: MedicineDataList){
                medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice()));
                medicine1.add(med.getMedicentoName());
            }

            medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
            mMedicineList.setAdapter(medicineAdapter);
            mMedicineList.setEnabled(true);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        TCost = 0;

        if(sp != null) {
            addSalesPersonDetailsToNavDrawer();
        } else {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent, Constants.RC_SIGN_IN);
        }

        mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());
        mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
        mOrderedMedicineAdapter.setOverallCostChangeListener(this);
        mOrderedMedicineAdapter.setContext(this);
        notify.setVisibility(View.GONE);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlaceOrderActivity.this,"Notify", Toast.LENGTH_SHORT).show();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrderActivity.this, Filter.class);
                startActivityForResult(intent, 1);
            }
        });
        mMedicineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getWindow().getCurrentFocus();
                InputMethodManager ime = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                mMedicineList.setText("");
                Medicine medicine = null;
                for(Medicine med: MedicineDataList) {
                    if(med.getMedicentoName().equals(medicineAdapter.getItem(position).getName())) {
                        medicine = med;
                        break;
                    }
                }
                mOrderedMedicineAdapter.add(new OrderedMedicine(medicine.getMedicentoName(),
                        medicine.getCompanyName(),
                        1,
                        medicine.getPrice(),
                        medicine.getCode(),
                        medicine.getPrice(),
                        medicine.getMstock(),
                        medicine.getPacking()));
                float cost = Float.parseFloat(mTotalTv.getText().toString().substring(1));
                float overall = cost+medicine.getPrice();
                mTotalTv.setText(getString(R.string.ruppe_symbol)+overall);
                count += 1;
                setCount(PlaceOrderActivity.this, ""+count);
                mOrderedMedicinesListView.smoothScrollToPosition(0);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = (int) viewHolder.itemView.getTag();
                mOrderedMedicineAdapter.remove(pos);
            }
        }).attachToRecyclerView(mOrderedMedicinesListView);
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(PlaceOrderActivity.this, "Welcome ", Toast.LENGTH_SHORT).show();
                SalesPerson sp = (SalesPerson)data.getSerializableExtra("salesperosn");
                mNavigationView = findViewById(R.id.nav_view);
                addSalesPersonDetailsToNavDrawer();
            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                if(result.equals("company")) {
                    ArrayList<Medicine> manu = (ArrayList<Medicine>) data.getSerializableExtra("list");
                    ArrayList<String> medicine2 = new ArrayList<>();
                    for(Medicine manu1: manu){
                        medicine2.add(manu1.getMedicentoName());
                    }
                    mMedicineAdapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.support_simple_spinner_dropdown_item,medicine2);
                    mMedicineList.setAdapter(mMedicineAdapter);
                }else if(result.equals("offer")) {
                    ArrayList<Medicine> manu = (ArrayList<Medicine>) data.getSerializableExtra("list");
                    ArrayList<String> medicine2 = new ArrayList<>();
                    for(Medicine manu1: manu){
                        medicine2.add(manu1.getMedicentoName());
                    }
                    mMedicineAdapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.support_simple_spinner_dropdown_item,medicine2);
                    mMedicineList.setAdapter(mMedicineAdapter);
                }
                Toast.makeText(PlaceOrderActivity.this, "Result : " + result, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(PlaceOrderActivity.this, "Canceled ", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == 3) {
            if(resultCode == RESULT_OK){

                makeYourOwns = (ArrayList<MakeYourOwn>) data.getSerializableExtra("make");
                mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());
                mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
                mOrderedMedicineAdapter.setOverallCostChangeListener(this);
                mOrderedMedicineAdapter.setContext(this);
                for(OrderedMedicine med: (ArrayList<OrderedMedicine>)data.getSerializableExtra("myList")){
                    mOrderedMedicineAdapter.add(med);
                }
            }
        }
    }

    private void addSalesPersonDetailsToNavDrawer() {

        String cache = Paper.book().read("user");

        if(cache != null && !cache.isEmpty()) {

            sp = new Gson().fromJson(cache, SalesPerson.class);
        }
        mNavigationView = findViewById(R.id.nav_view);
            mNavigationView.setNavigationItemSelectedListener(this);

            LinearLayout linearLayout = findViewById(R.id.header_header);
            View headerView = mNavigationView.getHeaderView(0);
            TextView navHeaderSalesmanName = linearLayout.findViewById(R.id.username_header);
            TextView navHeaderSalesmanEmail = linearLayout.findViewById(R.id.user_email_header);
            TextView navp = linearLayout.findViewById(R.id.user_pharmaid);
            navHeaderSalesmanName.setText(sp.getName());
            navHeaderSalesmanEmail.setText(getString(R.string.pharmacode) + sp.getUsercode());
            navp.setText(sp.getName());
            pharmacyName.setText(sp.getName());
    }

    @Override
    public void onCostChanged(float newCost, int qty, String type) {
        mTotalTv.setText(getString(R.string.ruppe_symbol)+newCost + "");
        if(type.equals("add")) {
            count += qty;
        } else {
            count -= qty;
        }
        setCount(this, ""+count);
    }

    private  class GetNames extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            isLoading = true;

            materialCardView.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            JsonParser sh = new JsonParser();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e("Gitesh", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray medicine = jsonObj.getJSONArray("products");
                    medicine1 = new ArrayList<>();
                    medicineAuto = new ArrayList<>();

                    // looping through All Contacts
                    for (int i = 0; i < medicine.length(); i++) {
                        JSONObject c = medicine.getJSONObject(i);
                        MedicineDataList.add(new Medicine(
                                c.getString("medicento_name"),
                                c.getString("company_name"),
                                c.getInt("price"),
                                c.getString("_id"),
                                c.getInt("stock"),
                                c.getString("item_code")
                        ));
                        MedicineDataList.get(i).setPacking(c.getString("packing"));
                        medicineAuto.add(new MedicineAuto(c.getString("medicento_name"),
                                c.getString("company_name"),
                                c.getInt("price")));
                        medicine1.add(c.getString("medicento_name"));
                    }
                } catch (final JSONException e) {
                    Toast.makeText(PlaceOrderActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Gitesh", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("Gitesh", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            materialCardView.setVisibility(View.INVISIBLE);

            medicineAdapter = new AutoCompleteAdapter(PlaceOrderActivity.this, medicineAuto);
            mMedicineList.setAdapter(medicineAdapter);
            mMedicineList.setEnabled(true);

            Gson gson = new Gson();
            String json = gson.toJson(MedicineDataList);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("saved", json);
            editor.apply();

            Toast.makeText(PlaceOrderActivity.this,"Now You Can Choose Medicine", Toast.LENGTH_SHORT).show();

            addSalesPersonDetailsToNavDrawer();
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        setCount(this, "0");
        return true;
    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = menu.findItem(R.id.action_proceed);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        cart_sub.setText("Cart Sub Total ("+count+" Items ) : ");

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.counter);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.counter, badge);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_proceed) {
            if(!IamConnect()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                builder.setTitle("No Internet Connection");
                builder.setIcon(R.mipmap.ic_launcher_new);
                builder.setCancelable(false);
                builder.setMessage("Please Connect To The Internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.show();
            } else {
                if (mOrderedMedicineAdapter.getItemCount() > 0) {
                        makeYourOwns = new ArrayList<>();
                        String json = extractJsonFromOrderItemsList(mOrderedMedicineAdapter.getList(), makeYourOwns, sp.getmAllocatedPharmaId(),  sp.getId());
                        new PlaceOrder().execute(json);
                    }
                 else {
                    Toast.makeText(PlaceOrderActivity.this, "Please Select Some Medicine", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
            }
            }
          /* else if (id == R.id.offer) {
                if(!amIConnect()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setIcon(R.mipmap.ic_launcher_new);
                    builder.setCancelable(false);
                    builder.setMessage("Please Connect To The Internet")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alert.dismiss();
                                }
                            });
                    alert = builder.create();
                    alert.show();
                } else {
                  Intent intent = new Intent(PlaceOrderActivity.this, Offers_page.class);
                    startActivity(intent);

                }
        } */
        return super.onOptionsItemSelected(item);
    }

        @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.sign_out) {
            clearUserDetails();
            intent = new Intent(this, SignUpActivity.class);
            startActivityForResult(intent, Constants.RC_SIGN_IN);
        }  else if (id == R.id.about_me) {
            intent = new Intent(this, SalesPersonDetails.class);
            intent.putExtra("usercode", usercode);
            intent.putExtra("SalesPerson", sp);
            startActivity(intent);
        } else if(id == R.id.Recentorder) {
            if(!IamConnect()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);
                builder.setTitle("No Internet Connection");
                builder.setIcon(R.mipmap.ic_launcher_new);
                builder.setCancelable(false);
                builder.setMessage("Please Connect To The Internet")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.show();
            } else {
                intent = new Intent(this, RecentOrderActivity.class);
                intent.putExtra("SalesPerson", sp);
                startActivity(intent);
            }
        }
        startActivity(intent);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearUserDetails() {

        Paper.book().delete("user");

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer = new Timer();
        LogOutTimerTask logOutTimerTask = new LogOutTimerTask();
        timer.schedule(logOutTimerTask, 1200000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        if(!IamConnect()) {
            alertDialogForInternet();
        } else {
            final int[] count1 = new int[1];
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://retailer-app-api.herokuapp.com/product/updateApp";
            StringRequest str = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("Code1", response);
                                strcode = response;
                                JSONObject spo = new JSONObject(response);
                                JSONArray version = spo.getJSONArray("Version");
                                for(int i=0;i<version.length();i++){
                                    JSONObject v = version.getJSONObject(i);
                                    versionUpdate = v.getString("version");
                                }
                                String versionName = BuildConfig.VERSION_NAME;
                                if(!versionUpdate.equals(versionName)) {
                                    alertDialogForUpdate();
                                }
                                code = spo.getInt("code");
                                count1[0] = spo.getInt("count");
                                int count = mSharedPreferences.getInt("count", 0);
                                if (code == 101 && count <= spo.getInt("count")) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putInt("count", count1[0] + 1);
                                    editor.apply();
                                    if(!isLoading) {
                                        new GetNames().execute();
                                        Toast.makeText(PlaceOrderActivity.this, "List Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e("error_coce", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(str);
        }

        String cache = Paper.book().read("user");

        if(cache == null || cache.isEmpty()) {
            Intent intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
            startActivityForResult(intent, Constants.RC_SIGN_IN);
        }
    }


    private void alertDialogForUpdate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);

        builder.setTitle("update Available");
        builder.setIcon(R.mipmap.ic_launcher_new);
        builder.setCancelable(false);

        builder.setMessage("New Version Available")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(
                                new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.developer.medicento.retailerappmedi"
                                        )
                                )
                        );
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertDialogForInternet() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);

        builder.setTitle("No Internet Connection");
        builder.setIcon(R.mipmap.ic_launcher_new);
        builder.setCancelable(false);

        builder.setMessage("Please Connect To The Internet")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });

        alert = builder.create();
        alert.show();

    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            clearUserDetails();
            Intent intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
            startActivityForResult(intent, Constants.RC_SIGN_IN);

        }
    }


    public class PlaceOrder extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlaceOrderActivity.this);
            pDialog.setTitle("Placing Order");
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Intent intent = new Intent(PlaceOrderActivity.this, OrderConfirmed.class);
            intent.putExtra("TotalCost", Float.valueOf(mTotalTv.getText().toString().substring(1)));
            intent.putExtra("json", s);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse = null;
            url = Constants.PLACE_ORDER_URL;
            String jsonData = strings[0];
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                URL url1 = new URL(url);
                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(jsonData);
                writer.close();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("Git", "Error response code : " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e("Git", "Error IOException");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return jsonResponse;
        }

    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String extractJsonFromOrderItemsList(ArrayList<OrderedMedicine> data, ArrayList<MakeYourOwn> data1, String pId, String sId) {
        JSONArray orderItems = new JSONArray();
        try {
            for (OrderedMedicine orderedMedicine : data) {
                JSONObject object = new JSONObject();
                object.put("medicento_name", orderedMedicine.getMedicineName());
                object.put("company_name", orderedMedicine.getMedicineCompany());
                object.put("pharma_id", pId);
                object.put("code", orderedMedicine.getCode());
                object.put("qty", String.valueOf(orderedMedicine.getQty()));
                object.put("rate", String.valueOf(orderedMedicine.getRate()));
                object.put("cost", String.valueOf(orderedMedicine.getCost()));
                object.put("salesperson_id", sId);
                orderItems.put(object);
            }
            JSONArray allDataArray = new JSONArray();
            for(MakeYourOwn makeYourOwn: makeYourOwns){
                JSONObject each = new JSONObject();
                try {
                    Log.i("Make", makeYourOwn.getName());
                    each.put("Chosen", makeYourOwn.getName());
                } catch (JSONException e){
                    e.printStackTrace();
                }
                allDataArray.put(each);
            }
            JSONObject object = new JSONObject();
            try {
                object.put("choosen_data", allDataArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            orderItems.put(object);
        } catch (JSONException e) {
            Log.v("Saf", e.toString());
        }
        Log.i("orderItems", orderItems.toString());
        return orderItems.toString();
    }

    private Boolean IamConnect() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}
