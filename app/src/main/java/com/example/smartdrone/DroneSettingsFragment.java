package com.example.smartdrone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DroneSettingsFragment extends PreferenceFragmentCompat {
    // TODO: currently, preferences have no effect on app settings
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.drone_preferences, rootKey);
    }

}
