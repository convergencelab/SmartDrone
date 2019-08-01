package com.convergencelabstfx.smartdrone;


import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Drone preferences fragment.
 */
public class DroneSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.drone_preferences, rootKey);

        Preference aboutButton = findPreference(getString(R.string.about_button));
        aboutButton.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), AboutActivity.class);
            startActivity(intent);
            return true;
        });
    }

}
