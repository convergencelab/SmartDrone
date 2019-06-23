package com.example.smartdrone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.smartdrone.Utility.DroneLog;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DroneLog.activityLifecycle("Splash Screen Activity", "onCreate");
        

        Intent intent = new Intent(this, DroneActivity.class);
        startActivity(intent);
        finish();
    }
}
