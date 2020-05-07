package com.medicento.retailerappmedi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.CartPageActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.adapter.FullStcokImageAdapter;
import com.medicento.retailerappmedi.adapter.ImagesAdapter;
import com.medicento.retailerappmedi.adapter.ItemCartList;
import com.medicento.retailerappmedi.adapter.StcokImageAdapter;
import com.medicento.retailerappmedi.data.EssentialList;
import com.medicento.retailerappmedi.data.SalesPerson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class UploadPurchaseActivity extends AppCompatActivity implements PaymentResultListener, Player.EventListener, FullStcokImageAdapter.setOnClickListener {

    ImageView back, close, image;
    CardView image_card_view;
    RelativeLayout stocks, upper;
    LinearLayout upload_purchase, performa_invoice, download_ll, text_seek_bar;
    SeekBar seek_bar;
    TextView title, per_total_amount, per_advance_amount, per_remaining_amount, lr_total_amount, lr_advance_amount, lr_remaining_amount, view_text;
    RecyclerView stock_images_rv, stock_images_full_screen_rv;
    StcokImageAdapter imagesAdapter;
    FullStcokImageAdapter fullStcokImageAdapter;
    private ArrayList<String> urls;
    private ArrayList<EssentialList> essentialLists;
    Button view_performa, download_performa, download_lr, view_lr, confirm_to_upload, review, proceed_to_50, proceed_to_50_remain, view_on_web;
    String performa_url = "", lr_url = "";
    final int REQUEST_PERMISSION_CODE = 1000;
    ProgressBar progressBar;
    float gst = 0;

    com.github.barteksc.pdfviewer.PDFView pdfViewer;
    boolean isFromDownload, isFromLr, isPdfVisible;

    int price = 0, currentWindow = 0;
    long playbackPosition = 0;;
    SalesPerson sp;
    boolean isPaused, playWhenReady;
    String video_url;
    private PlayerView videoView;
    private SimpleExoPlayer player;
    private ImageView fullscreenButton;
    boolean fullscreen;
    RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_purchase);

        per_total_amount = findViewById(R.id.per_total_amount);
        per_advance_amount = findViewById(R.id.per_advance_amount);
        per_remaining_amount = findViewById(R.id.per_remaining_amount);
        lr_total_amount = findViewById(R.id.lr_total_amount);
        lr_advance_amount = findViewById(R.id.lr_advance_amount);
        proceed_to_50_remain = findViewById(R.id.proceed_to_50_remain);
        lr_remaining_amount = findViewById(R.id.lr_remaining_amount);
        videoView = findViewById(R.id.videoView);
        back = findViewById(R.id.back);
        group = findViewById(R.id.group);
        close = findViewById(R.id.close);
        title = findViewById(R.id.title);
        stocks = findViewById(R.id.stocks);
        view_lr = findViewById(R.id.view_lr);
        seek_bar = findViewById(R.id.seek_bar);
        upper = findViewById(R.id.upper);
        review = findViewById(R.id.review);
        image = findViewById(R.id.image);
        view_text = findViewById(R.id.view_text);
        progressBar = findViewById(R.id.progressBar);
        download_lr = findViewById(R.id.download_lr);
        proceed_to_50 = findViewById(R.id.proceed_to_50);
        text_seek_bar = findViewById(R.id.text_seek_bar);
        confirm_to_upload = findViewById(R.id.confirm_to_upload);
        upload_purchase = findViewById(R.id.upload_purchase);
        performa_invoice = findViewById(R.id.performa_invoice);
        view_on_web = findViewById(R.id.view_on_web);
        image_card_view = findViewById(R.id.image_card_view);
        stock_images_full_screen_rv = findViewById(R.id.stock_images_full_screen_rv);
        stock_images_rv = findViewById(R.id.stock_images_rv);
        download_ll = findViewById(R.id.download_ll);
        pdfViewer = findViewById(R.id.pdfViewer);

        view_performa = findViewById(R.id.view_performa);
        download_performa = findViewById(R.id.download_performa);

        Paper.init(this);

        Gson gson = new Gson();
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            sp = gson.fromJson(cache, SalesPerson.class);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        essentialLists = new ArrayList<>();


        String essential_saved = Paper.book().read("essential_saved_json");
        if (essential_saved != null && !essential_saved.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(essential_saved);
                String data = jsonObject.getString(sp.getUsercode());
                Type type = new TypeToken<ArrayList<EssentialList>>() {
                }.getType();
                essentialLists = new Gson().fromJson(data, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (EssentialList essentialList : essentialLists) {
            if (essentialList.getQty() > 0) {
                price += essentialList.getCost() * essentialList.getQty();
                gst += (essentialList.getCost() * essentialList.getQty()*essentialList.getDiscount()*0.01);
            }
        }

        per_total_amount.setText(String.format("₹ %.2f", (price + gst)));
        per_remaining_amount.setText("₹ " + (int)(price/2));
        lr_total_amount.setText(String.format("₹ %.2f", (price + gst)));
        if (price%2==0) {
            per_advance_amount.setText(String.format("₹ %.2f", ((int) (price / 2) + gst)));
            lr_advance_amount.setText(String.format("₹ %.2f",((int) (price / 2) + gst)));
        } else {
            per_advance_amount.setText(String.format("₹ %.2f", ((int) ((price / 2)+1) + gst)));
            lr_advance_amount.setText(String.format("₹ %.2f", ((int) ((price / 2)+1) + gst)));
        }
        lr_remaining_amount.setText("₹ " + (int)(price/2));

        urls = new ArrayList<>();

        imagesAdapter = new StcokImageAdapter(urls, this);
        stock_images_rv.setLayoutManager(new GridLayoutManager(this, 4));
        stock_images_rv.setAdapter(imagesAdapter);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                seek_bar.setProgress(i-1);
            }
        });

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                stocks.setVisibility(View.GONE);
                upload_purchase.setVisibility(View.GONE);
                performa_invoice.setVisibility(View.GONE);
                download_ll.setVisibility(View.GONE);
                switch (i) {
                    case 0:
                        stocks.setVisibility(View.VISIBLE);
                        title.setText("Stock Videos & Pics");
                        break;
                    case 1:
                        player.setPlayWhenReady(false);
                        title.setText("Purchase Order");
                        upload_purchase.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        player.setPlayWhenReady(false);
                        title.setText("Performa Invoice");
                        performa_invoice.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        player.setPlayWhenReady(false);
                        if (price%2==0) {
                            startPayment((int)(( price / 2)+gst));
                        } else {
                            startPayment(((int)(( price / 2)+gst))+1);
                        }
                        break;
                    case 4:
                        player.setPlayWhenReady(false);
                        title.setText("Delivery LR");
                        download_ll.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        startPayment( price / 2);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fullscreenButton = videoView.findViewById(R.id.exo_fullscreen_icon);

        fullscreenButton.setEnabled(false);

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullscreen) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (190 * getApplicationContext().getResources().getDisplayMetrics().density);
                    videoView.setLayoutParams(params);
                    upper.setVisibility(View.VISIBLE);
                    group.setVisibility(View.VISIBLE);
                    text_seek_bar.setVisibility(View.VISIBLE);
                    confirm_to_upload.setVisibility(View.VISIBLE);
                    fullscreen = false;
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    videoView.setLayoutParams(params);
                    upper.setVisibility(View.GONE);
                    group.setVisibility(View.GONE);
                    text_seek_bar.setVisibility(View.GONE);
                    confirm_to_upload.setVisibility(View.GONE);
                    fullscreen = true;
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_card_view.setVisibility(View.GONE);
            }
        });

        confirm_to_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seek_bar.setProgress(1);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seek_bar.setProgress(2);
            }
        });

        proceed_to_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seek_bar.setProgress(3);
            }
        });

        proceed_to_50_remain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seek_bar.setProgress(5);
            }
        });

        view_performa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDownload = false;
                if (performa_url != null && performa_url.isEmpty()) {
                    getPerformaUrl();
                }
            }
        });

        download_performa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDownload = true;
                isFromLr = false;
                String has_permission_granted = Paper.book().read("has_permission_granted");
                if (has_permission_granted != null && !has_permission_granted.isEmpty() && !has_permission_granted.equals("yes")) {
                    if (performa_url != null && performa_url.isEmpty()) {
                        getPerformaUrl();
                    }
                } else {
                    if (!checkPermission()) {
                        requestPermission();
                    } else {
                        if (performa_url != null && performa_url.isEmpty()) {
                            getPerformaUrl();
                        }
                        Paper.book().write("has_permission_granted", "yes");
                    }
                }
            }
        });

        view_lr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDownload = false;
                if (lr_url != null && lr_url.isEmpty()) {
                    getLrUrl();
                }
            }
        });

        download_lr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromDownload = true;
                isFromLr = true;
                String has_permission_granted = Paper.book().read("has_permission_granted");
                if (has_permission_granted != null && !has_permission_granted.isEmpty() && !has_permission_granted.equals("yes")) {
                    if (lr_url != null && lr_url.isEmpty()) {
                        getLrUrl();
                    }
                } else {
                    if (!checkPermission()) {
                        requestPermission();
                    } else {
                        if (lr_url != null && lr_url.isEmpty()) {
                            getLrUrl();
                        }
                        Paper.book().write("has_permission_granted", "yes");
                    }
                }
            }
        });

        getImages();
    }

    private void getLrUrl() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_lr/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            lr_url = JsonUtils.getJsonValueFromKey(jsonObject, "url");
                            new RetrievePdfStream().execute(lr_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    private void getPerformaUrl() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_performa/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            performa_url = JsonUtils.getJsonValueFromKey(jsonObject, "url");
                            new RetrievePdfStream().execute(performa_url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    private void getImages() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_images/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject each = data.getJSONObject(i);
                                urls.add(JsonUtils.getJsonValueFromKey(each, "image_url"));
                            }

                            video_url = JsonUtils.getJsonValueFromKey(jsonObject, "video_url");
                            initializePlayer(video_url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBar.setVisibility(View.GONE);
                        imagesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );

        progressBar.setVisibility(View.VISIBLE);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(String url) {
        Glide.with(this).load(url).into(image);
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

            if (inputStream != null) {
                if (!isFromDownload) {
                    pdfViewer.setVisibility(View.VISIBLE);
                    pdfViewer.fromStream(inputStream).onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            isPdfVisible = true;
                            progressBar.setVisibility(View.GONE);
                        }

                    }).onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }).load();
                } else {
                    File file = null;

                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/medicento/docs");

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    if (isFromLr) {
                        file = new File(dir, "/LR" + System.currentTimeMillis() + ".pdf");
                    } else {
                        file = new File(dir, "/Performa" + System.currentTimeMillis() + ".pdf");
                    }
                    copyInputStreamToFile(inputStream, file);
                }
            }
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UploadPurchaseActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(UploadPurchaseActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED
                && read == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, REQUEST_PERMISSION_CODE);
        Paper.book().write("has_permission_granted", "yes");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        Paper.book().write("has_permission_granted", "yes");
        if (performa_url != null && performa_url.isEmpty()) {
            getPerformaUrl();
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Toast.makeText(this, "Downloaded File In Medicento Folder", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showImages() {

        image_card_view.setVisibility(View.VISIBLE);
        Glide.with(this).load(urls.get(0)).into(image);
        fullStcokImageAdapter = new FullStcokImageAdapter(urls, this);
        stock_images_full_screen_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        stock_images_full_screen_rv.setAdapter(fullStcokImageAdapter);
        fullStcokImageAdapter.setSetOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if (isPdfVisible) {
            isPdfVisible = false;
            pdfViewer.setVisibility(View.GONE);
        } else if (fullscreen) {
            fullscreenButton.performClick();
        } else {
            super.onBackPressed();
        }
    }

    public void startPayment(int price) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.mdl);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "mediclick heathcare services private limited");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #" + System.currentTimeMillis());
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", (price * 100) + "");

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("data", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        if (seek_bar.getProgress() == 3) {
            seek_bar.setProgress(4);
        } else {
            saveData(s);
        }
        Log.d("data", "onPaymentSuccess: " + s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        if (seek_bar.getProgress() == 3) {
            seek_bar.setProgress(4);
        } else {
            saveData(s);
        }
        Log.d("data", "onPaymentError: " + s);
    }

    private void saveData(final String s) {

        final JSONArray jsonArray = new JSONArray();

        for (EssentialList essentialList : essentialLists) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", essentialList.getName());
                jsonObject.put("cost", essentialList.getCost());
                jsonObject.put("qty", essentialList.getQty());
                price += essentialList.getCost() + essentialList.getQty();
                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final JSONObject items = new JSONObject();
        try {
            items.put("items", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/save_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                        String essential_saved = Paper.book().read("essential_saved_json");
                        if (essential_saved != null && !essential_saved.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject(essential_saved);
                                jsonObject.put(sp.getUsercode(), "");
                                Paper.book().write("essential_saved_json", jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(sp.getUsercode(), "");
                                Paper.book().write("essential_saved_json", jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("Order Saved")) {
                                startActivity(new Intent(UploadPurchaseActivity.this, PaymentGateWayActivity.class)
                                        .putExtra("id", JsonUtils.getJsonValueFromKey(jsonObject, "id")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("order_items", items.toString());
                params.put("response", s);
                params.put("id", sp.getmAllocatedPharmaId());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void initializePlayer(String url) {
        video_url = url;
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).build();
            videoView.setPlayer(player);
        }
        // Create a DASH media source pointing to a DASH manifest uri.
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(0, 0);
        player.prepare(mediaSource, false, false);
        player.addListener(this);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "medicento");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void initializePlayerResume(String url) {
        video_url = url;
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).build();
            videoView.setPlayer(player);
        }
        // Create a DASH media source pointing to a DASH manifest uri.
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
        player.addListener(this);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_READY:
                fullscreenButton.setEnabled(true);
                break;
            case Player.STATE_BUFFERING:
                fullscreenButton.setEnabled(true);
                break;
        }
    }

    @Override
    public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        view_text.setVisibility(View.VISIBLE);
        view_on_web.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {

    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        releasePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused) {
            isPaused = false;
            initializePlayerResume(video_url);
        }
    }

}
