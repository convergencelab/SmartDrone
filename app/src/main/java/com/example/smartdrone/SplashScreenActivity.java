package com.example.smartdrone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    /**
     * Duration of splash screen in milliseconds.
     */
    private static final int SPLASH_DURATION = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), DroneActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
