package com.medicento.retailerappmedi.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.medicento.retailerappmedi.R;

public class PaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
