package com.medicento.retailerappmedi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.medicento.retailerappmedi.PlaceOrderActivity;
import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.SignUpActivity;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            startNewActivity(PlaceOrderActivity.class);
        } else {
            startNewActivity(SignUpActivity.class);
        }
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
