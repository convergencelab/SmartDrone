package com.convergencelabstfx.smartdrone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.convergencelabstfx.smartdrone.drone.DroneActivity;
import com.convergencelabstfx.smartdrone.v2.views.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView background = findViewById(R.id.splash_img);
        background.setImageResource(R.drawable.convergence_lab_1080);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}
