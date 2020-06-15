package com.convergencelabstfx.smartdrone.v2.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.convergencelabstfx.smartdrone.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

}
