package com.example.smartdrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DroneSoundActivity extends AppCompatActivity {
    public static final String USER_MODE_KEY = "userModeIx"; //todo extract to string resource
    public static final String USER_PLUGIN_KEY = "userPlugin"; //todo extract to string resource
    public static final String BASSNOTE_KEY = "bassNoteEnabled"; //todo extract to string resource

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