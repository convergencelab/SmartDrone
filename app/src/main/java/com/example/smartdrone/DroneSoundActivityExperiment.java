package com.example.smartdrone;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class DroneSoundActivityExperiment extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Switch bassSwitch;
    private TextView curModeText;

    private int userModeIx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_sound_experiment);
        findViews();
        loadSavedDate();

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

    private void findViews() {
        bassSwitch = findViewById(R.id.root_bass_switch);
        curModeText = findViewById(R.id.mode_text_mode);
    }

    private void loadSavedDate() {
        sp = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        // Load bass root switch data.
        bassSwitch.setChecked(sp.getBoolean(DroneSoundActivity.BASSNOTE_KEY, true));
        // Load user mode data.
        userModeIx = sp.getInt(DroneSoundActivity.USER_MODE_KEY, 0);
        String curMode = MusicTheory.MAJOR_MODE_NAMES[sp.getInt(DroneSoundActivity.USER_MODE_KEY, 0)];
        curModeText.setText(curMode);
    }

    public void getNextMode(View view) {
        userModeIx = (userModeIx + 1) % 7;
        curModeText.setText(MusicTheory.MAJOR_MODE_NAMES[userModeIx]);
        editor.putInt(DroneSoundActivity.USER_MODE_KEY, userModeIx); //todo refactor to put ints instead of strings
        editor.apply();
    }
}
