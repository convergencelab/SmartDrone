package com.convergencelabstfx.smartdrone;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.convergencelabstfx.smartdrone.Models.DroneSoundModel;
import com.convergencelabstfx.smartdrone.Models.MidiDriverModel;
import com.example.smartdrone.ModeTemplateCollection;
import com.example.smartdrone.MusicTheory;
import com.convergencelabstfx.smartdrone.Utility.DronePreferences;

import java.util.HashSet;

public class VoicingCreatorActivity extends AppCompatActivity {

    private static final int NUM_BUTTONS = 14;

    private boolean[] chordTones;
    private EditText templateName;

    ModeTemplateCollection mtc = new ModeTemplateCollection();
    private DroneSoundModel droneSoundModel;

    private HashSet<String> nameSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicing_creator);


        chordTones = new boolean[NUM_BUTTONS];
        // Initialize root to true
        chordTones[0] = true;
      
        nameSet = VoicingHelper.getSetOfAllTemplateNames(DronePreferences.getAllTemplatePref(getApplicationContext()));

        templateName = findViewById(R.id.voicing_name_edit_text);
        loadButtonData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        droneSoundModel = new DroneSoundModel(
                Constants.PLUGIN_INDICES[DronePreferences.getStoredPluginPref(getApplicationContext())],
                DronePreferences.getStoredModePref(this),
                DronePreferences.getStoredBassPref(this),
                VoicingHelper.inflateTemplate("throwaway,0"));
        droneSoundModel.initializePlayback();
        droneSoundModel.changePlayBack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        droneSoundModel.getMidiDriverModel().getMidiDriver().stop();
    }

    /**
     * Loads button data for buttons.
     */
    private void loadButtonData() {
        String buttonId;
        int resId;
        Button curButton;
        // Buttons use base 1 indexing.
        for (int i = 1; i < NUM_BUTTONS + 1; i++) {
            buttonId = "voice_button_" + i;
            resId = getResources().getIdentifier(buttonId, "id", getPackageName());
            curButton = findViewById(resId);
            curButton.setText("" + i);
            curButton.setTag(i - 1);
            curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive)); //todo find better way to do this
            if ((int) curButton.getTag() == 0) {
                curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_active)); //todo find better way to do this
            }
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tone = (int) v.getTag();
                    // Toggle between true and false.
                    chordTones[tone] = !chordTones[tone];
                    if (chordTones[tone]) {
                        v.setBackground(getResources().getDrawable(R.drawable.active_key_background_active)); //todo find better way to do this
                        droneSoundModel.getMidiDriverModel().sendMidiNote(Constants.START_NOTE,
                                calcInterval((int) v.getTag()), MidiDriverModel.DEFAULT_VOLUME);
                    }
                    else {
                        v.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive)); //todo find better way to do this
                        droneSoundModel.getMidiDriverModel().sendMidiNote(Constants.STOP_NOTE,
                                calcInterval((int) v.getTag()), MidiDriverModel.DEFAULT_VOLUME);

                    }
                }
            });
        }
    }

    /**
     * Returns flattened voicing template.
     * @return      String; flattened template.
     */
    private String returnFlattenedTemplate() {
        String flattenedTemplate = "";
        String templateNameStr = templateName.getText().toString();
        if (templateName.length() == 0) { //todo find better way to do this
            templateNameStr = "Example Name";
        }
        else if (templateName.length() > 15) { //todo better way of doing this
            templateNameStr = "Example Name";
        }
        flattenedTemplate += templateNameStr;

        String chordTonesStr = "";
        for (int i = 0; i < NUM_BUTTONS; i++) {
            if (chordTones[i]) {
                System.out.println(chordTonesStr);
                chordTonesStr += ',' + Integer.toString(i);
            }
        }
        if (chordTonesStr.length() == 0) { //todo find better way to do this
            chordTonesStr = ",0";
        }
        flattenedTemplate += chordTonesStr;
        System.out.println(flattenedTemplate);
        return flattenedTemplate;
    }

    public void saveVoicing(View view) {
        if (!validateName(templateName.getText().toString())) {
            return;
        }
        if (!validateVoicing(chordTones)) {
            Toast t = Toast.makeText(this, "Voicing must have at least one voice.", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        String flattenedTemplate = returnFlattenedTemplate();

        if (nameSet.contains(VoicingHelper.getTemplateName(flattenedTemplate))) {
            Toast t = Toast.makeText(this, "Name already taken: Please choose another name.", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        else if (VoicingHelper.getTemplateName(flattenedTemplate).contains("|") || VoicingHelper.getTemplateName(flattenedTemplate).contains(",") ||
                flattenedTemplate.contains(",,")) {
            Toast t = Toast.makeText(this, "Name cannot contain characters '|' or ','.", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        VoicingHelper.addTemplateToPref(getApplicationContext(), flattenedTemplate);
        finish();
    }

    private boolean validateVoicing(boolean[] chordTones) {
        for (int i = 0; i < chordTones.length; i++) {
            if (chordTones[i]) {
                return true;
            }
        }
        return false;
    }

    private boolean validateName(String name) {
        if (nameSet.contains(name)) {
            Toast t = Toast.makeText(this, "Name already taken: Please choose another name.", Toast.LENGTH_LONG);
            t.show();
            return false;
        }

        if (name.contains("|") || name.contains(",")) {
            Toast t = Toast.makeText(this, "Name cannot contain characters ' | ' or ' , '", Toast.LENGTH_LONG);
            t.show();
            return false;
        }

        if (name.length() == 0) {
            Toast t = Toast.makeText(this, "Please enter a name.", Toast.LENGTH_LONG);
            t.show();
            return false;
        }

        return true;
    }

    public void cancelVoicing(View view) {
        finish();
    }

    public int calcInterval(int tag) {
        int toReturn = 48;
        toReturn += MusicTheory.MAJOR_SCALE_SEQUENCE[droneSoundModel.getModeIx()];
        if (tag >= MusicTheory.DIATONIC_SCALE_SIZE) {
            tag -= 7;
            toReturn += 12;
        }
        toReturn += mtc.getModeTemplateForMode(droneSoundModel.getModeIx()).getIntervals()[tag];
        return toReturn;
    }
}
