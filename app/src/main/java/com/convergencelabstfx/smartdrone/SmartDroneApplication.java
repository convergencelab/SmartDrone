package com.convergencelabstfx.smartdrone;

import android.app.Application;

import timber.log.Timber;

public class SmartDroneApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
