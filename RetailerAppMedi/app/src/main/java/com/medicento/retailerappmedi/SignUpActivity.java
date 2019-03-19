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
import com.medicento.retailerappmedi.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medicento.retailerappmedi.data.Constants;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SignUp Activity";
    private final static int RC_SIGN_IN = 2;

    String verificationId;

    FirebaseAuth mAuth;

    CardView verifyCard;

    EditText code;

    private GoogleSignInClient mGoogleSignInClient;

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

    SharedPreferences sharedPreferences;

    ImageView facebook, google, twitter;

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }

    private void init() {

        facebook = findViewById(R.id.facebook_login);

        google = findViewById(R.id.google_login);

        twitter = findViewById(R.id.twitter_login);

        forget = findViewById(R.id.forget);

        privacy = findViewById(R.id.privacy);

        terms = findViewById(R.id.termsOfService);

        mLogo = findViewById(R.id.medicento_logo);

        mEmailEditTv = findViewById(R.id.user_code_tv);

        phoneEditTv = findViewById(R.id.user_phone_tv);

        signUp = findViewById(R.id.signUp);

        code =findViewById(R.id.code);

        relativeLayout = findViewById(R.id.relative);

        progressBar = findViewById(R.id.progress_sign_up);

        signIn = findViewById(R.id.sign_in_btn);

        verifyCode = findViewById(R.id.verifyCode);

        verifyCard = findViewById(R.id.verifyCodeCard);

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

            case R.id.google_login:

                Snackbar.make(relativeLayout, "google", Toast.LENGTH_SHORT).show();
                break;

            case R.id.facebook_login:

                Snackbar.make(relativeLayout, "facebook", Toast.LENGTH_SHORT).show();
                break;

            case R.id.twitter_login:

                Snackbar.make(relativeLayout, "twitter", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sign_in_btn:

                hideSoftKeyboard(v);

                usercode = mEmailEditTv.getText().toString();

                if (usercode.isEmpty() && phoneEditTv.getText().toString().isEmpty()) {

                    Snackbar.make(relativeLayout, "Please Enter Data To Login", Toast.LENGTH_SHORT).show();

                } else if (!amIConnect()) {

                    showAlertDialog();

                } else {

                    progressBar.setVisibility(View.VISIBLE);

                    snackbar = Snackbar.make(relativeLayout, "Please wait signing you in ..", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();

                    RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);

                    String url = "https://retailer-app-api.herokuapp.com/user/login?usercode=" + mEmailEditTv.getText().toString();
                    signUp(url, queue);

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

                if(phone.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Provide Email / Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        "https://retailer-app-api.herokuapp.com/user/forgetPhone?phone=" + phone.getText().toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if(jsonObject.getString("message").equals("user found")) {

                                        final Dialog dialog1 = new Dialog(SignUpActivity.this);
                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog1.setContentView(R.layout.congrats);

                                        TextView email = dialog1.findViewById(R.id.phone);
                                        email.setText("  Your Registered Mobile Number: "+phone.getText().toString());

                                        TextView code = dialog1.findViewById(R.id.code);
                                        code.setText("Your PharmaCode: "+jsonObject.getString("code"));

                                        Button back1 = dialog1.findViewById(R.id.back);
                                        back1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                            }
                                        });

                                        dialog1.show();

                                    } else {

                                        final Dialog dialog1 = new Dialog(SignUpActivity.this);
                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog1.setContentView(R.layout.no_user_found);

                                        Button retry, login;
                                        retry = dialog1.findViewById(R.id.cancel);
                                        login = dialog1.findViewById(R.id.login);

                                        retry.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                            }
                                        });

                                        login.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog1.show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignUpActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }
                });

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

        mAuth = FirebaseAuth.getInstance();

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

        facebook.setOnClickListener(this);

        google.setOnClickListener(this);

        twitter.setOnClickListener(this);

        signUp.setOnClickListener(this);

        signIn.setOnClickListener(this);

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifycode = code.getText().toString().trim();
                if(verifycode.isEmpty() || verifycode.length()<6) {
                    Toast.makeText(SignUpActivity.this, "Enter Code", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyCode(verifycode);
            }
        });

        verifyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCard.setVisibility(View.GONE);
            }
        });

    }

    private boolean amIConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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

    private void signUp(String url, RequestQueue queue) {


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("message_for_perso",response);
                        sp = null;
                        try {

                            JSONObject spo = new JSONObject(response);

                            snackbar.dismiss();

                            if(spo.has("message")) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Snackbar.make(relativeLayout, "Invalid Login Credentials. Please try again!", Snackbar.LENGTH_SHORT).show();

                            } else {
                                JSONArray spa = spo.getJSONArray("Sales_Person");
                                JSONObject user = spa.getJSONObject(0);

                                if(user.has("Allocated_Area")) {

                                    sp = new SalesPerson(user.getString("Name"),
                                            user.getLong("Total_sales"),
                                            user.getInt("No_of_order"),
                                            user.getInt("Returns"),
                                            user.getLong("Earnings"),
                                            user.getString("_id"),
                                            user.getString("Allocated_Area"),
                                            user.getString("Allocated_Pharma"));
                                } else {
                                    sp = new SalesPerson(user.getString("Name"),
                                            user.getLong("Total_sales"),
                                            user.getInt("No_of_order"),
                                            user.getInt("Returns"),
                                            user.getLong("Earnings"),
                                            user.getString("_id"),
                                            "5c4b5e7bed743008af76ef0e",
                                            user.getString("Allocated_Pharma"));
                                }

                                JSONObject jsonObject = user.getJSONObject("user");
                                if(jsonObject.has("phone")) {
                                    if(jsonObject.getString("phone").equals("-")) {
                                        sp.setPhone("");
                                    } else {
                                        if (!jsonObject.getString("phone").contains(phoneEditTv.getText().toString())) {
                                            Snackbar.make(relativeLayout, "Invalid Login Credentials. Please try again!", Snackbar.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            return;
                                        }
                                        sp.setPhone(jsonObject.getString("phone"));
                                        Log.d("phone", sp.getPhone());
                                    }
                                } else {
                                    sp.setPhone("");
                                }
                                sp.setUsercode(usercode);

                                saveData(sp);

                                progressBar.setVisibility(View.INVISIBLE);
                                verifyCard.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(SignUpActivity.this, PlaceOrderActivity.class);
                                intent.putExtra("user", sp);
                                startActivity(intent);

//                                verifyCard.setVisibility(View.VISIBLE);
//                                sendVerificationCode(phoneEditTv.getText().toString());

                                }

                        } catch (JSONException e) {

                            e.printStackTrace();
                            progressBar.setVisibility(View.INVISIBLE);
                            snackbar.dismiss();
                            Snackbar.make(relativeLayout, "Some Error Occured Please try again!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mEmailEditTv.setText("");

                        progressBar.setVisibility(View.INVISIBLE);

                        snackbar.dismiss();
                        Snackbar.make(relativeLayout, "Invalid Usercode ", Snackbar.LENGTH_SHORT).show();
                    }
                });
        queue.add(stringRequest);
    }


    private  void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+number,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBacks
        );
    }

    private  void verifyCode(String code) {
        try {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(phoneAuthCredential);
        } catch (Exception e ){
            verifyCard.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(SignUpActivity.this, PlaceOrderActivity.class);
            intent.putExtra("user", sp);
            startActivity(intent);
        }
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            verifyCard.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(SignUpActivity.this, PlaceOrderActivity.class);
                            intent.putExtra("user", sp);
                            startActivity(intent);

                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(SignUpActivity.this, "Code Sent : "+s, Toast.LENGTH_SHORT).show();
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();

            if(code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void saveData(SalesPerson salesPerson) {

        Paper.book().write("user", new Gson().toJson(salesPerson));
    }

    private void hideSoftKeyboard(View view) {

        InputMethodManager ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ime.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
