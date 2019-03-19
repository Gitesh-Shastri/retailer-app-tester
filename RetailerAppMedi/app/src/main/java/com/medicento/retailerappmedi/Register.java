package com.medicento.retailerappmedi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medicento.retailerappmedi.data.Area;
import com.medicento.retailerappmedi.data.Constants;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class Register extends AppCompatActivity {

    EditText shop_name, shop_address, pin_code, owner_name, phone_number, email, drug_license, gst_license;
    Spinner area_spinner, state_spinner, city_spinner;

    RelativeLayout relativeLayout;
    Snackbar snackbar;

    ProgressDialog progressDialog;

    ArrayList<Area> areas;
    ArrayList<String> state, city;

    String id;

    Button create, terms_of_service, login, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relativeLayout = findViewById(R.id.relative);

        areas = new ArrayList<>();

        shop_name = findViewById(R.id.shop_name);
        shop_address = findViewById(R.id.shop_address);
        pin_code = findViewById(R.id.owner_pincode);
        owner_name = findViewById(R.id.owner_name);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        drug_license = findViewById(R.id.drug_license);
        gst_license = findViewById(R.id.gst_license);
        login = findViewById(R.id.login);
        state_spinner = findViewById(R.id.state_spinner);
        city_spinner = findViewById(R.id.city_spinner);


        state = new ArrayList<>();
        city = new ArrayList<>();

        getArea();

        hideSoftKeyboard();

        terms_of_service = findViewById(R.id.termsOfService);
        area_spinner = findViewById(R.id.area_spinner);
        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shop_name.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Name", Snackbar.LENGTH_SHORT).show();
                } else if (shop_address.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Shop Adress", Snackbar.LENGTH_SHORT).show();
                } else if (phone_number.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Phone Number", Snackbar.LENGTH_SHORT).show();
                } else if (drug_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Drug License", Snackbar.LENGTH_SHORT).show();
                } else if (gst_license.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter GST License", Snackbar.LENGTH_SHORT).show();
                } else {
                    sendDetailsToServer();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, SignUpActivity.class));
                finish();
            }
        });

        terms_of_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, TermsAndCondition.class));
            }
        });
    }

    private void sendDetailsToServer() {

        snackbar = Snackbar.make(relativeLayout, "Please Wait Creating Account", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.CREATE_ACCOUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Register", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if (message.equals("user created !")) {
                                String code = jsonObject.getString("code");

                                final Dialog dialog1 = new Dialog(Register.this);
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog1.setContentView(R.layout.congo_registered);

                                TextView phone = dialog1.findViewById(R.id.phone);
                                phone.setText("  Your Registered Mobile Number : " + phone_number.getText().toString());

                                TextView email1 = dialog1.findViewById(R.id.email);
                                email1.setText("  Your Registered Email Address : " + email.getText().toString());

                                TextView code1 = dialog1.findViewById(R.id.code);
                                code1.setText("Your PharmaCode : " + code);

                                Button back1 = dialog1.findViewById(R.id.back);
                                back1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog1.dismiss();
                                        startActivity(new Intent(Register.this, SignUpActivity.class));
                                        finish();
                                    }
                                });

                                dialog1.show();

                            }
                            Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(relativeLayout, "Something Went Wrong !" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(relativeLayout, "Something Went Wrong !", Snackbar.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("pharma_name", shop_name.getText().toString());
                params.put("area_id", id);
                params.put("pharma_address", shop_address.getText().toString());
                params.put("gst", gst_license.getText().toString());
                params.put("drug", drug_license.getText().toString());
                params.put("email", email.getText().toString());
                params.put("phone", phone_number.getText().toString());
                params.put("name", owner_name.getText().toString());
                params.put("pincode", pin_code.getText().toString());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void getArea() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

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
                            final ArrayList<Area> tempareas = new ArrayList<>();
                            state.add(areas.get(0).getState());
                            city.add(areas.get(0).getCity());
                            tempareas.add(new Area("Area Not Available",
                                    "0",
                                    "none",
                                    "none"));
                            tempareas.add(areas.get(0));
                            for (Area area : areas) {
                                if (!state.contains(area.getState())) {
                                    state.add(area.getState());
                                }
                                if (area.getState().equals(areas.get(1).getState())) {
                                    if (!city.contains(area.getCity())) {
                                        city.add(area.getCity());
                                    }
                                    if (!tempareas.get(1).getName().equals(area.getName())) {
                                        if (city.contains(area.getCity())) {
                                            tempareas.add(area);
                                        }
                                    }
                                }
                            }
                            Collections.sort(state, String.CASE_INSENSITIVE_ORDER);
                            HintAdapter<String> statehintAdapter = new HintAdapter<String>(
                                    Register.this,
                                    R.layout.spinner_item,
                                    "Select State",
                                    state) {

                                @Override
                                protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                    final String area = getItem(position);
                                    final String name = area;

                                    // Here you inflate the layout and set the value of your widgets
                                    View view = inflateLayout(parent, false);
                                    ((TextView) view.findViewById(R.id.text)).setText(name);
                                    return view;
                                }
                            };


                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    state_spinner,
                                    // Default layout - You don't need to pass in any layout id, just your hint text and
                                    // your list data
                                    statehintAdapter,
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            city.clear();
                                            for (Area area : areas) {
                                                if (area.getState().equals(itemAtPosition)) {
                                                    if (!city.contains(area.getCity())) {
                                                        city.add(area.getCity());
                                                    }
                                                }
                                            }

                                            Collections.sort(city, String.CASE_INSENSITIVE_ORDER);

                                            HintAdapter<String> cityhintAdapter = new HintAdapter<String>(
                                                    Register.this,
                                                    R.layout.spinner_item,
                                                    "Select City",
                                                    city) {

                                                @Override
                                                protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                                    final String area = getItem(position);
                                                    final String name = area;

                                                    // Here you inflate the layout and set the value of your widgets
                                                    View view = inflateLayout(parent, false);
                                                    ((TextView) view.findViewById(R.id.text)).setText(name);
                                                    return view;
                                                }
                                            };

                                            HintSpinner<String> cityhintSpinner = new HintSpinner<>(
                                                    city_spinner,
                                                    // Default layout - You don't need to pass in any layout id, just your hint text and
                                                    // your list data
                                                    cityhintAdapter,
                                                    new HintSpinner.Callback<String>() {
                                                        @Override
                                                        public void onItemSelected(int position, String itemAtPosition) {
                                                            ArrayList<Area> tempareas1 = new ArrayList<>();
                                                            tempareas1.add(new Area("Area Not Available",
                                                                    "0",
                                                                    "none",
                                                                    "none"));
                                                            for (Area area : areas) {
                                                                if (area.getCity().equals(itemAtPosition)) {
                                                                    if (!tempareas1.get(tempareas1.size() - 1).getName().equals(area.getName())) {
                                                                        tempareas1.add(area);
                                                                    }
                                                                }
                                                            }

                                                            if (!tempareas1.isEmpty()) {
                                                                Collections.sort(tempareas1, new Comparator<Area>() {
                                                                    @Override
                                                                    public int compare(Area c1, Area c2) {
                                                                        //You should ensure that list doesn't contain null values!
                                                                        return c1.getName().compareTo(c2.getName());
                                                                    }
                                                                });
                                                            }
                                                            HintAdapter<Area> hintAdapter = new HintAdapter<Area>(
                                                                    Register.this,
                                                                    R.layout.spinner_item,
                                                                    "Select Area",
                                                                    tempareas1) {

                                                                @Override
                                                                protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                                                    final Area area = getItem(position);
                                                                    final String name = area.getName();

                                                                    // Here you inflate the layout and set the value of your widgets
                                                                    View view = inflateLayout(parent, false);
                                                                    ((TextView) view.findViewById(R.id.text)).setText(name);
                                                                    return view;
                                                                }
                                                            };

                                                            HintSpinner<Area> areahintSpinner = new HintSpinner<>(
                                                                    area_spinner,
                                                                    hintAdapter,
                                                                    new HintSpinner.Callback<Area>() {
                                                                        @Override
                                                                        public void onItemSelected(int position, Area itemAtPosition) {
                                                                            id = itemAtPosition.getId();
                                                                        }
                                                                    });
                                                            areahintSpinner.init();
                                                    }
                                        });
                                            cityhintSpinner.init();
                                            Toast.makeText(Register .this,itemAtPosition,Toast.LENGTH_SHORT).

                                        show();
                                    }
                        });
                        hintSpinner.init();
                           /* ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>( Register.this,
                                    R.layout.support_simple_spinner_dropdown_item,
                                    state);
                            state_spinner.setAdapter(stateAdapter);
*/
                        HintAdapter<String> cityhintAdapter = new HintAdapter<String>(
                                Register.this,
                                R.layout.spinner_item,
                                "Select City",
                                city) {

                            @Override
                            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                final String area = getItem(position);
                                final String name = area;

                                // Here you inflate the layout and set the value of your widgets
                                View view = inflateLayout(parent, false);
                                ((TextView) view.findViewById(R.id.text)).setText(name);
                                return view;
                            }
                        };

                        HintSpinner<String> cityhintSpinner = new HintSpinner<>(
                                city_spinner,
                                // Default layout - You don't need to pass in any layout id, just your hint text and
                                // your list data
                                cityhintAdapter,
                                new HintSpinner.Callback<String>() {
                                    @Override
                                    public void onItemSelected(int position, String itemAtPosition) {
                                        ArrayList<Area> tempareas1 = new ArrayList<>();
                                        tempareas1.add(new Area("Area Not Available",
                                                "0",
                                                "none",
                                                "none"));
                                        for (Area area : areas) {
                                            if (area.getCity().equals(itemAtPosition)) {
                                                if (!tempareas1.get(tempareas1.size() - 1).getName().equals(area.getName())) {
                                                    tempareas1.add(area);
                                                }
                                            }
                                        }

                                        if (!tempareas1.isEmpty()) {
                                            Collections.sort(tempareas1, new Comparator<Area>() {
                                                @Override
                                                public int compare(Area c1, Area c2) {
                                                    //You should ensure that list doesn't contain null values!
                                                    return c1.getName().compareTo(c2.getName());
                                                }
                                            });
                                        }
                                        HintAdapter<Area> hintAdapter = new HintAdapter<Area>(
                                                Register.this,
                                                R.layout.spinner_item,
                                                "Select Area",
                                                tempareas1) {

                                            @Override
                                            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                                final Area area = getItem(position);
                                                final String name = area.getName();

                                                // Here you inflate the layout and set the value of your widgets
                                                View view = inflateLayout(parent, false);
                                                ((TextView) view.findViewById(R.id.text)).setText(name);
                                                return view;
                                            }
                                        };

                                        HintSpinner<Area> areahintSpinner = new HintSpinner<>(
                                                area_spinner,
                                                hintAdapter,
                                                new HintSpinner.Callback<Area>() {
                                                    @Override
                                                    public void onItemSelected(int position, Area itemAtPosition) {
                                                        id = itemAtPosition.getId();
                                                    }
                                                });
                                        areahintSpinner.init();

                                    }
                                });
                        cityhintSpinner.init();

                           /* ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>( Register.this,
                                    R.layout.support_simple_spinner_dropdown_item,
                                    city);
                            city_spinner.setAdapter(cityAdapter);
                           */
                        HintAdapter<Area> hintAdapter = new HintAdapter<Area>(
                                Register.this,
                                R.layout.spinner_item,
                                "Select Area",
                                tempareas) {

                            @Override
                            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                                final Area area = getItem(position);
                                final String name = area.getName();

                                // Here you inflate the layout and set the value of your widgets
                                View view = inflateLayout(parent, false);
                                ((TextView) view.findViewById(R.id.text)).setText(name);
                                return view;
                            }
                        };

                        HintSpinner<Area> areahintSpinner = new HintSpinner<>(
                                area_spinner,
                                hintAdapter,
                                new HintSpinner.Callback<Area>() {
                                    @Override
                                    public void onItemSelected(int position, Area itemAtPosition) {
                                        id = itemAtPosition.getId();
                                    }
                                });
                        areahintSpinner.init();
                    } catch(
                    JSONException e)

                    {

                        e.printStackTrace();
                        Snackbar.make(relativeLayout, "Something Went Wrong !" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
    },
            new Response.ErrorListener()

    {
        @Override
        public void onErrorResponse (VolleyError error){

        Snackbar.make(relativeLayout, "Something Went Wrong !", Snackbar.LENGTH_SHORT).show();
    }
    }
        );
        requestQueue.add(stringRequest);

}


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
