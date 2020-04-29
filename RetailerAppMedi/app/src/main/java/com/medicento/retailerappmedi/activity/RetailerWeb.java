package com.medicento.retailerappmedi.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.SalesPerson;

import java.io.IOException;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import io.paperdb.Paper;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class RetailerWeb extends AppCompatActivity implements ZXingScannerView.ResultHandler , BarcodeReader.BarcodeReaderListener{

    private ZXingScannerView scan;
    private TextView text_result;

    SalesPerson sp;

    SurfaceView cameraPreview;
    TextView textResult;

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    int RequestCameraPermisionID = 1001;

    BarcodeReader barcodeReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_web);

        Paper.init(this);

        Gson gson = new Gson();

        final String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }


        cameraPreview = findViewById(R.id.cameraPreview);
        textResult = findViewById(R.id.textResult);
        

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                int camera = ContextCompat.checkSelfPermission(RetailerWeb.this, Manifest.permission.CAMERA);
                if(camera != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(RetailerWeb.this).setMessage("You need to enable permissions to use this feature. \nGo To Permissions > Enable All Permissions").setPositiveButton("Enable Permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();
                    return;
                }

                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0) {
                    textResult.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            textResult.setText(qrcodes.valueAt(0).displayValue);

                            RequestQueue requestQueue = Volley.newRequestQueue(RetailerWeb.this);
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.GET,
                                    "http://stage.medicento.com:8080/pharmacy/update_pharmacy/?code=" + textResult.getText() + "&pharmacy_id=" + sp.getmAllocatedPharmaId(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            Paper.book().write("retailer_code", textResult.getText());

                                            Intent intent = new Intent(RetailerWeb.this, RetailerWebLogOut.class);
                                            startActivity(intent);

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            showVolleyError(error);
                                        }
                                    }
                            );

                            requestQueue.add(stringRequest);
                        }
                    });
                }
            }
        });

//        scan = findViewById(R.id.scan);
//        text_result = findViewById(R.id.text_result);

//        scan.setResultHandler(RetailerWeb.this);
//        scan.startCamera();
    }

    @Override
    protected void onPause() {
        //scan.stopCamera();
        super.onPause();
    }

    @Override
    public void handleResult(Result rawResult) {
        text_result.setText(rawResult.getText());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/pharmacy/update_pharmacy/?code=" + text_result.getText() + "&pharmacy_id=" + sp.getmAllocatedPharmaId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retailerweb", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    @Override
    public void onScanned(Barcode barcode) {
        text_result.setText(barcode.displayValue);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/pharmacy/update_pharmacy/?code=" + text_result.getText() + "&pharmacy_id=" + sp.getmAllocatedPharmaId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retailerweb", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(final SparseArray<Barcode> sparseArray) {
        if (sparseArray.size() != 0) {
            textResult.post(new Runnable() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    textResult.setText(sparseArray.valueAt(0).displayValue);
                }
            });
        }

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }
}
