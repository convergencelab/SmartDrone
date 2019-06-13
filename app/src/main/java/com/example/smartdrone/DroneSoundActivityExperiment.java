package com.example.smartdrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class DroneSoundActivityExperiment extends AppCompatActivity {

    private static final String CUR_TEMP_TAG_KEY = "cur_temp_tag";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ArrayList<String> templateList;

    private Switch bassSwitch;
    private TextView curModeText;
    private TextView userPluginText;
    private LinearLayout voicingLinear;

    int curTempTag;
    int i;

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

    @Override
    protected void onResume() {
        super.onResume();
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

    //todo refactor to highlight based on text rather than Tag; I think tag will create bugs when adding/removing voices
    //todo refactor giant function
    /**
     * Load voicing data from shared preferences.
     * Inflates scroll view with all voicings.
     */
    private void loadVoicingData() {
        String tempsStr = prefs.getString(DroneActivity.ALL_TEMP_KEY, null);
        if (tempsStr == null) {
            tempsStr = Constants.DEFAULT_TEMPLATES;
            editor.putString(DroneActivity.ALL_TEMP_KEY, tempsStr);
            editor.apply();
        }
        curTempTag = prefs.getInt(CUR_TEMP_TAG_KEY, 0);
        templateList = VoicingHelper.inflateTemplateList(tempsStr);
        Log.d("d_bug", "List: " + tempsStr);

        // Inflate scroll view
        i = 0;
        for (String temp : templateList) {
            final TextView tv = new TextView(getApplicationContext());
            // Set attributes of text view.
            if (i == curTempTag) {
                tv.setTextColor(getResources().getColor(R.color.green_test));
            }
            else {
                tv.setTextColor(getResources().getColor(R.color.blackish_test));
            }
            tv.setText(VoicingHelper.getTemplateName(temp));
            tv.setTextSize(18); //todo refactor: hard coded string
            tv.setPadding(0, 40, 0, 40);
            //todo mkae drawable transparent
            //todo make ripple effect on click
            tv.setBackgroundResource(R.drawable.textline_bottom);
            tv.setTag(i);
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                //todo works correctly, but needs to be refactored
                @Override
                public void onClick(View v) {
                    View prevTemp = (View) v.getParent();
                    TextView prevCur = prevTemp.findViewWithTag(curTempTag);
                    prevCur.setTextColor(getResources().getColor(R.color.blackish_test));
                    tv.setTextColor(getResources().getColor(R.color.green_test));
                    int tag = (int) v.getTag();
                    String curTemplate = templateList.get(tag);
                    curTempTag = tag;
                    editor.putString(DroneActivity.CUR_TEMP_KEY, curTemplate);
                    editor.putInt(CUR_TEMP_TAG_KEY, tag);
                    editor.apply();
                }
            });
            voicingLinear.addView(tv);
            i++;
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

    /**
     * Opens the voicing creator activity.
     * @param       view View; American Talk Show on the ABC network.
     */
    public void openVoicingCreator(View view) {
        Intent intent = new Intent(this, VoicingCreatorActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String newTemplate = data.getStringExtra(VoicingCreatorActivity.SAVED_VOICING);
                if (newTemplate != "null") {
                    String tempsStr = prefs.getString(DroneActivity.ALL_TEMP_KEY, null);
                    tempsStr += '|' + newTemplate; //todo write method to do this.
                    Log.d("d_bug", tempsStr);
                    editor.putString(DroneActivity.ALL_TEMP_KEY, tempsStr);
                    editor.apply();
                    Log.d("d_bug", newTemplate + "aonetuha");
                    addVoicingToList(newTemplate);
                }
            }
        }
    }

    private void addVoicingToList(String newTemplate) {
        templateList.add(newTemplate);
        final TextView tv = new TextView(getApplicationContext());
        // Set attributes of text view.
        tv.setTextColor(getResources().getColor(R.color.blackish_test));

        tv.setText(VoicingHelper.getTemplateName(newTemplate));
        tv.setTextSize(18); //todo refactor: hard coded string
        tv.setPadding(0, 40, 0, 40);
        //todo mkae drawable transparent
        //todo make ripple effect on click
        tv.setBackgroundResource(R.drawable.textline_bottom);
        tv.setTag(i);
        i++;
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            //todo works correctly, but needs to be refactored
            @Override
            public void onClick(View v) {
                View prevTemp = (View) v.getParent();
                TextView prevCur = prevTemp.findViewWithTag(curTempTag);
                prevCur.setTextColor(getResources().getColor(R.color.blackish_test));
                tv.setTextColor(getResources().getColor(R.color.green_test));
                int tag = (int) v.getTag();
                String curTemplate = templateList.get(tag);
                curTempTag = tag;
                editor.putString(DroneActivity.CUR_TEMP_KEY, curTemplate);
                editor.putInt(CUR_TEMP_TAG_KEY, tag);
                editor.apply();
            }
        });
        voicingLinear.addView(tv);
    }
}
