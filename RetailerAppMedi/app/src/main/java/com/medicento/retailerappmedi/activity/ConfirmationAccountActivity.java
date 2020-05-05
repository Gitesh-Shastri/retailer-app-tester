package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.medicento.retailerappmedi.EssentialsActivity;
import com.medicento.retailerappmedi.HomeActivity;
import com.medicento.retailerappmedi.R;

public class ConfirmationAccountActivity extends AppCompatActivity {

    TextView pharma_code, mobile_no;
    Button sign_in_btn;
    String number = "9911806266";
    String pharmacode = "5108";
    boolean isDistributor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_account);

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

        if (getIntent() != null && getIntent().hasExtra("distributor")) {
            isDistributor = true;
        }

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDistributor) {
                    startActivity(new Intent(ConfirmationAccountActivity.this, EssentialsActivity.class));
                } else {
                    startActivity(new Intent(ConfirmationAccountActivity.this, HomeActivity.class));
                }
            }
        });

        pharma_code.setText(Html.fromHtml("Your Pharma Code : <br/><b>"+ pharmacode +"</b>"));
        mobile_no.setText(Html.fromHtml("Your Registered Mobile No. /  <br>Email ID : <b>"+number+"</b>"));
    }
}
