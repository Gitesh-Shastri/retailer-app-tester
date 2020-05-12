package com.medicento.retailerappmedi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Register;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.create_account.ConfirmAddressActivity;
import com.medicento.retailerappmedi.create_account.RegisterDistributorActivity;
import com.medicento.retailerappmedi.data.City;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.medicento.retailerappmedi.data.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class CreateNewAddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    EditText address, state, city, pincode, area;
    TextView name, message;
    Button submit;
    AlertDialog alert;
    ImageView back;
    ProgressBar progress_bar;
    SalesPerson sp;
    com.medicento.retailerappmedi.data.Address address_obj;
    private int LOCATION_PERMISSION = 100;
    GoogleApiClient googleApiClient;
    Location mLocation;
    LocationRequest locationRequest;
    Spinner state_spinner, city_spinner;
    ArrayList<City> city_list;
    ArrayList<State> state_list;
    RequestQueue requestQueue;
    State selected_state;
    City selected_city;
    int state_position = 0;
    int city_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_address);

        Paper.init(this);
        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        state_list = new ArrayList<>();
        city_list = new ArrayList<>();

        address = findViewById(R.id.address);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        back = findViewById(R.id.back);
        area = findViewById(R.id.area);
        city_spinner = findViewById(R.id.city_spinner);
        state_spinner = findViewById(R.id.state_spinner);
        pincode = findViewById(R.id.pincode);
        progress_bar = findViewById(R.id.progress_bar);
        name = findViewById(R.id.name);
        message = findViewById(R.id.message);
        submit = findViewById(R.id.submit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent() != null && getIntent().hasExtra("address_obj")) {

            address_obj = (com.medicento.retailerappmedi.data.Address) getIntent().getSerializableExtra("address_obj");

            address.setText(address_obj.getAddress());
            state.setText(address_obj.getState());
            city.setText(address_obj.getCity());
            name.setText(address_obj.getName());
            pincode.setText(address_obj.getPincode());
            area.setText(address_obj.getArea());

        } else {
            name.setText(sp.getName());
        }

        requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/area/state/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject state_obj = jsonArray.getJSONObject(i);

                                state_list.add(new State(JsonUtils.getJsonValueFromKey(state_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(state_obj, "id")));

                                if (JsonUtils.getJsonValueFromKey(state_obj, "name").equals(state.getText().toString())) {
                                    state_position = i;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setStateSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        requestQueue.add(stringRequest);

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mLocation = location;

                    Log.d("data", "onLocationChanged: " + location.getLatitude() + " " + location.getLatitude() + " ");

                    Geocoder geocoder = new Geocoder(CreateNewAddressActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Address obj = addresses.get(0);
                        String add = obj.getAddressLine(0);
                        add = add + "\n" + obj.getCountryName();
                        add = add + "\n" + obj.getCountryCode();
                        add = add + "\n" + obj.getAdminArea();
                        add = add + "\n" + obj.getPostalCode();
                        add = add + "\n" + obj.getSubAdminArea();
                        add = add + "\n" + obj.getLocality();
                        add = add + "\n" + obj.getSubThoroughfare();

                        if (city.getText().toString().isEmpty()) {
                            city.setText(obj.getSubAdminArea());
                        }
                        if (state.getText().toString().isEmpty()) {
                            state.setText(obj.getAdminArea());
                        }
                        pincode.setText(obj.getPostalCode());

                        Log.v("IGA", "Address" + add);
                        // Toast.makeText(this, "Address=>" + add,
                        // Toast.LENGTH_SHORT).show();

                        // TennisAppActivity.showDialog(add);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            CreateNewAddressActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
        } else {
            enableLoc();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setVisibility(View.GONE);
                message.setText("Please fill the mandatory fields");
                if (address.getText().toString().isEmpty()) {
                    address.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (state.getText().toString().isEmpty()) {
                    state.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (city.getText().toString().isEmpty()) {
                    city.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (pincode.getText().toString().isEmpty()) {
                    pincode.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (area.getText().toString().isEmpty()) {
                    area.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (message.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (!amIConnect(CreateNewAddressActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewAddressActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setIcon(R.mipmap.ic_launcher_new);
                    builder.setCancelable(false);
                    builder.setMessage("Please Connect To The Internet")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    submit.performClick();
                                    alert.dismiss();
                                }
                            });
                    alert = builder.create();
                    alert.show();
                } else {
                    sendDetailsToServer();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setStateSpinner() {

        HintAdapter<State> statehintAdapter = new HintAdapter<State>(
                CreateNewAddressActivity.this,
                R.layout.spinner_item,
                "Select State",
                state_list) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(state_list.get(position).getName());
                return view;
            }
        };

        HintSpinner<State> hintSpinner = new HintSpinner<>(
                state_spinner,
                statehintAdapter,
                new HintSpinner.Callback<State>() {
                    @Override
                    public void onItemSelected(int position, State itemAtPosition) {
                        selected_state = itemAtPosition;
                        state.setText(selected_state.getName());
                        fetchCity(selected_state);
                    }
                });
        hintSpinner.init();
    }

    public void fetchCity(State state) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/area/city/?state="+state.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0;i<jsonArray.length();i++) {
                                JSONObject city_obj = jsonArray.getJSONObject(i);

                                city_list.add(new City(JsonUtils.getJsonValueFromKey(city_obj, "name"),
                                        JsonUtils.getJsonValueFromKey(city_obj, "id")));

                                if (JsonUtils.getJsonValueFromKey(city_obj, "name").equals(city.getText().toString())) {
                                    city_position = i;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setCitySpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        }
                }
        );
        if (city_list != null) {
            city_list = new ArrayList<>();
        } else {
            city_list.clear();
        }
        requestQueue.add(stringRequest);
    }

    private void setCitySpinner() {
        HintAdapter<City> statehintAdapter = new HintAdapter<City>(
                CreateNewAddressActivity.this,
                R.layout.spinner_item,
                "Select City",
                city_list) {

            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {

                View view = inflateLayout(parent, false);
                ((TextView) view.findViewById(R.id.text)).setText(city_list.get(position).getName());
                return view;
            }
        };

        HintSpinner<City> hintSpinner = new HintSpinner<>(
                city_spinner,
                statehintAdapter,
                new HintSpinner.Callback<City>() {
                    @Override
                    public void onItemSelected(int position, City itemAtPosition) {
                        selected_city = itemAtPosition;
                        city.setText(selected_city.getName());
                    }
                });
        hintSpinner.init();
    }

    private void getAreaName() {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/area/get_area_from_address/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject results = new JSONObject(response);
                            area.setText(results.getString("results"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error == null || error.networkResponse == null) {
                                Log.d("error_message", "showVolleyError: no Response");
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("error_message", "showVolleyError: " + body);
                            } catch (UnsupportedEncodingException e) {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String address_line = address.getText().toString();
                params.put("address", address_line);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendDetailsToServer() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_and_edit_pharmacy_address/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sp.setAddress(address.getText().toString());
                        Paper.book().write("user", new Gson().toJson(sp));
                        progress_bar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(CreateNewAddressActivity.this, "Error in connecting to server. Please try again after some time", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("area", area.getText().toString());
                params.put("state", state.getText().toString());
                params.put("city", city.getText().toString());
                params.put("address", address.getText().toString());
                params.put("pin_code", pincode.getText().toString());
                params.put("pharmacy_id", sp.getId());
                params.put("code", sp.getUsercode());
                if (address_obj != null) {
                    params.put("id", address_obj.getId());
                }

                Log.d("data", "getParams: " + params.toString());
                return params;
            }
        };

        progress_bar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                0,
                2));
        requestQueue.add(stringRequest);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(CreateNewAddressActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(CreateNewAddressActivity.this).build();
            googleApiClient.connect();
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(CreateNewAddressActivity.this, 101);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            CreateNewAddressActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;

        Log.d("data", "onLocationChanged: " + location.getLatitude() + " " + location.getLatitude() + " ");

        Geocoder geocoder = new Geocoder(CreateNewAddressActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            if (city.getText().toString().isEmpty()) {
                city.setText(obj.getSubAdminArea());
            }
            if (state.getText().toString().isEmpty()) {
                state.setText(obj.getAdminArea());
            }
            pincode.setText(obj.getPostalCode());

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLoc();
//                buildGoogleApiClient();
            }
        }
    }
}
