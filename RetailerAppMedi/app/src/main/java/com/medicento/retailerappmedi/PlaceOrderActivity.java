package com.medicento.retailerappmedi;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.data.Area;
import com.medicento.retailerappmedi.data.CountDrawable;
import com.medicento.retailerappmedi.data.ListViewAdapter;
import com.medicento.retailerappmedi.data.MenuItems;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.data.AutoCompleteAdapter;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.MakeYourOwn;
import com.medicento.retailerappmedi.data.Medicine;
import com.medicento.retailerappmedi.data.MedicineAuto;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.OrderedMedicineAdapter;
import com.medicento.retailerappmedi.data.SalesPerson;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static android.view.View.GONE;

public class PlaceOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OrderedMedicineAdapter.OverallCostChangeListener {

    TextView pharmacyName;
    private Timer timer;

    ProgressDialog progressDialog;

    ArrayList<Area> tempareas;

    Boolean isSlotChoosen;

    NavigationView mNavigationView;
    public static ArrayList<MakeYourOwn> makeYourOwns;
    RecyclerView mOrderedMedicinesListView;
    public static String strcode;
    Spinner dateSpinner, slotSpinner;
    ProgressBar progressBar;
    SharedPreferences mSharedPreferences;
    String usercode;
    SalesPerson sp;
    AutoCompleteTextView mMedicineList;
    Toolbar mToolbar;
    Button filter, notify;

    CardView first_time;

    AutoCompleteTextView state, city, area;

    ListView listView;

    ArrayAdapter<String> stateadapter;

    ArrayList<String> states, cities;

    MaterialCardView materialCardView;

    EditText email, phone;

    Button submit;

    TextView cart_sub;

    public static OrderedMedicineAdapter mOrderedMedicineAdapter;
    public static String url = "https://retailer-app-api.herokuapp.com/product/medimap";
    public static String url1 = "https://retailer-app-api.herokuapp.com/product/updateApp";
    String versionUpdate;
    private ProgressDialog pDialog;
    LinearLayout mCostLayout;
    TextView mTotalTv;
    int Count;
    public String name, type, content;
    ArrayList<String> medicine1;
    public static ArrayList<Medicine> MedicineDataList;

    Boolean isLoading;

    ArrayList<Area> areas;

    CardView cardView;

    EditText mobile, email_update;

    String area_id;

    Menu menu;

    Animation mAnimation;
    Button update;

    int TCost;
    private final int uniqueId = 12345;
    NotificationCompat.Builder notification;
    public static int code;
    ArrayAdapter<String> mMedicineAdapter;
    AlertDialog alert;

    ArrayList<MedicineAuto> medicineAuto;

    AutoCompleteAdapter medicineAdapter;

    ArrayList<MenuItems> menuItems;

    Button choose_slot;

    ArrayAdapter<String> cityeadapter;

    String slot;

    Button showState, showCity, showArea, save;

    int count;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        isSlotChoosen = false;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressBar = findViewById(R.id.progressBar);

        isLoading = false;

        email_update = findViewById(R.id.email);
        phone = findViewById(R.id.mobile);

        showCity = findViewById(R.id.showcity);
        showState = findViewById(R.id.showstate);
        showArea = findViewById(R.id.shwarea);

        update = findViewById(R.id.submit);


        city = findViewById(R.id.city_spinner);
        first_time = findViewById(R.id.first_time_login);

        cardView = findViewById(R.id.card_slot);

        dateSpinner = findViewById(R.id.day_spinner);
        slotSpinner = findViewById(R.id.slot_spinner);

        materialCardView = findViewById(R.id.card);
        save = findViewById(R.id.save);

        cart_sub = findViewById(R.id.tv2);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.mobile);

        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.getText().toString().isEmpty()) {
                    Toast.makeText(PlaceOrderActivity.this, "Area : " + area_id + "\n Email : " + email.getText().toString() + "\n Phone : " + phone.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlaceOrderActivity.this, "Please Enter Valid Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        state = findViewById(R.id.state_spinner);

        choose_slot = findViewById(R.id.choose);

        choose_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dateSpinner.getSelectedItem().toString();
                String slot1 = slotSpinner.getSelectedItem().toString();
                slot = date + slot1;
                cardView.setVisibility(GONE);
            }
        });

        ArrayAdapter<String> stateadapter = new ArrayAdapter<String>(this, R.layout.spinner_item_states, getResources().getStringArray(R.array.state));

        state.setAdapter(stateadapter);


        ArrayAdapter<String> cityeadapter = new ArrayAdapter<String>(this, R.layout.spinner_item_states, getResources().getStringArray(R.array.city));

        city.setAdapter(cityeadapter);

        area = findViewById(R.id.area_spinner);

        ArrayAdapter<String> areaeadapter = new ArrayAdapter<String>(this, R.layout.spinner_item_areas, getResources().getStringArray(R.array.area));

        area.setAdapter(areaeadapter);

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
        Count = 0;

        Paper.init(this);

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {

            sp = gson.fromJson(cache, SalesPerson.class);

            if (sp.getPhone() == null || sp.getPhone().isEmpty() || sp.getPhone().length() == 0) {
                first_time.setVisibility(View.VISIBLE);
                getArea();
            } else {
                first_time.setVisibility(View.GONE);
            }
        } else {
            if (getIntent() != null) {
                Intent intent = getIntent();
                sp = (SalesPerson) intent.getSerializableExtra("user");
            }
        }

        listView = findViewById(R.id.list_menu_items);

        menuItems = new ArrayList<>();

        menuItems.add(new MenuItems("Profile", "1", getResources().getDrawable(R.drawable.ic_man)));
        menuItems.add(new MenuItems("Recent Order", "2", getResources().getDrawable(R.drawable.ic_trolley)));
        menuItems.add(new MenuItems("Save List", "3", getResources().getDrawable(R.drawable.ic_save)));
        menuItems.add(new MenuItems("Sign Out", "1", getResources().getDrawable(R.drawable.ic_logout)));

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, menuItems);

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(PlaceOrderActivity.this, ProfileNew.class);
                        intent.putExtra("usercode", usercode);
                        intent.putExtra("SalesPerson", sp);
                        startActivity(intent);
                        break;
                    case 1:
                        if (!IamConnect()) {
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
                        Gson gson = new Gson();
                        final String json = mSharedPreferences.getString("saved_medicine", null);
                        Log.d("saved_medicine", json);
                        Type type = new TypeToken<ArrayList<OrderedMedicine>>() {
                        }.getType();
                        ArrayList<OrderedMedicine> medicineDataList = gson.fromJson(json, type);
                        for(OrderedMedicine orderedMedicine: medicineDataList) {
                            mOrderedMedicineAdapter.add(orderedMedicine);
                        }
                        setCount(PlaceOrderActivity.this, "" + mOrderedMedicineAdapter.getItemCount());
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case 3:
                        clearUserDetails();
                        intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
                        startActivityForResult(intent, Constants.RC_SIGN_IN);
                        break;

                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        TCost = 0;

        if (sp != null) {
            addSalesPersonDetailsToNavDrawer();
            final String json = mSharedPreferences.getString("saved", null);
            Type type = new TypeToken<ArrayList<Medicine>>() {
            }.getType();
            MedicineDataList = gson.fromJson(json, type);
            if (MedicineDataList == null) {
                MedicineDataList = new ArrayList<>();
                new GetNames().execute();
            } else {
                medicine1 = new ArrayList<>();
                medicineAuto = new ArrayList<>();
                for (Medicine med : MedicineDataList) {
                    medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice()));
                    medicine1.add(med.getMedicentoName());
                }

                medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
                mMedicineList.setAdapter(medicineAdapter);
                mMedicineList.setEnabled(true);
            }

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
                Toast.makeText(PlaceOrderActivity.this, "Notify", Toast.LENGTH_SHORT).show();
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
                InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                mMedicineList.setText("");
                Medicine medicine = null;
                for (Medicine med : MedicineDataList) {
                    if (med.getMedicentoName().equals(medicineAdapter.getItem(position).getName())) {
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
                        medicine.getPacking(),
                        medicine.getMrp(),
                        medicine.getScheme()));
                float cost = Float.parseFloat(mTotalTv.getText().toString().substring(1));
                float overall = cost + medicine.getPrice();
                mTotalTv.setText(getString(R.string.ruppe_symbol) + overall);
                count += 1;
                setCount(PlaceOrderActivity.this, "" + mOrderedMedicineAdapter.getItemCount());
                Gson gson = new Gson();
                String json = gson.toJson(mOrderedMedicineAdapter.getList());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("saved_medicine", json);
                editor.apply();
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
                setCount(PlaceOrderActivity.this, "" + mOrderedMedicineAdapter.getItemCount());
                Gson gson = new Gson();
                String json = gson.toJson(mOrderedMedicineAdapter.getList());
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("saved_medicine", json);
                editor.apply();
            }
        }).attachToRecyclerView(mOrderedMedicinesListView);
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);

        showState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.showDropDown();
            }
        });

        showCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city.showDropDown();
            }
        });

        showArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                area.showDropDown();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()
                &&phone.getText().toString().isEmpty()) {
                    Toast.makeText(PlaceOrderActivity.this, "Please Enter Details", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    progressDialog = new ProgressDialog(PlaceOrderActivity.this);
                    progressDialog.setMessage("Please Wait Just Give Us A Few Seconds ...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    String url = "https://retailer-app-api.herokuapp.com/pharma/updatePharma?id="+sp.getId();
                    url += "&area="+area_id;
                    url += "&phone="+phone.getText().toString();
                    url += "&email="+email.getText().toString();
                    RequestQueue requestQueue = Volley.newRequestQueue(PlaceOrderActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.has("message")) {
                                            Toast.makeText(PlaceOrderActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                            first_time.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(PlaceOrderActivity.this, "Some Error Occured Please Try Again !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(PlaceOrderActivity.this, "Some Error Occured Please Try Again !", Toast.LENGTH_SHORT).show();
                                }
                            });
                    requestQueue.add(stringRequest);
                }
            }
        });
    }


    private void getArea() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait Just Give Us A Few Seconds ...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        areas = new ArrayList<>();
        states = new ArrayList<>();
        cities = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.AREA_FETCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject areaOnject = new JSONObject(response);
                            JSONArray areasArray = areaOnject.getJSONArray("areas");
                            for (int i = 0; i < areasArray.length(); i++) {
                                JSONObject jsonObject = areasArray.getJSONObject(i);
                                areas.add(new Area(jsonObject.getString("area_name"),
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("area_state"),
                                        jsonObject.getString("area_city")));
                            }
                            tempareas = new ArrayList<>();
                            tempareas.add(new Area("Area Not Available",
                                    "0",
                                    "none",
                                    "none"));
                            for (Area area : areas) {
                                if (!states.contains(area.getState())) {
                                    states.add(area.getState());
                                }
                                if (area.getState().equals(areas.get(1).getState())) {
                                    if (!cities.contains(area.getCity())) {
                                        cities.add(area.getCity());
                                    }
                                    if (!tempareas.get(0).getName().equals(area.getName())) {
                                        if (cities.get(0).equals(area.getCity())) {
                                            tempareas.add(area);
                                        }
                                    }
                                }
                            }
                            Collections.sort(states, String.CASE_INSENSITIVE_ORDER);

                            stateadapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.spinner_item_states, states);

                            state.setAdapter(stateadapter);

                            state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    cities.clear();
                                    tempareas.clear();
                                    for (Area area : areas) {
                                        if (area.getState().equals(stateadapter.getItem(position).toString())) {
                                            if (!cities.contains(area.getCity())) {
                                                cities.add(area.getCity());
                                            }
                                            if (cities.size() > 0) {
                                                if (cities.get(0).equals(area.getCity())) {
                                                    tempareas.add(area);
                                                }
                                            }
                                        }
                                    }

                                    Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);

                                    cityeadapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.spinner_item_states, cities);

                                    city.setAdapter(cityeadapter);

                                    city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            tempareas.clear();
                                            for (Area area : areas) {
                                                if (area.getCity().equals(cityeadapter.getItem(position))) {
                                                    tempareas.add(area);
                                                }
                                            }

                                            area = findViewById(R.id.area_spinner);

                                            if (!tempareas.isEmpty()) {
                                                Collections.sort(tempareas, new Comparator<Area>() {
                                                    @Override
                                                    public int compare(Area c1, Area c2) {
                                                        //You should ensure that list doesn't contain null values!
                                                        return c1.getName().compareTo(c2.getName());
                                                    }
                                                });
                                            }
                                            tempareas.add(0, new Area("Area Not Available",
                                                    "0",
                                                    "none",
                                                    "none"));

                                            final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(PlaceOrderActivity.this, R.layout.spinner_item_areas, tempareas);

                                            area.setAdapter(areaeadapter);

                                            area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    area_id = areaeadapter.getItem(position).getId();
                                                }
                                            });

                                        }
                                    });


                                    area = findViewById(R.id.area_spinner);

                                    if (!tempareas.isEmpty()) {
                                        Collections.sort(tempareas, new Comparator<Area>() {
                                            @Override
                                            public int compare(Area c1, Area c2) {
                                                //You should ensure that list doesn't contain null values!
                                                return c1.getName().compareTo(c2.getName());
                                            }
                                        });
                                    }

                                    tempareas.add(0, new Area("Area Not Available",
                                            "0",
                                            "none",
                                            "none"));

                                    final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(PlaceOrderActivity.this, R.layout.spinner_item_areas, tempareas);

                                    area.setAdapter(areaeadapter);

                                    area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            area_id = areaeadapter.getItem(position).getId();
                                        }
                                    });


                                }
                            });

                            Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);

                            cityeadapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.spinner_item_states, cities);

                            city.setAdapter(cityeadapter);

                            city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    tempareas.clear();
                                    for (Area area : areas) {
                                        if (area.getCity().equals(cityeadapter.getItem(position))) {
                                            tempareas.add(area);
                                        }
                                    }

                                    area = findViewById(R.id.area_spinner);

                                    if (!tempareas.isEmpty()) {
                                        Collections.sort(tempareas, new Comparator<Area>() {
                                            @Override
                                            public int compare(Area c1, Area c2) {
                                                //You should ensure that list doesn't contain null values!
                                                return c1.getName().compareTo(c2.getName());
                                            }
                                        });
                                    }

                                    tempareas.add(0, new Area("Area Not Available",
                                            "0",
                                            "none",
                                            "none"));

                                    final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(PlaceOrderActivity.this, R.layout.spinner_item_areas, tempareas);

                                    area.setAdapter(areaeadapter);

                                    area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            area_id = areaeadapter.getItem(position).getId();
                                        }
                                    });

                                }
                            });

                            area = findViewById(R.id.area_spinner);

                            if (!tempareas.isEmpty()) {
                                Collections.sort(tempareas, new Comparator<Area>() {
                                    @Override
                                    public int compare(Area c1, Area c2) {
                                        //You should ensure that list doesn't contain null values!
                                        return c1.getName().compareTo(c2.getName());
                                    }
                                });
                            }
                            tempareas.add(0, new Area("Area Not Available",
                                    "0",
                                    "none",
                                    "none"));

                            final ArrayAdapter<Area> areaeadapter = new ArrayAdapter<Area>(PlaceOrderActivity.this, R.layout.spinner_item_areas, tempareas);

                            area.setAdapter(areaeadapter);

                            area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    area_id = areaeadapter.getItem(position).getId();
                                }
                            });

                            progressDialog.dismiss();
                        } catch (
                                JSONException e) {

                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                    }
                }
        );
        requestQueue.add(stringRequest);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(PlaceOrderActivity.this, "Welcome ", Toast.LENGTH_SHORT).show();
                SalesPerson sp = (SalesPerson) data.getSerializableExtra("salesperosn");
                mNavigationView = findViewById(R.id.nav_view);
                addSalesPersonDetailsToNavDrawer();
                Gson gson = new Gson();
                final String json = mSharedPreferences.getString("saved", null);
                Type type = new TypeToken<ArrayList<Medicine>>() {
                }.getType();
                MedicineDataList = gson.fromJson(json, type);
                if (MedicineDataList == null) {
                    MedicineDataList = new ArrayList<>();
                    new GetNames().execute();
                } else {
                    medicine1 = new ArrayList<>();
                    medicineAuto = new ArrayList<>();
                    for (Medicine med : MedicineDataList) {
                        medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice()));
                        medicine1.add(med.getMedicentoName());
                    }

                    medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
                    mMedicineList.setAdapter(medicineAdapter);
                    mMedicineList.setEnabled(true);
                }

            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.equals("company")) {
                    ArrayList<Medicine> manu = (ArrayList<Medicine>) data.getSerializableExtra("list");
                    ArrayList<String> medicine2 = new ArrayList<>();
                    medicineAuto = new ArrayList<>();
                    for (Medicine manu1 : manu) {
                        medicineAuto.add(new MedicineAuto(manu1.getMedicentoName(), manu1.getCompanyName(), manu1.getPrice()));
                        medicine1.add(manu1.getMedicentoName());
                    }

                    medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
                    mMedicineList.setAdapter(medicineAdapter);
                    } else if (result.equals("offer")) {
                    ArrayList<Medicine> manu = (ArrayList<Medicine>) data.getSerializableExtra("list");
                    ArrayList<String> medicine2 = new ArrayList<>();
                    for (Medicine manu1 : manu) {
                        medicine2.add(manu1.getMedicentoName());
                    }
                    mMedicineAdapter = new ArrayAdapter<String>(PlaceOrderActivity.this, R.layout.support_simple_spinner_dropdown_item, medicine2);
                    mMedicineList.setAdapter(mMedicineAdapter);
                }
                Toast.makeText(PlaceOrderActivity.this, "Result : " + result, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(PlaceOrderActivity.this, "Canceled ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {

                makeYourOwns = (ArrayList<MakeYourOwn>) data.getSerializableExtra("make");
                mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());
                mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
                mOrderedMedicineAdapter.setOverallCostChangeListener(this);
                mOrderedMedicineAdapter.setContext(this);
                for (OrderedMedicine med : (ArrayList<OrderedMedicine>) data.getSerializableExtra("myList")) {
                    mOrderedMedicineAdapter.add(med);
                }
            }
        }
    }

    private void addSalesPersonDetailsToNavDrawer() {

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {

            sp = new Gson().fromJson(cache, SalesPerson.class);
        }

        if (sp != null) {
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
    }

    @Override
    public void onCostChanged(float newCost, int qty, String type) {
        Gson gson = new Gson();
        String json = gson.toJson(mOrderedMedicineAdapter.getList());
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("saved_medicine", json);
        editor.apply();
        Log.d(getString(R.string.TAG), "count" + qty);
        mTotalTv.setText(getString(R.string.ruppe_symbol) + newCost + "");
        setCount(PlaceOrderActivity.this, Integer.toString(mOrderedMedicineAdapter.getItemCount()));
    }

    private class GetNames extends AsyncTask<Void, Void, Void> {


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
            String jsonStr = sh.makeServiceCall(url+"?sp="+sp.getmAllocatedPharmaId());
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
                        MedicineDataList.get(i).setMrp(c.getInt("mrp"));
                        MedicineDataList.get(i).setScheme(c.getString("scheme"));
                        medicineAuto.add(new MedicineAuto(c.getString("medicento_name"),
                                c.getString("company_name"),
                                c.getInt("price")));
                        medicine1.add(c.getString("medicento_name"));
                    }
                } catch (final JSONException e) {
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PlaceOrderActivity.this, "Now You Can Choose Medicine", Toast.LENGTH_SHORT).show();
                }
            });
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
        if(!isSlotChoosen) {
            MenuItem menuItem = menu.findItem(R.id.action_proceed);
            LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

            CountDrawable badge;

            cart_sub.setText("Cart Sub Total (" + count + " Items ) : ");

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (isSlotChoosen) {
            isSlotChoosen=false;
            makeYourOwns = new ArrayList<>();
            String json = extractJsonFromOrderItemsList(mOrderedMedicineAdapter.getList(), makeYourOwns, sp.getmAllocatedPharmaId(), sp.getId(), slot);
            new PlaceOrder().execute(json);
        } else {
            item.setIcon(R.drawable.ic_buy);
            if (id == R.id.action_proceed) {
                isSlotChoosen=true;
                if (!IamConnect()) {
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

                        Date today, tomorrow;

                        cardView.setVisibility(View.VISIBLE);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String str = sdf.format(new Date());

                        if ((7 < Integer.valueOf(str.substring(0, 2))) && (Integer.valueOf(str.substring(0, 2)) <= 18)) {
                            Calendar calendar = Calendar.getInstance();
                            today = calendar.getTime();

                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            tomorrow = calendar.getTime();


                            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                            String todayAsString = dateFormat.format(today);
                            String tomorrowAsString = dateFormat.format(tomorrow);

                            ArrayList<String> dates = new ArrayList<>();
                            dates.add(todayAsString);
                            dates.add(tomorrowAsString);

                            ArrayAdapter<String> dates_adapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, dates);

                            dateSpinner.setAdapter(dates_adapter);

                            dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (i == 0) {
                                        ArrayList<String> slots = new ArrayList<>();
                                        slots.add(" Till 7 PM");


                                        ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderActivity.this,
                                                android.R.layout.simple_spinner_item, slots);

                                        slotSpinner.setAdapter(slots_adapter);
                                    } else {
                                        ArrayList<String> slots = new ArrayList<>();
                                        slots.add(" Till 1 PM");
                                        slots.add(" Till 7 PM");


                                        ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderActivity.this,
                                                android.R.layout.simple_spinner_item, slots);

                                        slotSpinner.setAdapter(slots_adapter);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        } else {

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            today = calendar.getTime();

                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            tomorrow = calendar.getTime();


                            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

                            String todayAsString = dateFormat.format(today);
                            String tomorrowAsString = dateFormat.format(tomorrow);

                            ArrayList<String> dates = new ArrayList<>();
                            dates.add(todayAsString);
                            dates.add(tomorrowAsString);

                            ArrayAdapter<String> dates_adapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, dates);

                            dateSpinner.setAdapter(dates_adapter);

                            ArrayList<String> slots = new ArrayList<>();
                            slots.add(" Till 1 PM");
                            slots.add(" Till 7 PM");


                            ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderActivity.this,
                                    android.R.layout.simple_spinner_item, slots);

                            slotSpinner.setAdapter(slots_adapter);

                        }

                    } else {
                        Toast.makeText(PlaceOrderActivity.this, "Please Select Some Medicine", Toast.LENGTH_SHORT).show();
                        return super.onOptionsItemSelected(item);
                    }
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
        } else if (id == R.id.about_me) {
            intent = new Intent(this, SalesPersonDetails.class);
            intent.putExtra("usercode", usercode);
            intent.putExtra("SalesPerson", sp);
            startActivity(intent);
        } else if (id == R.id.Recentorder) {
            if (!IamConnect()) {
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

        String cache = Paper.book().read("user");

        if (cache == null || cache.isEmpty()) {
            Intent intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else {
            timer = new Timer();
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            LogOutTimerTask logOutTimerTask = new LogOutTimerTask();
            timer.schedule(logOutTimerTask, 1200000);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Gson gson = new Gson();
        final String json = mSharedPreferences.getString("saved_medicine", null);
        Log.d("saved_medicine", json);
        Type type = new TypeToken<ArrayList<OrderedMedicine>>() {
        }.getType();
        ArrayList<OrderedMedicine> medicineDataList = gson.fromJson(json, type);
        for(OrderedMedicine orderedMedicine: medicineDataList) {
            mOrderedMedicineAdapter.add(orderedMedicine);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        String cache = Paper.book().read("user");

        if (cache == null || cache.isEmpty()) {
            Intent intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else {

            if (!IamConnect()) {
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
                                    for (int i = 0; i < version.length(); i++) {
                                        JSONObject v = version.getJSONObject(i);
                                        versionUpdate = v.getString("version");
                                    }
                                    if (!versionUpdate.equals(Constants.VERSION)) {
                                        alertDialogForUpdate();
                                    }
                                    code = spo.getInt("code");
                                    count1[0] = spo.getInt("count");
                                    int count = mSharedPreferences.getInt("count", 0);
                                    if (code == 101 && count <= spo.getInt("count")) {
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putInt("count", count1[0] + 1);
                                        editor.apply();
                                        if (!isLoading) {
                                            new GetNames().execute();
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

        }
    }


    private void alertDialogForUpdate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this);

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.update_available);

        Button cancel, update;
        cancel = dialog1.findViewById(R.id.cancel);
        update = dialog1.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.medicento.retailerappmedi"
                                )
                        )
                );
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.show();

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
            Intent intent = new Intent(PlaceOrderActivity.this, SignUpActivity.class);
            startActivity(intent);
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
            Log.d("info", s);
            String message = "Something Went Wrong Please Try Again!!";
            try {
                JSONObject jsonObject = new JSONObject(s);
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (message.equals("Order has been placed successfully")) {
                Intent intent = new Intent(PlaceOrderActivity.this, OrderConfirmed.class);
                intent.putExtra("date", slot);
                intent.putExtra("pharmacy", pharmacyName.getText().toString());
                intent.putExtra("slots", slot);
                intent.putExtra("orderDetails", mOrderedMedicineAdapter.getList());
                intent.putExtra("TotalCost", Float.valueOf(mTotalTv.getText().toString().substring(1)));
                intent.putExtra("json", s);
                startActivity(intent);
            } else {
                Toast.makeText(PlaceOrderActivity.this, message, Toast.LENGTH_SHORT).show();
            }
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

    private static String extractJsonFromOrderItemsList(ArrayList<OrderedMedicine> data, ArrayList<MakeYourOwn> data1, String pId, String sId, String slot) {
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
                object.put("mrp", String.valueOf(orderedMedicine.getMrp()));
                object.put("scheme", String.valueOf(orderedMedicine.getScheme()));
                object.put("source", "Retailer App");
                object.put("salesperson_id", sId);
                object.put("slot", slot);
                orderItems.put(object);
            }
            JSONArray allDataArray = new JSONArray();
            for (MakeYourOwn makeYourOwn : makeYourOwns) {
                JSONObject each = new JSONObject();
                try {
                    Log.i("Make", makeYourOwn.getName());
                    each.put("Chosen", makeYourOwn.getName());
                } catch (JSONException e) {
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
