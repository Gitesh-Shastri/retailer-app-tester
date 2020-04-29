package com.medicento.retailerappmedi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.medicento.retailerappmedi.activity.ConfirmationAccountActivity;
import com.medicento.retailerappmedi.activity.MainActivity;

public class AccountCredentialdsActivity extends AppCompatActivity {

    TextView pharma_code, mobile_no;
    Button sign_in_btn;
    String number = "9911806266";
    String pharmacode = "5108";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_credentialds);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pharma_code = findViewById(R.id.pharma_code);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        mobile_no = findViewById(R.id.mobile_no);

        if (getIntent() != null && getIntent().hasExtra("number")) {
            number = getIntent().getStringExtra("number");
            pharmacode = getIntent().getStringExtra("pharmacode");
        }

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountCredentialdsActivity.this, SignUpActivity.class));
            }
        });

        pharma_code.setText(Html.fromHtml("Your Pharma Code : <br/><b>"+ pharmacode +"</b>"));
        mobile_no.setText(Html.fromHtml("Your Registered Mobile No. /  <br>Email ID : <b>"+number+"</b>"));
    }
}
