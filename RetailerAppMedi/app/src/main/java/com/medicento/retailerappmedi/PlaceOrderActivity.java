package com.medicento.retailerappmedi;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.medicento.retailerappmedi.create_account.CreateAccountActivity;
import com.medicento.retailerappmedi.data.SalesPerson;

import io.paperdb.Paper;

public class PlaceOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String cache = Paper.book().read("user");
                Gson gson = new Gson();
                if (cache != null && !cache.isEmpty()) {
                    SalesPerson sp = gson.fromJson(cache, SalesPerson.class);
                    if (sp.getType().equals("Pharmacy")) {
                        startNewActivity(HomeActivity.class);
                    } else {
                        startNewActivity(EssentialsActivity.class);
                    }
                } else {
                    startNewActivity(CreateAccountActivity.class);
                }
            }
        }, 2000);
    }

    private void startNewActivity(Class cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
