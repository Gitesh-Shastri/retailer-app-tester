package com.medicento.retailerappmedi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.medicento.retailerappmedi.data.Medicine;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavedMedicines extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_medicines);

    }
}
