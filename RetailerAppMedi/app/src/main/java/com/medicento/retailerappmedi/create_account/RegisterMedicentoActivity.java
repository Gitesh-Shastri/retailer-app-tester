package com.medicento.retailerappmedi.create_account;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterMedicentoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    String tag = "Pharmacy";
    GoogleApiClient googleApiClient;
    Location mLocation;
    LocationRequest locationRequest;
    EditText name_et, number, gst, drug;
    Button sign_in_btn;
    TextView message;
    private int LOCATION_PERMISSION = 100;
    AlertDialog alert;
    String city, state, address, pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_medicento);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        name_et = findViewById(R.id.name_et);
        number = findViewById(R.id.number);
        message = findViewById(R.id.message);
        sign_in_btn = findViewById(R.id.sign_in_btn);
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

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message.setVisibility(View.GONE);

                number.setBackgroundResource(R.drawable.border_grey);
                name_et.setBackgroundResource(R.drawable.border_grey);
                gst.setBackgroundResource(R.drawable.border_grey);
                drug.setBackgroundResource(R.drawable.border_grey);

                message.setText("Please fill the mandatory fields");
                if (name_et.getText().toString().isEmpty()) {
                    name_et.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (number.getText().toString().isEmpty()) {
                    number.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (drug.getText().toString().isEmpty()) {
                    drug.setBackgroundResource(R.drawable.border_outline_red);
                    message.setVisibility(View.VISIBLE);
                }
                if (message.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (!amIConnect(RegisterMedicentoActivity.this)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterMedicentoActivity.this);
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
                    Intent intent = new Intent(RegisterMedicentoActivity.this, ConfirmAddressActivity.class);
                    intent.putExtra("number", number.getText().toString());
                    intent.putExtra("gst", gst.getText().toString());
                    intent.putExtra("drug", drug.getText().toString());
                    intent.putExtra("name", name_et.getText().toString());
                    intent.putExtra("pincode", pincode);
                    startActivity(intent);
                }
            }
        });

    }


    public void turnGPSOn() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.sendBroadcast(intent);

        String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);


        }
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;

        Log.d("data", "onLocationChanged: " + location.getLatitude() + " " + location.getLatitude() + " ");

        Geocoder geocoder = new Geocoder(RegisterMedicentoActivity.this, Locale.getDefault());
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
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(300000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            RegisterMedicentoActivity.this,
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(RegisterMedicentoActivity.this)
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
                            status.startResolutionForResult(RegisterMedicentoActivity.this, 101);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });

    }
}
