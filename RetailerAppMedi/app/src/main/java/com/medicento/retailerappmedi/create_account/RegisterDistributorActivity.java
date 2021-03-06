package com.medicento.retailerappmedi.create_account;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.medicento.retailerappmedi.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;

public class RegisterDistributorActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    EditText name_et, number, gst, drug, fssai, trade;
    TextView message, drug_license, trade_license, message_1, message_2, message_3, message_4;
    Button sign_in_btn;
    AlertDialog alert;
    String city, state, address, pincode;
    GoogleApiClient googleApiClient;
    Location mLocation;
    LocationRequest locationRequest;
    private int LOCATION_PERMISSION = 100;
    RadioButton distributor, trader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_distributor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        name_et = findViewById(R.id.name_et);
        message_1 = findViewById(R.id.message_1);
        message_2 = findViewById(R.id.message_2);
        message_3 = findViewById(R.id.message_3);
        message_4 = findViewById(R.id.message_4);
        fssai = findViewById(R.id.fssai);
        trade = findViewById(R.id.trade);
        number = findViewById(R.id.number);
        message = findViewById(R.id.message);
        drug_license = findViewById(R.id.drug_license);
        trade_license = findViewById(R.id.trade_license);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        distributor = findViewById(R.id.distributor);
        trader = findViewById(R.id.trader);
        gst = findViewById(R.id.gst);
        drug = findViewById(R.id.drug);

        message.setVisibility(View.GONE);

        name_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name_et.setBackgroundResource(R.drawable.border_grey);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        trade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trade.setBackgroundResource(R.drawable.border_grey);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        fssai.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fssai.setBackgroundResource(R.drawable.border_grey);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        drug.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                drug.setBackgroundResource(R.drawable.border_grey);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        gst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                gst.setBackgroundResource(R.drawable.border_grey);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                number.setBackgroundResource(R.drawable.border_grey);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        distributor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    message.setVisibility(View.GONE);
                }
            }
        });

        trader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    message.setVisibility(View.GONE);
                }
            }
        });


        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message.setVisibility(View.GONE);
                message_1.setVisibility(View.GONE);
                message_2.setVisibility(View.GONE);
                message_3.setVisibility(View.GONE);
                message_4.setVisibility(View.GONE);

                number.setBackgroundResource(R.drawable.border_grey);
                name_et.setBackgroundResource(R.drawable.border_grey);
                gst.setBackgroundResource(R.drawable.border_grey);
                drug.setBackgroundResource(R.drawable.border_grey);
                trade.setBackgroundResource(R.drawable.border_grey);
                fssai.setBackgroundResource(R.drawable.border_grey);

                message.setText("Please fill the mandatory field");
                message_2.setText("Please fill the mandatory field");
                message_3.setText("Please fill the mandatory field");
                message_4.setText("Please fill the mandatory field");

                if (!distributor.isChecked() && !trader.isChecked()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        trader.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        distributor.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    }
                    message.setVisibility(View.VISIBLE);
                    message.setText("Please Select one option");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                trader.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                distributor.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            }
                        }
                    }, 2000);

                } else {
                    if (name_et.getText().toString().isEmpty()) {
                        name_et.setBackgroundResource(R.drawable.border_outline_red);
                        message.setVisibility(View.VISIBLE);
                    }
                    if (number.getText().toString().isEmpty()) {
                        number.setBackgroundResource(R.drawable.border_outline_red);
                        message.setVisibility(View.VISIBLE);
                    }
                    if (gst.getText().toString().isEmpty()) {
                        gst.setBackgroundResource(R.drawable.border_outline_red);
                        message.setVisibility(View.VISIBLE);
                    }
                }

                if (message.getVisibility() == View.VISIBLE || message_1.getVisibility() == View.VISIBLE ||
                        message_2.getVisibility() == View.VISIBLE || message_3.getVisibility() == View.VISIBLE ||
                        message_4.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (!amIConnect(RegisterDistributorActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterDistributorActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setIcon(R.mipmap.ic_launcher_new);
                    builder.setCancelable(false);
                    builder.setMessage("Please Connect To The Internet")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sign_in_btn.performClick();
                                    alert.dismiss();
                                }
                            });
                    alert = builder.create();
                    alert.show();
                } else {
                    Intent intent = new Intent(RegisterDistributorActivity.this, ConfirmAddressDistributorActivity.class);
                    intent.putExtra("state", state);
                    intent.putExtra("city", city);
                    intent.putExtra("number", number.getText().toString());
                    intent.putExtra("gst", gst.getText().toString());
                    intent.putExtra("trade", trade.getText().toString());
                    intent.putExtra("fssai", fssai.getText().toString());
                    intent.putExtra("drug", drug.getText().toString());
                    intent.putExtra("address", address);
                    intent.putExtra("name", name_et.getText().toString());
                    intent.putExtra("pincode", pincode);
                    startActivity(intent);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            RegisterDistributorActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION);
        } else {
            enableLoc();
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(RegisterDistributorActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this).build();
            googleApiClient.connect();
        }

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
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
                            status.startResolutionForResult(RegisterDistributorActivity.this, 101);
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
        locationRequest.setInterval(300000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            RegisterDistributorActivity.this,
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

        Geocoder geocoder = new Geocoder(RegisterDistributorActivity.this, Locale.getDefault());
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

            city = obj.getAdminArea();
            state = obj.getSubAdminArea();
            address = obj.getAddressLine(0);
            pincode = obj.getPostalCode();

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
