package com.medicento.retailerappmedi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.Utils.MedicentoUtils;
import com.medicento.retailerappmedi.activity.DidntFindMedicineActivity;
import com.medicento.retailerappmedi.activity.NotificationActivity;
import com.medicento.retailerappmedi.activity.PaymentSummaryActivity;
import com.medicento.retailerappmedi.activity.RetailerWebLogOut;
import com.medicento.retailerappmedi.adapter.EssentialAdapter;
import com.medicento.retailerappmedi.adapter.MedicineAdapter;
import com.medicento.retailerappmedi.adapter.SearchMedicineAdapter;
import com.medicento.retailerappmedi.data.Category;
import com.medicento.retailerappmedi.data.ListViewAdapter;
import com.medicento.retailerappmedi.data.Medicine;
import com.medicento.retailerappmedi.data.MenuItems;
import com.medicento.retailerappmedi.data.MenuItemsBuilder;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SearchMedicineAdapter.OnItemClickListener,
        MedicineAdapter.OverallCostChangeListener {

    private static final String TAG = "HomeActivity";
    TextView pharma_name, cart, home_text;
    ImageView home_image;
    SalesPerson sp;
    RecyclerView medicine_rv;
    private MedicineAdapter medicineAdapter;
    private ArrayList<Medicine> medicines, tempMedicines;
    private ArrayList<OrderedMedicine> medicineOrdereds;
    private RecyclerView ordered_medicines_list_rv, essential_rv;
    private SearchMedicineAdapter searchMedicineAdapter;

    LinearLayout essential, notification, payment, recentorder;
    EditText search_medicine;
    private boolean isSearching = false, isPlacingOrder = false;

    private SharedPreferences mSharedPreferences;
    private Dialog dialog_placing_order;
    private JSONObject jsonObject;
    private String order_id = "", slot = "", date = "";
    private Button buy, message_us;
    private ArrayList<Category> essentials;
    private EssentialAdapter essentialAdapter;
    Toolbar mToolbar;
    DrawerLayout drawer;
    String usercode = "";
    AlertDialog alert;
    ListView listView;
    ArrayList<MenuItems> menuItems;
    NavigationView mNavigationView;
    ImageView more, left, right;
    Handler handler;
    Runnable runnable;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);

        setContentView(R.layout.activity_home);

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (!MedicentoUtils.isMyServiceRunning(FetchMedicineService.class, this)) {
            Context context = getApplicationContext();
            Intent intent1;
            intent1 = new Intent(context, FetchMedicineService.class);
            try {
                context.startService(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String json = mSharedPreferences.getString("medicines_saved", null);
        Type type = new TypeToken<ArrayList<Medicine>>() {
        }.getType();
        tempMedicines = new Gson().fromJson(json, type);

        String order_json = mSharedPreferences.getString("saved_medicine_pharma", null);
        try {
            JSONObject jsonObject = new JSONObject(order_json);
            String data = jsonObject.getString(sp.getmAllocatedPharmaId());
            Type type1 = new TypeToken<ArrayList<OrderedMedicine>>() {
            }.getType();
            medicineOrdereds = new Gson().fromJson(data, type1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mToolbar = findViewById(R.id.toolbar);
        message_us = findViewById(R.id.message_us);
        more = findViewById(R.id.more);
        left = findViewById(R.id.left);
        progress_bar = findViewById(R.id.progress_bar);
        right = findViewById(R.id.right);
        setSupportActionBar(mToolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        listView = findViewById(R.id.list_menu_items);

        setNavigationItem();

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(HomeActivity.this, ProfileNew.class);
                        intent.putExtra("usercode", usercode);
                        intent.putExtra("SalesPerson", sp);
                        startActivity(intent);
                        break;
                    case 1:
                        if (!amIConnect(HomeActivity.this)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                            intent = new Intent(HomeActivity.this, RecentOrderActivity.class);
                            intent.putExtra("SalesPerson", sp);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        intent = new Intent(HomeActivity.this, PaymentSummaryActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(HomeActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(HomeActivity.this, DidntFindMedicineActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(HomeActivity.this, RetailerWebLogOut.class);
                        startActivity(intent);
                        break;
                    case 6:
                        clearUserDetails();
                        intent = new Intent(HomeActivity.this, SignUpActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                }
            }
        });

        message_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String orderShareDetails = "*Pharmacy Name* : " + sp.getName() + "\n" +
                        "*Address* : " + sp.getAddress() + " \n" +
                        "*Area* : " + sp.getArea_name() + "\n" +
                        "*City* : " + sp.getCity_name() + "\n" +
                        "*State* : " + sp.getState_name() + "\n" +
                        "*Email Id* : " + sp.getEmail() + "\n" +
                        "*Regd Phone* : " + sp.getPhone() + "\n";
                Intent share_intent = new Intent(Intent.ACTION_VIEW);
                try {
                    String url = "https://api.whatsapp.com/send?phone=+917829349000&text=" + URLEncoder.encode(orderShareDetails, "UTF-8");
                    share_intent.setPackage("com.whatsapp");
                    share_intent.setData(Uri.parse(url));
                    startActivity(share_intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ordered_medicines_list_rv = findViewById(R.id.ordered_medicines_list_rv);
        pharma_name = findViewById(R.id.pharma_name);
        medicine_rv = findViewById(R.id.medicine_rv);
        essential_rv = findViewById(R.id.essential_rv);
        home_text = findViewById(R.id.home_text);
        home_image = findViewById(R.id.home_image);
        cart = findViewById(R.id.cart);
        buy = findViewById(R.id.buy);

        notification = findViewById(R.id.notification);
        payment = findViewById(R.id.payment);
        recentorder = findViewById(R.id.recentorder);
        essential = findViewById(R.id.essential);
        search_medicine = findViewById(R.id.search_medicine);

        notification.setOnClickListener(this);
        payment.setOnClickListener(this);
        recentorder.setOnClickListener(this);
        essential.setOnClickListener(this);
        buy.setOnClickListener(this);

        Date today;
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        today = calendar.getTime();
        slot = "Till 1PM";
        if (hour >= 8) {
            if (hour < 16) {
                slot = "Till 7PM";
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                today = calendar.getTime();
            }
        }
        date = dateFormat.format(today);

        search_medicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    populateMedicine();
                } else {
                    ordered_medicines_list_rv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pharma_name.setText(sp.getName().toUpperCase());

        medicines = new ArrayList<>();
        if (medicineOrdereds == null) {
            medicineOrdereds = new ArrayList<>();
        }
        essentials = new ArrayList<>();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        essentialAdapter = new EssentialAdapter(essentials, this, metrics);
        essential_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        essential_rv.setAdapter(essentialAdapter);

        medicineAdapter = new MedicineAdapter(medicineOrdereds, this);
        medicine_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        medicine_rv.setAdapter(medicineAdapter);
        medicineAdapter.setmOverallCostChangeListener(this);

        searchMedicineAdapter = new SearchMedicineAdapter(this, medicines);
        ordered_medicines_list_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ordered_medicines_list_rv.setAdapter(searchMedicineAdapter);
        searchMedicineAdapter.setOnItemClickListener(this);

        home_text.setTextColor(Color.parseColor("#18989e"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            home_image.setElevation(5f);
        }
        home_image.setColorFilter(Color.parseColor("#18989e"));

        addSalesPersonDetailsToNavDrawer();

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) essential_rv.getLayoutManager();
                    essential_rv.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) essential_rv.getLayoutManager();
                    essential_rv.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getCategory();
    }

    private void getCategory() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_category_lists/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject each = data.getJSONObject(i);
                                essentials.add(new Category(JsonUtils.getJsonValueFromKey(each, "name"))
                                        .setImage_url(JsonUtils.getJsonValueFromKey(each, "image_url")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (handler == null) {
                            handler = new Handler();
                            if (runnable == null) {
                                runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        slideImages();
                                    }
                                };
                            }
                        }
                        progress_bar.setVisibility(View.GONE);
                        handler.postDelayed(runnable, 5000);
                        essentialAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                    }
                }
        );

        progress_bar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    private void slideImages() {

        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    slideImages();
                }
            };
        }
        try {
            try {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) essential_rv.getLayoutManager();
                if (linearLayoutManager.findFirstVisibleItemPosition() + 4 >= essentials.size()) {
                    essential_rv.scrollToPosition(0);
                } else {
                    essential_rv.scrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 5);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(runnable, 5000);
    }

    private void addSalesPersonDetailsToNavDrawer() {

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = new Gson().fromJson(cache, SalesPerson.class);
        }

        if (sp != null) {
            mNavigationView = findViewById(R.id.nav_view);

            LinearLayout linearLayout = findViewById(R.id.header_header);
            View headerView = mNavigationView.getHeaderView(0);
            TextView navHeaderSalesmanName = linearLayout.findViewById(R.id.username_header);
            TextView navHeaderSalesmanEmail = linearLayout.findViewById(R.id.user_email_header);
            TextView navp = linearLayout.findViewById(R.id.user_pharmaid);
            navHeaderSalesmanName.setText(sp.getName());
            navHeaderSalesmanEmail.setText("PharmaCode: " + sp.getUsercode());
            navp.setText(sp.getName());
        }
    }

    private void setNavigationItem() {
        menuItems = new ArrayList<>();

        menuItems.add(
                new MenuItemsBuilder().setName("Profile").
                        setId("1").setDrawable(getResources().getDrawable(R.drawable.ic_man))
                        .setDescription("Contact Details/ DL Details")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Recent Order")
                        .setId("2")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_trolley))
                        .setDescription("Return - Cancel - Reorder - View Details")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Payment Details")
                        .setId("3")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_purse))
                        .setDescription("Credit Amt - Payments - Due Date")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Notifications")
                        .setId("4")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_notification))
                        .setDescription("Order Updates - App Updates")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Did't find your medicine??")
                        .setId("5")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_search_file))
                        .setDescription("We will try arranging from other Distributors")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Retailer Web")
                        .setId("7")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_computer_black_24dp))
                        .setDescription("Sign in to Retailer Web Portal via QR Code")
                        .createMenuItems());
        menuItems.add(
                new MenuItemsBuilder().setName("Sign Out")
                        .setId("6")
                        .setDrawable(getResources().getDrawable(R.drawable.ic_logout))
                        .setDescription("Signing off Medicento Retailer App")
                        .createMenuItems());

        ListViewAdapter listViewAdapter = new ListViewAdapter(this, menuItems);

        listView.setAdapter(listViewAdapter);
    }

    private void populateMedicine() {

        if (isSearching) {
            return;
        }

        isSearching = true;

        if (tempMedicines != null) {
            medicines.clear();
            String name = search_medicine.getText().toString();
            for (Medicine medicine : tempMedicines) {
                if (medicine.getMedicentoName().toLowerCase().startsWith(name.toLowerCase())) {
                    medicines.add(medicine);
                }
            }
            ordered_medicines_list_rv.setVisibility(View.VISIBLE);
            searchMedicineAdapter.notifyDataSetChanged();
            isSearching = false;
        } else {
            String json = mSharedPreferences.getString("medicines_saved", null);
            Type type = new TypeToken<ArrayList<Medicine>>() {
            }.getType();
            tempMedicines = new Gson().fromJson(json, type);
            isSearching = false;
        }
    }

    private void clearUserDetails() {
        Paper.book().delete("user");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.notification:
                startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                break;
            case R.id.payment:
                startActivity(new Intent(HomeActivity.this, PaymentSummaryActivity.class));
                break;
            case R.id.recentorder:
                startActivity(new Intent(HomeActivity.this, RecentOrderActivity.class));
                break;
            case R.id.essential:
                startActivity(new Intent(HomeActivity.this, EssentialsActivity.class));
                break;
            case R.id.buy:
                if (medicineAdapter == null) {
                    return;
                }
                if (medicineAdapter.getItemCount() == 0) {
                    return;
                }
                placeOrder();
                break;
        }
    }

    @Override
    public void onClick(int positon) {

        View view1 = getWindow().getCurrentFocus();
        InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ime.hideSoftInputFromWindow(view1.getWindowToken(), 0);

        search_medicine.setText("");
        Medicine medicine = medicines.get(positon);

        medicineAdapter.add(new OrderedMedicine(medicine.getMedicentoName(),
                medicine.getCompanyName(),
                1,
                medicine.getPrice(),
                medicine.getCode(),
                medicine.getPrice(),
                medicine.getMstock(),
                medicine.getPacking(),
                medicine.getMrp(),
                medicine.getOffer_qty(),
                medicine.getDiscount(),
                medicine.getOffer_qty()));

        medicines.clear();
        searchMedicineAdapter.notifyDataSetChanged();
        ordered_medicines_list_rv.setVisibility(View.GONE);
    }

    @Override
    public void onCostChanged() {
        float price = 0;

        ArrayList<OrderedMedicine> medicines = medicineAdapter.getMedicines();

        for (OrderedMedicine medicine : medicines) {
            price += medicine.getRate() * medicine.getQty();
        }
        cart.setText(Html.fromHtml("Cart Sub Total (" + medicineAdapter.getItemCount() + " Items ) : <b>" + String.format("INR %.2f</b>", price)));
        Gson gson = new Gson();
        String json = gson.toJson(medicineAdapter.getMedicines());
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            jsonObject.put(sp.getmAllocatedPharmaId(), json);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("saved_medicine_pharma", jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {

        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/area/is_operational/?pk="+sp.getmAllocatedCityId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!JsonUtils.getBooleanValueFromJsonKey(jsonObject, "is_operational")) {
                                startActivity(
                                        new Intent(HomeActivity.this, CartPageOrderActivity.class)
                                                .putExtra("orders", medicineAdapter.getMedicines()));
                            } else {
                                placeOrderAfterCheck();
                            }
                        } catch (Exception e) {
                            placeOrderAfterCheck();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        placeOrderAfterCheck();
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void placeOrderAfterCheck() {
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/place_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog_placing_order != null) {
                            dialog_placing_order.dismiss();
                        }

                        try {
                            JSONObject jsonObject1 = new JSONObject(response);

                            String message = JsonUtils.getJsonValueFromKey(jsonObject1, "message");
                            String order_id = JsonUtils.getJsonValueFromKey(jsonObject1, "order_id");

                            if (message.equals("Order Placed")) {
                                Intent intent = new Intent(HomeActivity.this, OrderConfirmed.class);

                                intent.putExtra("pharmacy", pharma_name.getText().toString());

                                intent.putExtra("slots", slot);
                                intent.putExtra("date", date);
                                intent.putExtra("orderDetails", medicineAdapter.getMedicines());
                                intent.putExtra("TotalCost", medicineAdapter.getTotalCost());
                                intent.putExtra("order_id", order_id);

                                intent.putExtra("json", response);

                                Gson gson = new Gson();
                                if (jsonObject != null) {
                                    try {
                                        jsonObject.put(sp.getmAllocatedPharmaId(), "");
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putString("saved_medicine_pharma", jsonObject.toString());
                                        editor.apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put(sp.getmAllocatedPharmaId(), "");
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putString("saved_medicine_pharma", jsonObject.toString());
                                        editor.apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                startActivity(intent);
                            } else {
                                Toast.makeText(HomeActivity.this, "Order Cannot Be Placed Try Again.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                        }

                        isPlacingOrder = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (dialog_placing_order != null) {
                            dialog_placing_order.dismiss();
                        }
                        isPlacingOrder = false;
                        Toast.makeText(HomeActivity.this, "Error In The Network.", Toast.LENGTH_SHORT).show();
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
                    for (OrderedMedicine orderedMedicine : medicineAdapter.getMedicines()) {
                        JSONObject object = new JSONObject();
                        object.put("medicine_name", orderedMedicine.getMedicineName());
                        object.put("company_name", orderedMedicine.getMedicineCompany());
                        object.put("Itemcode", orderedMedicine.getCode());
                        object.put("Quantity", orderedMedicine.getQty());
                        object.put("price", orderedMedicine.getRate());
                        object.put("cost", orderedMedicine.getCost());
                        object.put("mrp", orderedMedicine.getMrp());
                        try {
                            object.put("scheme", orderedMedicine.getOffer_qty());
                        } catch (Exception e) {
                            object.put("scheme", "-");
                            e.printStackTrace();
                        }
                        orderItems.put(object);
                    }

                    jsonObject.put("items", orderItems);
                    params.put("order_items", jsonObject.toString());
                    params.put("pharmacy_id", sp.getmAllocatedPharmaId());


                    String order_cache = Paper.book().read("order_id");
                    if (order_cache != null && !order_cache.isEmpty()) {
                        order_id = order_cache;
                        params.put("order_id", order_id);
                    }
//                    if (!sales_code.getText().toString().isEmpty()) {
//                        params.put("sales_code", sales_code.getText().toString());
//                    }

                    params.put("date", date);
                    params.put("slot_time", slot);
                    params.put("source", "Retailer App");
                    params.put("area_id", "5");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        if (!isPlacingOrder) {

            dialog_placing_order = new Dialog(this);
            dialog_placing_order.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_placing_order.setCancelable(false);
            dialog_placing_order.setContentView(R.layout.dialog_placing_order);

            dialog_placing_order.show();
            isPlacingOrder = true;

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    0,
                    2));
            requestQueue.add(stringRequest);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null && runnable != null) {
            handler.postDelayed(runnable, 5000);
        }
    }
}
