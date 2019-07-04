package com.convergencelab.smartdrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DroneSettingsActivity extends AppCompatActivity {

    public static final String NOTE_LEN_KEY = "noteLen";
    public static final String KEY_SENS_KEY = "keySens"; // TODO: refactor drone_preferences; has hardcoded key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DroneSettingsFragment()).commit();
    }
}
