package com.example.smartdrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartdrone.Models.MidiDriverModel;
import com.example.smartdrone.Models.VoicingModel;

public class VoicingCreatorActivity extends AppCompatActivity {

    private static final int NUM_BUTTONS = 14;
    public static final String SAVED_VOICING_KEY = "saved_voicing_key";

    private boolean[] chordTones;
    private EditText templateName;

    private MidiDriverModel midiDriverModel; //todo add in playback for chord voicing
    private VoicingModel voicingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicing_creator);

        midiDriverModel = new MidiDriverModel(); //todo: add playback to voicing creator activity
        voicingModel = new VoicingModel();


        chordTones = new boolean[NUM_BUTTONS];
        // Initialize root to true
        chordTones[0] = true;

        templateName = findViewById(R.id.voicing_name_edit_text);
        loadButtonData();
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
                    }
                    else {
                        v.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive)); //todo find better way to do this
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
        String flattenedVoicing = returnFlattenedTemplate();
        Intent newTemplate = new Intent();
        newTemplate.putExtra(SAVED_VOICING_KEY, flattenedVoicing);
        setResult(RESULT_OK, newTemplate);
        finish();
    }

    public void cancelVoicing(View view) {
        Intent newTemplate = new Intent();
        newTemplate.putExtra(SAVED_VOICING_KEY, "null");
        setResult(RESULT_OK, newTemplate); //todo find better way to do this
        finish();
    }
}
