package com.example.smartdrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DroneSoundActivity extends AppCompatActivity {


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DroneSoundFragment()).commit();
    }
}
