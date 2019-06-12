package com.example.smartdrone;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class DroneSoundActivityExperiment extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Switch bassSwitch;
    private TextView curModeText;
    private TextView userPluginText;
    private LinearLayout voicingLinear;

    private int userModeIx;
    private int userPluginIx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_sound_experiment);
        prefs = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        findViews();
        loadSavedData();

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

    /**
     * Initializes views fields with their corresponding views.
     */
    private void findViews() {
        bassSwitch = findViewById(R.id.root_bass_switch);
        curModeText = findViewById(R.id.mode_text_name);
        userPluginText = findViewById(R.id.user_plugin_name);
        voicingLinear = findViewById(R.id.user_voicing_linear);
    }

    /**
     * Loads all user saved preferences.
     */
    private void loadSavedData() {
        loadBassSwitchData();
        loadModeData();
        loadPluginData();
        loadVoicingData();
    }

    /**
     * Load bass switch data from shared preferences.
     */
    private void loadBassSwitchData() {
        bassSwitch.setChecked(prefs.getBoolean(DroneSoundActivity.BASSNOTE_KEY, true));
    }

    /**
     * Load mode data from shared preferences.
     */
    private void loadModeData() {
        userModeIx = prefs.getInt(DroneSoundActivity.USER_MODE_KEY, 0);
        String curMode = MusicTheory.MAJOR_MODE_NAMES[userModeIx];
        curModeText.setText(curMode);
    }

    /**
     * Load plugin data from shared preferences.
     */
    private void loadPluginData() {
        userPluginIx = prefs.getInt(DroneSoundActivity.USER_PLUGIN_KEY, 0);
        String userPlugin = Constants.PLUGIN_NAMES[userPluginIx];
        userPluginText.setText(userPlugin);
    }

    /**
     * Load voicing data from shared preferences.
     * Inflates scroll view with all voicings.
     */
    private void loadVoicingData() {
        String tempsStr = prefs.getString(DroneActivity.ALL_TEMP_KEY, null);
        if (tempsStr == null) {
            tempsStr = Constants.DEFAULT_TEMPLATES;
        }
        ArrayList<String> tempsList = VoicingHelper.inflateTemplateList(tempsStr);
        // Inflate scroll view
        for (String temp : tempsList) {
            TextView tv = new TextView(this);
            tv.setText(temp);
            tv.setTextSize(64);
            voicingLinear.addView(tv);
        }
    }

    /**
     * Changes user mode.
     * Click changes mode to the next mode, in order from Ionian to Locrian.
     * @param       view View; button on top of text view.
     */
    public void getNextMode(View view) {
        userModeIx = (userModeIx + 1) % 7;
        curModeText.setText(MusicTheory.MAJOR_MODE_NAMES[userModeIx]);
        editor.putInt(DroneSoundActivity.USER_MODE_KEY, userModeIx);
        editor.apply();
    }

    public void getNextPlugin(View view) {
        userPluginIx = (userPluginIx + 1) % Constants.PLUGIN_INDICES.length;
        userPluginText.setText(Constants.PLUGIN_NAMES[userPluginIx]);
        editor.putInt(DroneSoundActivity.USER_PLUGIN_KEY, userPluginIx);
        editor.apply();
    }
}
