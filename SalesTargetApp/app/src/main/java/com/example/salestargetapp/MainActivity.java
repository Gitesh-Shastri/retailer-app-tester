package com.example.salestargetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.salestargetapp.dashboard.DashboardActivity;
import com.example.salestargetapp.data.SalesPerson;
import com.example.salestargetapp.login.LoginActivity;
import com.example.salestargetapp.pharmacy_selection.PharmacySelectionActivity;
import com.google.gson.Gson;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            startActivity(new Intent(MainActivity.this, PharmacySelectionActivity.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}
