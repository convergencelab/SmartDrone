package com.convergencelabstfx.smartdrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.convergencelabstfx.smartdrone.Models.DroneSoundModel;
import com.example.smartdrone.MusicTheory;
import com.convergencelabstfx.smartdrone.Utility.DronePreferences;

import java.util.ArrayList;

public class DroneSoundActivity extends AppCompatActivity {

//    public static final String USER_MODE_KEY = "userModeIx"; //todo extract to string resource
    public static final String USER_PLUGIN_KEY = "userPlugin"; //todo extract to string resource
//    public static final String BASSNOTE_KEY = "bassNoteEnabled"; //todo extract to string resource


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private ArrayList<String> templateList;
    private TextView curSelectedTemplateView;

    private Switch bassSwitch;
    private TextView curModeText;
    private TextView userPluginText;
    private LinearLayout voicingLinear;

    String curTemplateString;

    private int userModeIx;
    private int userPluginIx;
    private boolean hasBassNote;

    private DroneSoundModel droneSoundModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_sound);
        prefs = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedData();

        droneSoundModel = new DroneSoundModel(Constants.PLUGIN_INDICES[userPluginIx], userModeIx, hasBassNote, VoicingHelper.inflateTemplate(curTemplateString));
        droneSoundModel.initializePlayback();
        droneSoundModel.changePlayBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTemplateData();
        droneSoundModel.stopPlayback();
    }

    private void saveTemplateData() {
        String flattenedTemplateList = VoicingHelper.flattenTemplateList(templateList);
        editor.putString(DroneActivity.ALL_TEMP_KEY, flattenedTemplateList);
        editor.putString(DroneActivity.CUR_TEMP_KEY, curTemplateString);
        editor.apply();
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
        hasBassNote = DronePreferences.getStoredBassPref(this);
        bassSwitch.setChecked(hasBassNote);
        bassSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hasBassNote = true;
                    DronePreferences.setStoredBassPref(DroneSoundActivity.this, true);
//                    editor.apply();
                }
                else {
//                    editor.putBoolean(BASSNOTE_KEY, false);
                    hasBassNote = false;
//                    editor.apply();
                    DronePreferences.setStoredBassPref(DroneSoundActivity.this, false);
                }
                droneSoundModel.setHasBassNote(hasBassNote); // todo refactor
            }
        });
    }

    /**
     * Load mode data from shared preferences.
     */
    private void loadModeData() {
//        userModeIx = prefs.getInt(USER_MODE_KEY, 0);
        userModeIx = DronePreferences.getStoredModePref(this);
        String curMode = MusicTheory.MAJOR_MODE_NAMES[userModeIx];
        curModeText.setText(curMode);
    }

    /**
     * Load plugin data from shared preferences.
     */
    private void loadPluginData() {
        userPluginIx = prefs.getInt(USER_PLUGIN_KEY, 0);
        //todo remove when debugged
        if (userPluginIx >= Constants.PLUGIN_INDICES.length) {
            userPluginIx = 0;
        }
        String pluginName = Constants.PLUGIN_NAMES[userPluginIx];
        userPluginText.setText(pluginName);
    }

    //todo refactor to highlight based on text rather than Tag; I think tag will create bugs when adding/removing voices
    //todo refactor giant function
    /**
     * Load voicing data from shared preferences.
     * Inflates scroll view with all voicings.
     */
    private void loadVoicingData() {
        if (voicingLinear.getChildCount() != 0) {
            voicingLinear.removeAllViews();
        }
        String tempsStr = prefs.getString(DroneActivity.ALL_TEMP_KEY, Constants.DEFAULT_TEMPLATE_LIST);
        curTemplateString = prefs.getString(DroneActivity.CUR_TEMP_KEY, Constants.DEFAULT_TEMPLATE);
        /// List of flattened templates
        templateList = VoicingHelper.inflateTemplateList(tempsStr);
        Log.d("d_bug", "List: " + tempsStr);

        // Inflate scroll view
        for (String temp : templateList) {
            // Get text view
            final TextView tv = new TextView(getApplicationContext());
            // Set attributes of text view.
            if (temp.equals(curTemplateString)) {
                tv.setTextColor(getResources().getColor(R.color.green_test));
                curSelectedTemplateView = tv;
            }
            else {
                tv.setTextColor(getResources().getColor(R.color.blackish_test));
            }
            tv.setText(VoicingHelper.getTemplateName(temp));
            tv.setTextSize(18); //todo refactor: hard coded string
            tv.setPadding(0, 40, 0, 40);
            //todo make drawable transparent
            //todo make ripple effect on click
            tv.setBackgroundResource(R.drawable.textline_bottom);
            tv.setTag(temp);
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                //todo works correctly, but needs to be refactored
                @Override
                public void onClick(View v) {
                    // Set old template text black.
                    curSelectedTemplateView.setTextColor(getResources().getColor(R.color.blackish_test));
                    // Set new template text green.
                    tv.setTextColor(getResources().getColor(R.color.green_test));
                    // Update flattened template.
                    curTemplateString = (String) tv.getTag();
                    // Update current template variable.
                    curSelectedTemplateView = tv;
                    //todo testing line of code
                    droneSoundModel.setCurTemplate(VoicingHelper.inflateTemplate(curTemplateString));
                }
            });
            tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (templateList.size() > 1) {
                        voicingLinear.removeView(tv); //todo create fragment dialog. Edit, delete, cancel
                        templateList.remove(tv.getTag());
                        if (tv == curSelectedTemplateView) {
                            TextView temp = (TextView) voicingLinear.getChildAt(0);
                            curSelectedTemplateView = temp;
                            curTemplateString = (String) curSelectedTemplateView.getTag();
                            temp.setTextColor(getResources().getColor(R.color.green_test));
                        }
                    }
                    return true;
                }
            });
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
//        editor.putInt(USER_MODE_KEY, userModeIx);
//        editor.apply();
        DronePreferences.setStoredModePref(this, userModeIx);
        droneSoundModel.setModeIx(userModeIx);
    }

    public void getNextPlugin(View view) {
        userPluginIx = (userPluginIx + 1) % Constants.PLUGIN_INDICES.length;
        userPluginText.setText(Constants.PLUGIN_NAMES[userPluginIx]);
        droneSoundModel.setPlugin(Constants.PLUGIN_INDICES[userPluginIx]);
        editor.putInt(USER_PLUGIN_KEY, userPluginIx);
        editor.apply();
        droneSoundModel.changePlayBack();
    }


    /**
     * Opens the voicing creator activity.
     * @param       view View; American Talk Show on the ABC network.
     */
    public void openVoicingCreator(View view) {
        Intent intent = new Intent(this, VoicingCreatorActivity.class);
        startActivity(intent);
    }
}
