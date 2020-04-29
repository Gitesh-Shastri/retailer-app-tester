package com.medicento.retailerappmedi.create_account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.SignUpActivity;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void createRetailer(View view) {
        startActivity(new Intent(CreateAccountActivity.this, RegisterMedicentoActivity.class).putExtra("tag", view.getTag().toString()));
    }

    public void createDistrinbutor(View view) {
        startActivity(new Intent(CreateAccountActivity.this, RegisterDistributorActivity.class).putExtra("tag", view.getTag().toString()));
    }

    public void login(View view) {
        startActivity(new Intent(CreateAccountActivity.this, SignUpActivity.class));
    }
}
