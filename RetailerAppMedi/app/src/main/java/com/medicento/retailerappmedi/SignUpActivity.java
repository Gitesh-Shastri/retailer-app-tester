package com.medicento.retailerappmedi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.medicento.retailerappmedi.Utils.JsonUtils;
import com.medicento.retailerappmedi.activity.NoInternetActivity;
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medicento.retailerappmedi.data.Constants;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

import static com.medicento.retailerappmedi.Utils.MedicentoUtils.amIConnect;
import static com.medicento.retailerappmedi.Utils.MedicentoUtils.showVolleyError;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SignUp Activity";


    EditText code;

    EditText mEmailEditTv, phoneEditTv;

    TextView call, forget;

    RelativeLayout relativeLayout;

    String usercode;

    SalesPerson sp;

    AlertDialog alert;

    ImageView mLogo;

    TextView terms, signUp;

    Snackbar snackbar;

    ProgressBar progressBar;

    Button signIn, privacy, verifyCode, verifyCancel;

    RequestQueue queue;
    Dialog dialog_app_exit;
    JSONObject activityObject;

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

    private void init() {

        forget = findViewById(R.id.forget);
        privacy = findViewById(R.id.privacy);
        terms = findViewById(R.id.termsOfService);
        mLogo = findViewById(R.id.medicento_logo);
        mEmailEditTv = findViewById(R.id.user_code_tv);
        phoneEditTv = findViewById(R.id.user_phone_tv);
        signUp = findViewById(R.id.signUp);
        code = findViewById(R.id.code);
        relativeLayout = findViewById(R.id.relative);
        progressBar = findViewById(R.id.progress_sign_up);
        signIn = findViewById(R.id.sign_in_btn);
        verifyCode = findViewById(R.id.verifyCode);
        verifyCancel = findViewById(R.id.verifyCancel);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.termsOfService:
                startActivity(new Intent(SignUpActivity.this, TermsAndCondition.class));
                break;

            case R.id.signUp:
                startActivity(new Intent(SignUpActivity.this, Register.class));
                break;

            case R.id.forget:
                forgetCode();
                break;

            case R.id.sign_in_btn:
                hideSoftKeyboard(v);
                usercode = mEmailEditTv.getText().toString();
                if (usercode.isEmpty() && phoneEditTv.getText().toString().isEmpty()) {
                    Snackbar.make(relativeLayout, "Please Enter Data To Login", Toast.LENGTH_SHORT).show();
                } else if (!amIConnect(SignUpActivity.this)) {
                    startActivity(new Intent(SignUpActivity.this, NoInternetActivity.class));
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    snackbar = Snackbar.make(relativeLayout, "Please wait signing you in ..", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();

                    signUp();
                }
        }
    }

    private void forgetCode() {

        final Dialog dialog = new Dialog(SignUpActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_pharma_code);

        final EditText phone = dialog.findViewById(R.id.email);

        Button submit = dialog.findViewById(R.id.submit);

        Button back = dialog.findViewById(R.id.back);
        Button create = dialog.findViewById(R.id.create);

        dialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, Register.class));
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!amIConnect(SignUpActivity.this)) {
                    startActivity(new Intent(SignUpActivity.this, NoInternetActivity.class));
                }
                if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Provide Email / Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        "http://54.161.199.63:8080/pharmacy/forget_email/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                                    if(message.equals("Pharmacy Found")) {

                                        JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");

                                        String code_value = JsonUtils.getJsonValueFromKey(pharmacy, "pharma_code");

                                        final Dialog dialog1 = new Dialog(SignUpActivity.this);
                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog1.setContentView(R.layout.congrats);

                                        TextView email = dialog1.findViewById(R.id.phone);
                                        email.setText("  Your Registered Mobile Number / Email: " + phone.getText().toString());

                                        TextView code = dialog1.findViewById(R.id.code);
                                        code.setText("Your PharmaCode: " + code_value);

                                        Button back1 = dialog1.findViewById(R.id.back);
                                        back1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                            }
                                        });

                                        dialog1.show();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Sorry, you have entered a wrong Email Id/Mobile Number. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Sorry, you have entered a wrong Email Id/Mobile Number. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    if (error == null || error.networkResponse == null) {
                                        Toast.makeText(SignUpActivity.this, "No Response From Server", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String body;
                                    try {
                                        body = new String(error.networkResponse.data, "UTF-8");
                                        JSONObject jsonObject = new JSONObject(body);
                                        if(jsonObject.has("message")) {
                                            Toast.makeText(SignUpActivity.this, JsonUtils.getJsonValueFromKey(jsonObject, "message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        Toast.makeText(SignUpActivity.this, "No Response From Server", Toast.LENGTH_SHORT).show();
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
                        params.put("email", phone.getText().toString());
                        return params;
                    }

                };

                requestQueue.add(stringRequest);

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Paper.init(this);

        init();

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://medicento.com/medicento-privacy-policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        forget.setOnClickListener(this);

        terms.setOnClickListener(this);

        signUp.setOnClickListener(this);

        signIn.setOnClickListener(this);

    }

    private void signUp() {

        queue = Volley.newRequestQueue(SignUpActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://54.161.199.63:8080/pharmacy/login/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                            JSONObject pharmacy = jsonObject.getJSONObject("pharmacy");

                            if (message.equals("Pharmacy Found")) {

                                JSONObject area = pharmacy.getJSONObject("area");

                                sp = new SalesPerson(
                                        JsonUtils.getJsonValueFromKey(pharmacy, "name"),
                                        0L,
                                        0,
                                        0,
                                        0,
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"),
                                        JsonUtils.getJsonValueFromKey(area, "id"),
                                        JsonUtils.getJsonValueFromKey(pharmacy, "id"));

                                sp.setmAllocatedStateId(JsonUtils.getJsonValueFromKey(area, "state"));
                                sp.setmAllocatedCityId(JsonUtils.getJsonValueFromKey(area, "city"));

                                JSONObject city = pharmacy.getJSONObject("city");
                                JSONObject state = pharmacy.getJSONObject("state");

                                sp.setAddress(JsonUtils.getJsonValueFromKey(pharmacy, "address"));
                                sp.setEmail(JsonUtils.getJsonValueFromKey(pharmacy, "email_id"));
                                sp.setArea_name(JsonUtils.getJsonValueFromKey(area, "name"));
                                sp.setCity_name(JsonUtils.getJsonValueFromKey(city, "name"));
                                sp.setState_name(JsonUtils.getJsonValueFromKey(state, "name"));

                                sp.setPhone(phoneEditTv.getText().toString());
                                sp.setUsercode(usercode);

                                Paper.book().write("user", new Gson().toJson(sp));

                                snackbar.dismiss();
                                Snackbar.make(relativeLayout, "User Logged In", Snackbar.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUpActivity.this, PlaceOrderActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("user", sp);
                                startActivity(intent);
                                finish();
                            } else {
                                snackbar.dismiss();
                                Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            snackbar.dismiss();
                            Snackbar.make(relativeLayout, "Invalid Details", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mEmailEditTv.setText("");
                        phoneEditTv.setText("");

                        progressBar.setVisibility(View.INVISIBLE);

                        snackbar.dismiss();
                        Snackbar.make(relativeLayout, "Invalid Details", Snackbar.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pharma_code", mEmailEditTv.getText().toString());
                params.put("mobile_no", phoneEditTv.getText().toString());

                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ime.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityObject = new JSONObject();
        try {
            activityObject.put("activity_name", "SignUp");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                "http://54.161.199.63:8080/api/app/record_activity/",
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
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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
    }
}
