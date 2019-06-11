package com.example.smartdrone;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class DroneSoundActivityExperiment extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_sound_experiment);

        sp = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        Switch bassSwitch = findViewById(R.id.root_bass_switch);
        bassSwitch.setChecked(sp.getBoolean(DroneSoundActivity.BASSNOTE_KEY, true));

        bassSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(DroneSoundActivity.BASSNOTE_KEY, true);
                    editor.apply();
                }
                else {
                    editor.putBoolean(DroneSoundActivity.BASSNOTE_KEY, false);
                    editor.apply();
                }
            }
        });
    }
}
