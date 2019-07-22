package com.convergencelab.smartdrone;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DroneSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DroneSettingsFragment()).commit();
    }
}
