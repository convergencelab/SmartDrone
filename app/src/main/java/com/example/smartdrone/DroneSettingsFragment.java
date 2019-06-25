package com.example.smartdrone;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

/**
 * Drone preferences fragment.
 */
public class DroneSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.drone_preferences, rootKey);


        Preference aboutButton = findPreference(getString(R.string.about_button));
        aboutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

}
