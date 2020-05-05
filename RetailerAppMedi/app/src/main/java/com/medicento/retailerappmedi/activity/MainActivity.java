package com.medicento.retailerappmedi.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.FetchMedicineService;
import com.medicento.retailerappmedi.Filter;
import com.medicento.retailerappmedi.JsonParser;
import com.medicento.retailerappmedi.OrderConfirmed;
import com.medicento.retailerappmedi.ProfileNew;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.RecentOrderActivity;
import com.medicento.retailerappmedi.SignUpActivity;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.Utils.MedicentoUtils;
import com.medicento.retailerappmedi.adapter.ImagesAdapter;
import com.medicento.retailerappmedi.adapter.MedicineSearchAdapter;
import com.medicento.retailerappmedi.data.AutoCompleteAdapter;
import com.medicento.retailerappmedi.data.Constants;
import com.medicento.retailerappmedi.data.CountDrawable;
import com.medicento.retailerappmedi.data.GetMedicineResponse;
import com.medicento.retailerappmedi.data.ListViewAdapter;
import com.medicento.retailerappmedi.data.MakeYourOwn;
import com.medicento.retailerappmedi.data.Medicine;
import com.medicento.retailerappmedi.data.MedicineAuto;
import com.medicento.retailerappmedi.data.MedicineResponse;
import com.medicento.retailerappmedi.data.MenuItems;
import com.medicento.retailerappmedi.data.MenuItemsBuilder;
import com.medicento.retailerappmedi.data.OrderedMedicine;
import com.medicento.retailerappmedi.data.OrderedMedicineAdapter;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.order_related.OrderItem;
import com.medicento.retailerappmedi.interfaces.Api;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.getDeviceModel;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class MainActivity extends AppCompatActivity implements
        OrderedMedicineAdapter.OverallCostChangeListener, MedicineSearchAdapter.OnItemClickListener {

    private static final String TAG = "PlaceOrderAct";
    TextView pharmacyName, order_date;

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
    Button filter, notify, message_us;

    CardView first_time;

    ListView listView;

    MaterialCardView materialCardView;

    EditText email, phone;

    TextView cart_sub;

    String date, slot1;

    public static OrderedMedicineAdapter mOrderedMedicineAdapter;
    public static String url = "http://medicento-api.herokuapp.com/product/medimap";
    String versionUpdate;
    LinearLayout mCostLayout;
    TextView mTotalTv;
    int Count;
    public String name, type, content;
    ArrayList<String> medicine1;
    public static ArrayList<Medicine> MedicineDataList;

    Boolean isLoading;
    CardView cardView;

    EditText mobile, email_update;

    Menu menu;

    Animation mAnimation;

    int TCost;

    public static int code;
    AlertDialog alert;

    ArrayList<MedicineAuto> medicineAuto;

    AutoCompleteAdapter medicineAdapter;

    ArrayList<MenuItems> menuItems;

    Button choose_slot;

    String slot;

    Button showArea, save;

    int count;

    Dialog dialog_app_exit, dialog_placing_order;
    String list_code = "";

    JSONObject activityObject;
    RecyclerView ordered_medicines_list_rv;
    MedicineSearchAdapter medicineSearchAdapter;

    JSONObject jsonObject;

    EditText sales_code;
    private ImagesAdapter imagesAdapter;

    @Override
    public void onBackPressed() {
        dialog_app_exit = new Dialog(this);
        dialog_app_exit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_app_exit.setContentView(R.layout.dialog_exit_app);

        Button yes, no;
        yes = dialog_app_exit.findViewById(R.id.yes);
        no = dialog_app_exit.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_app_exit.dismiss();
            }
        });
        dialog_app_exit.show();
    }

    String token = "";
    private String page_no = "0";
    private String text = "";

    EditText search_medicine;
    private boolean isLoadingSearch, isScrolling;

    private ArrayList<Medicine> medicineResponses;
    private ArrayList<String> images;
    private RecyclerView images_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        isSlotChoosen = false;
        isLoadingSearch = false;
        isScrolling = false;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Paper.init(this);
        activityObject = new JSONObject();

        if (!MedicentoUtils.isMyServiceRunning(FetchMedicineService.class, this)) {
            Context context = getApplicationContext();
            Intent intent1;
            intent1 = new Intent(context, FetchMedicineService.class);
            context.startService(intent1);
        }

        Gson gson = new Gson();

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        } else {
            if (getIntent() != null) {
                Intent intent = getIntent();
                sp = (SalesPerson) intent.getSerializableExtra("user");
            }
        }

        images = new ArrayList<>();
        images.add("");
        images.add("");
        images.add("");
        images.add("");

        images_rv = findViewById(R.id.images_rv);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        images_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImagesAdapter(images, this, metrics);
        images_rv.setAdapter(imagesAdapter);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        token = task.getResult().getToken();

                        sentFirebaseToken();
                    }
                });

        progressBar = findViewById(R.id.progressBar);

        isLoading = false;

        email_update = findViewById(R.id.email);
        phone = findViewById(R.id.mobile);
        showArea = findViewById(R.id.shwarea);

        first_time = findViewById(R.id.first_time_login);

        cardView = findViewById(R.id.card_slot);

        dateSpinner = findViewById(R.id.day_spinner);
        slotSpinner = findViewById(R.id.slot_spinner);
        sales_code = findViewById(R.id.sales_code);
        search_medicine = findViewById(R.id.search_medicine);
        ordered_medicines_list_rv = findViewById(R.id.ordered_medicines_list_rv);
        message_us = findViewById(R.id.message_us);

        materialCardView = findViewById(R.id.card);
        save = findViewById(R.id.save);

        cart_sub = findViewById(R.id.tv2);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.mobile);
        order_date = findViewById(R.id.order_date);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        Date today;
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        today = calendar.getTime();
        slot1 = "Till 1PM";
        if (hour >= 8) {
            if (hour < 16) {
                slot1 = "Till 7PM";
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                today = calendar.getTime();
            }
        }
        date = dateFormat.format(today);
        order_date.setText("" + date + " " + slot1);

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

        medicineResponses = new ArrayList<>();
        medicineSearchAdapter = new MedicineSearchAdapter(this, medicineResponses);
        medicineSearchAdapter.setOnItemClicklistener(this);
        ordered_medicines_list_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ordered_medicines_list_rv.setAdapter(medicineSearchAdapter);
        ordered_medicines_list_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

        listView = findViewById(R.id.list_menu_items);

        setNavigationItem();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, ProfileNew.class);
                        intent.putExtra("usercode", usercode);
                        intent.putExtra("SalesPerson", sp);
                        startActivity(intent);
                        break;
                    case 1:
                        if (!amIConnect(MainActivity.this)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                            intent = new Intent(MainActivity.this, RecentOrderActivity.class);
                            intent.putExtra("SalesPerson", sp);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, PaymentSummaryActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, DidntFindMedicineActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, RetailerWebLogOut.class);
                        startActivity(intent);
                        break;
                    case 6:
                        clearUserDetails();
                        intent = new Intent(MainActivity.this, SignUpActivity.class);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        TCost = 0;

        if (sp != null) {
            addSalesPersonDetailsToNavDrawer();
            MedicineDataList = new ArrayList<>();
//            final String json = mSharedPreferences.getString("saved_medi", null);
//            Type type = new TypeToken<ArrayList<Medicine>>() {
//            }.getType();
//            MedicineDataList = gson.fromJson(json, type);
//            if (MedicineDataList == null) {
//                MedicineDataList = new ArrayList<>();
//                new GetNames().execute();
//            } else {
//                medicine1 = new ArrayList<>();
//                medicineAuto = new ArrayList<>();
//                for (Medicine med : MedicineDataList) {
//                    if (med.getPrice() != 0) {
//                        medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice(), med.getScheme(), med.getDiscount(), med.getOffer_qty(), med.getPacking()));
//                        medicine1.add(med.getMedicentoName());
//                    }
//                }
//
//                medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
//                mMedicineList.setAdapter(medicineAdapter);
//                mMedicineList.setEnabled(true);
//            }

            medicineAdapter = new AutoCompleteAdapter(this, MedicineDataList);
            mMedicineList.setAdapter(medicineAdapter);
            mMedicineList.setEnabled(true);

        } else {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());
        mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
        mOrderedMedicineAdapter.setOverallCostChangeListener(this);
        mOrderedMedicineAdapter.setContext(this);
        notify.setVisibility(View.GONE);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Notify", Toast.LENGTH_SHORT).show();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Filter.class);
                startActivityForResult(intent, 1);
            }
        });

        search_medicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ordered_medicines_list_rv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!isLoadingSearch && charSequence.toString().length() > 0) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Api api = retrofit.create(Api.class);
                    medicineResponses.clear();
                    isLoadingSearch = true;
                    page_no = "0";
                    text = charSequence.toString();

                    Call<GetMedicineResponse> getMedicineResponseCall = api.getMedicineResponseCall(charSequence.toString(), page_no);

                    getMedicineResponseCall.enqueue(new Callback<GetMedicineResponse>() {
                        @Override
                        public void onResponse(Call<GetMedicineResponse> call, retrofit2.Response<GetMedicineResponse> response) {

                            try {
                                GetMedicineResponse getMedicineResponse = response.body();

                                for (MedicineResponse medicineResponse : getMedicineResponse.getMedicineResponses()) {
                                    medicineResponses.add(new Medicine(medicineResponse.getItem_name(),
                                            medicineResponse.getManfc_name(),
                                            medicineResponse.getPtr(),
                                            medicineResponse.getId() + "",
                                            medicineResponse.getItem_code(),
                                            medicineResponse.getQty(),
                                            medicineResponse.getPacking(),
                                            medicineResponse.getMrp(),
                                            medicineResponse.getScheme(),
                                            medicineResponse.getDiscount(),
                                            medicineResponse.getOffer_qty()
                                    ));

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            medicineSearchAdapter.notifyDataSetChanged();

                            isLoadingSearch = false;
                            page_no = (Integer.parseInt(page_no) + 1) + "";
                            Log.d(TAG, "onResponse: " + response.body());
                        }

                        @Override
                        public void onFailure(Call<GetMedicineResponse> call, Throwable t) {
                            medicineSearchAdapter.notifyDataSetChanged();
                            isLoadingSearch = false;
                        }
                    });
                } else if (charSequence.toString().length() == 0) {
                    medicineResponses.clear();
                    page_no = "0";
                    text = "";
                    medicineSearchAdapter.notifyDataSetChanged();
                    ordered_medicines_list_rv.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mMedicineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View view1 = getWindow().getCurrentFocus();
                InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                mMedicineList.setText("");
                Medicine medicine = medicineAdapter.getItem(position);

                if (mOrderedMedicineAdapter.checkMedicineQuantity(medicine)) {
                    final Dialog dialog1 = new Dialog(MainActivity.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setContentView(R.layout.maximum_limit);

                    Button back1 = dialog1.findViewById(R.id.okay);
                    back1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });

                    dialog1.show();
                    return;
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
                        medicine.getOffer_qty(),
                        medicine.getDiscount(),
                        medicine.getOffer_qty()));
                float cost;
                try {
                    cost = Float.parseFloat(mTotalTv.getText().toString().substring(3));
                } catch (Exception e) {
                    cost = 0;
                    e.printStackTrace();
                }
                float overall = cost + medicine.getPrice();
                mTotalTv.setText(String.format("Rs.%.2f", overall));
                count += 1;
                setCount(MainActivity.this);
                Gson gson = new Gson();
                String json = gson.toJson(mOrderedMedicineAdapter.getList());
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
                mOrderedMedicinesListView.smoothScrollToPosition(0);
                saveOrder();
            }
        });

        mMedicineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMedicineList.showDropDown();
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
                setCount(MainActivity.this);
                Gson gson = new Gson();
                String json = gson.toJson(mOrderedMedicineAdapter.getList());
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
        }).attachToRecyclerView(mOrderedMedicinesListView);
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(200);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);

        if (getIntent() != null && getIntent().hasExtra("re_order_items")) {
            if (mOrderedMedicineAdapter != null) {

                gson = new Gson();
                String json = gson.toJson(mOrderedMedicineAdapter.getList());
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

                mOrderedMedicineAdapter.reset();

                ArrayList<OrderItem> orderItems = (ArrayList<OrderItem>) getIntent().getSerializableExtra("re_order_items");

                final String json1 = mSharedPreferences.getString("medicines_saved", null);
                Type type = new TypeToken<ArrayList<Medicine>>() {
                }.getType();
                if (json1 != null) {
                    MedicineDataList = new Gson().fromJson(json1, type);

                    for (OrderItem orderItem : orderItems) {
                        for (Medicine med : MedicineDataList) {
                            if (med.getMedicentoName().equals(orderItem.getName()) && med.getCompanyName().equals(orderItem.getCompany())) {
                                mOrderedMedicineAdapter.add(new OrderedMedicine(med.getMedicentoName(),
                                        med.getCompanyName(),
                                        orderItem.getQty(),
                                        med.getPrice(),
                                        med.getCode(),
                                        orderItem.getQty() * med.getPrice(),
                                        med.getMstock(),
                                        med.getPacking(),
                                        med.getMrp(),
                                        med.getScheme(),
                                        med.getDiscount(),
                                        med.getOffer_qty()));
                                float cost;
                                try {
                                    cost = Float.parseFloat(mTotalTv.getText().toString().substring(1));
                                } catch (Exception e) {
                                    cost = 0;
                                    e.printStackTrace();
                                }
                                float overall = cost + orderItem.getQty() * med.getPrice();
                                mTotalTv.setText(String.format("Rs.%.2f", overall));
                                count += 1;
                                setCount(MainActivity.this);

                                gson = new Gson();
                                json = gson.toJson(mOrderedMedicineAdapter.getList());
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
                                mOrderedMedicinesListView.smoothScrollToPosition(0);
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    private void sentFirebaseToken() {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String finalAndroidId = androidId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_user_info/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("android_id", finalAndroidId);
                params.put("reg_id", token);
                params.put("android_version", Constants.VERSION);
                params.put("manufacture_name", MedicentoUtils.getDeviceManufacture());
                params.put("model_name", getDeviceModel());
                params.put("user_ip", "");

                if (sp != null && sp.getmAllocatedPharmaId() != null) {
                    params.put("pharmacy_id", sp.getmAllocatedPharmaId());
                }

                return params;
            }
        };
        requestQueue.add(stringRequest);

        requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest1 = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/register_device/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dev_id", finalAndroidId);
                params.put("reg_id", token);
                return params;
            }
        };
        requestQueue.add(stringRequest1);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Welcome ", Toast.LENGTH_SHORT).show();
                SalesPerson sp = (SalesPerson) data.getSerializableExtra("salesperosn");
                mNavigationView = findViewById(R.id.nav_view);
                addSalesPersonDetailsToNavDrawer();
                Gson gson = new Gson();
                final String json = mSharedPreferences.getString("saved_medi", null);
                Type type = new TypeToken<ArrayList<Medicine>>() {
                }.getType();
                MedicineDataList = gson.fromJson(json, type);
                if (MedicineDataList == null) {
                    MedicineDataList = new ArrayList<>();
                    //     new GetNames().execute();
                } else {
                    medicine1 = new ArrayList<>();
                    medicineAuto = new ArrayList<>();
                    for (Medicine med : MedicineDataList) {
                        medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getPrice(), med.getScheme()));
                        medicine1.add(med.getMedicentoName());
                    }

                    medicineAdapter = new AutoCompleteAdapter(this, MedicineDataList);
                    mMedicineList.setAdapter(medicineAdapter);
                    mMedicineList.setEnabled(true);
                }

            } else {
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
                finish();
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

            LinearLayout linearLayout = findViewById(R.id.header_header);
            View headerView = mNavigationView.getHeaderView(0);
            TextView navHeaderSalesmanName = linearLayout.findViewById(R.id.username_header);
            TextView navHeaderSalesmanEmail = linearLayout.findViewById(R.id.user_email_header);
            TextView navp = linearLayout.findViewById(R.id.user_pharmaid);
            navHeaderSalesmanName.setText(sp.getName());
            navHeaderSalesmanEmail.setText(getString(R.string.pharmacode) + sp.getUsercode());
            navp.setText(sp.getName());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(sp.getName());
            }
            pharmacyName.setText(sp.getName());
        }
    }

    @Override
    public void onCostChanged(float newCost, int qty, String type) {
        Gson gson = new Gson();
        String json = gson.toJson(mOrderedMedicineAdapter.getList());
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
        setCount(MainActivity.this);
    }

    @Override
    public void onItemClick1(int position) {
        Medicine medicine = medicineResponses.get(position);

        if (mOrderedMedicineAdapter.checkMedicineQuantity(medicine)) {
            final Dialog dialog1 = new Dialog(MainActivity.this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.setContentView(R.layout.maximum_limit);

            Button back1 = dialog1.findViewById(R.id.okay);
            back1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });

            dialog1.show();

            medicineResponses.clear();
            medicineSearchAdapter.notifyDataSetChanged();
            return;
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
                medicine.getOffer_qty(),
                medicine.getDiscount(),
                medicine.getOffer_qty()));
        float cost;
        try {
            cost = Float.parseFloat(mTotalTv.getText().toString().substring(3));
        } catch (Exception e) {
            cost = 0;
            e.printStackTrace();
        }
        float overall = cost + medicine.getPrice();
        mTotalTv.setText(String.format("Rs.%.2f", overall));
        count += 1;
        setCount(MainActivity.this);
        Gson gson = new Gson();
        String json = gson.toJson(mOrderedMedicineAdapter.getList());
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
        mOrderedMedicinesListView.smoothScrollToPosition(0);
        medicineResponses.clear();
        medicineSearchAdapter.notifyDataSetChanged();
        ordered_medicines_list_rv.setVisibility(GONE);
        saveOrder();
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

            String jsonStr = sh.makeServiceCall(url + "?sp=" + sp.getmAllocatedPharmaId());

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray medicine = jsonObj.getJSONArray("products");
                    medicine1 = new ArrayList<>();
                    medicineAuto = new ArrayList<>();
                    MedicineDataList = new ArrayList<>();
                    for (int i = 0; i < medicine.length(); i++) {
                        JSONObject c = medicine.getJSONObject(i);
                        if (c.getInt("price") != 0) {
                            MedicineDataList.add(new Medicine(
                                    c.getString("medicento_name"),
                                    c.getString("company_name"),
                                    c.getInt("price"),
                                    c.getString("_id"),
                                    c.getString("item_code"),
                                    c.getInt("stock"),
                                    c.getString("packing"),
                                    c.getInt("price"),
                                    returnStringValueOfJsonKey(c, "scheme"),
                                    c.getString("discount"),
                                    c.getString("offer_qty")

                            ));
                            medicineAuto.add(new MedicineAuto(c.getString("medicento_name"),
                                    c.getString("company_name"),
                                    c.getInt("price"),
                                    returnStringValueOfJsonKey(c, "scheme"),
                                    c.getString("discount"),
                                    c.getString("offer_qty"),
                                    c.getString("packing")));
                            medicine1.add(c.getString("medicento_name"));
                        }
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
            materialCardView.setVisibility(View.INVISIBLE);

            if (medicineAuto != null) {
                medicineAdapter = new AutoCompleteAdapter(MainActivity.this, MedicineDataList);
                mMedicineList.setAdapter(medicineAdapter);
                mMedicineList.setEnabled(true);
            }
            Gson gson = new Gson();
            String json = gson.toJson(MedicineDataList);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("saved_medi", json);
            editor.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Now You Can Choose Medicine", Toast.LENGTH_SHORT).show();
                }
            });
            addSalesPersonDetailsToNavDrawer();
            isLoading = false;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        setCount(this);
        return true;
    }

    public void setCount(Context context) {
        try {
            if (!isSlotChoosen) {
                MenuItem menuItem = menu.findItem(R.id.action_proceed);
                LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

                CountDrawable badge;

                // Reuse drawable if possible
                Drawable reuse = icon.findDrawableByLayerId(R.id.counter);
                if (reuse != null && reuse instanceof CountDrawable) {
                    badge = (CountDrawable) reuse;
                } else {
                    badge = new CountDrawable(context);
                }

                badge.setCount(mOrderedMedicineAdapter.getItemCount() + "");
                icon.mutate();
                icon.setDrawableByLayerId(R.id.counter, badge);
            }

            cart_sub.setText("Cart Sub Total (" + mOrderedMedicineAdapter.getItemCount() + " Items ) : ");

            float price = 0;

            ArrayList<OrderedMedicine> medicines = mOrderedMedicineAdapter.getList();

            for (OrderedMedicine medicine : medicines) {
                price += medicine.getRate() * medicine.getQty();
            }
            mTotalTv.setText(String.format("Rs.%.2f", price));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String returnStringValueOfJsonKey(JSONObject jsonObject, String key) {
        if (jsonObject.has(key) && jsonObject.isNull(key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
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

            if (!amIConnect(MainActivity.this)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                return true;
            }
            if (mOrderedMedicineAdapter.getItemCount() == 0) {
                Toast.makeText(MainActivity.this, "Please Select Some Medicine", Toast.LENGTH_SHORT).show();
                return true;
            }

            slot = date + slot1;
            placeOrder();
        }
//        else if (id == R.id.action_notification) {
//            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
//        }
        return super.onOptionsItemSelected(item);
    }

    private void clearUserDetails() {
        Paper.book().delete("user");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            activityObject.put("end_time", System.currentTimeMillis() + "");
            activityObject.put("android_id", androidId);
            if (sp != null && sp.getmAllocatedPharmaId() != null) {
                activityObject.put("pharmacy_id", sp.getmAllocatedPharmaId());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/record_activity/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d(TAG, "getParams: " + activityObject.toString());
                try {
                    params.put("activity_name", activityObject.getString("activity_name"));
                    params.put("start_time", activityObject.getString("start_time"));
                    params.put("end_time", activityObject.getString("end_time"));
                    params.put("android_id", activityObject.getString("android_id"));
                    params.put("pharmacy_id", activityObject.getString("pharmacy_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);

        String cache = Paper.book().read("user");
        if (cache == null || cache.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            activityObject.put("activity_name", "PlaceOrder");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String cache = Paper.book().read("user");

        String code_cache = Paper.book().read("list_code");
        if (code_cache != null && !code_cache.isEmpty()) {
            list_code = code_cache;
        }

        if (cache == null || cache.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        } else {
            Gson gson = new Gson();
            final String json = mSharedPreferences.getString("saved_medicine_pharma", null);
            if (json == null) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(sp.getmAllocatedPharmaId(), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jsonObject = new JSONObject(json);
                    if (jsonObject.has(sp.getmAllocatedPharmaId())) {
                        try {
                            String data = jsonObject.getString(sp.getmAllocatedPharmaId());
                            Type type = new TypeToken<ArrayList<OrderedMedicine>>() {
                            }.getType();
                            ArrayList<OrderedMedicine> medicineDataList = gson.fromJson(data, type);
                            if (medicineDataList != null) {
                                if (mOrderedMedicineAdapter != null) {
                                    mOrderedMedicineAdapter.reset();
                                    for (OrderedMedicine orderedMedicine : medicineDataList) {
                                        mOrderedMedicineAdapter.addMedicines(orderedMedicine);
                                    }
                                    mOrderedMedicineAdapter.setOverallCostChangeListener(this);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            FirebaseMessaging.getInstance().subscribeToTopic("all");

            if (!amIConnect(MainActivity.this)) {
                alertDialogForInternet();
            } else {
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://stage.medicento.com:8080/api/app/get_app_versioncode_list_status/?id=" + sp.getmAllocatedPharmaId();
                Log.d(TAG, "onResume: " + url);
                StringRequest str = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d(TAG, "onResponse: " + response);
                                    JSONObject spo = new JSONObject(response);
                                    versionUpdate = JsonUtils.getStringValueFromJsonKey(spo, "app_version");

                                    String notifi = JsonUtils.getJsonValueFromKey(spo, "count");

                                    if (!versionUpdate.equals(Constants.VERSION)) {
                                        alertDialogForUpdate();
                                    }

//                                    try {
//                                        MenuItem menuItem = menu.findItem(R.id.action_notification);
//                                        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
//
//                                        CountDrawable badge;
//
//                                        // Reuse drawable if possible
//                                        Drawable reuse = icon.findDrawableByLayerId(R.id.counter);
//                                        if (reuse != null && reuse instanceof CountDrawable) {
//                                            badge = (CountDrawable) reuse;
//                                        } else {
//                                            badge = new CountDrawable(MainActivity.this);
//                                        }
//
//                                        badge.setCount(notifi);
//                                        icon.mutate();
//                                        icon.setDrawableByLayerId(R.id.counter, badge);
//                                    } catch (Exception e) {
//                                        Log.d(TAG, "onResponse: " + Log.getStackTraceString(e));
//                                        e.printStackTrace();
//                                    }

                                    String code_check = JsonUtils.getStringValueFromJsonKey(spo, "app_version");
                                    if (!code_check.equals(list_code)) {
                                        Paper.book().write("list_code", code_check);
                                        if (!isLoading) {
//                                            new GetNames().execute();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

    private boolean isPlacingOrder = false;

    private void placeOrder() {

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

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
                                Intent intent = new Intent(MainActivity.this, OrderConfirmed.class);

                                intent.putExtra("pharmacy", pharmacyName.getText().toString());

                                intent.putExtra("slots", slot);
                                intent.putExtra("orderDetails", mOrderedMedicineAdapter.getList());
                                intent.putExtra("TotalCost", mOrderedMedicineAdapter.getTotalCost());
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
                                Toast.makeText(MainActivity.this, "Order Cannot Be Placed Try Again.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Error In The Network.", Toast.LENGTH_SHORT).show();
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
                    for (OrderedMedicine orderedMedicine : mOrderedMedicineAdapter.getList()) {
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
                    if (!sales_code.getText().toString().isEmpty()) {
                        params.put("sales_code", sales_code.getText().toString());
                    }

                    params.put("date", date);
                    params.put("slot_time", slot1);
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
                    3,
                    2));
            requestQueue.add(stringRequest);
        }
    }

    private String order_id = "";

    private void saveOrder() {

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/save_upcoming_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);
                            JSONObject jsonObject = new JSONObject(response);

                            order_id = JsonUtils.getJsonValueFromKey(jsonObject, "order_data");
                            Paper.book().write("order_id", order_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error In The Network.", Toast.LENGTH_SHORT).show();
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
                    for (OrderedMedicine orderedMedicine : mOrderedMedicineAdapter.getList()) {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                3,
                2));
        requestQueue.add(stringRequest);
    }
}
