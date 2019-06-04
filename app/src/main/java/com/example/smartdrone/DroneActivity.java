/*
 * @SOURCES:
 * Midi Driver         : https://github.com/billthefarmer/mididriver
 * Signal Processing   : https://github.com/JorenSix/TarsosDSP
 * TarsosDSP Example   : https://stackoverflow.com/questions/31231813/tarsosdsp-pitch-analysis-for-dummies
 *
 * The terms 'midikey' and 'ix' are used interchangeably throughout this application.
 * They both refer to the integer that represents the note, though,
 * midikey sometimes references the octave as well as the note name,
 * where ix only refers to the note name; ix only has 12 possible values.
 */

//todo:
// SAVE DRONE STATE ON DRONE SETTINGS ACTIVITY STARTED
// MOVE NOTE/PITCH DISPLAY TO ABSTRACT PIANO

// Display notes with correct enharmonic spelling; ex: (C# -> Db), (A# -> Bb)
// Make it so if main activity is stopped; pitch processing does not happen
// debug accuracy of note detection.
// look into api for signal filtering.
// add feature that active keys can generate chords.
// make drone settings affect drone
// convert activity into fragments
// make drone settings stretch across full width of screen
// request user permission for audio recording
// user preference for screen staying on during use

package com.example.smartdrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdrone.Models.DroneModel;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashMap;


public class DroneActivity extends AppCompatActivity
        implements MidiDriver.OnMidiStartListener {

    private final static HashMap<String, String> nameToResIdName = new HashMap<>();

    private DroneModel droneModel;

    ImageButton controlButton;
    ImageView piano;
    TextView activeKeyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_main);

        // Handles drone logic.
        droneModel = new DroneModel(this);

        // Builds hashmap for note name to piano image name.
        createPianoMap();

        // Activate/Deactivate Drone toggle button.
        controlButton = findViewById(R.id.drone_control_button);

        // Text Views.
        activeKeyText = findViewById(R.id.activeKeyPlainText);
        piano = findViewById(R.id.image_piano);

        // Construct Midi Driver.
        droneModel.getMidiDriverModel().getMidiDriver().setOnMidiStartListener(this);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.drone_preferences, false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (droneModel.getMidiDriverModel().getMidiDriver() != null) {
            droneModel.deactivateDrone();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //todo: magic code that controls and saves user preferences
        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);

        //todo: make ints by default so no conversion is necessary
        String noteLenPref = sharedPref.
                getString(DroneSettingsActivity.NOTE_LEN_KEY, "60");
        String keySensPref = sharedPref.
                getString(DroneSettingsActivity.KEY_SENS_KEY, "3");

        // Update fields to match user saved preferences.
        int noteLengthRequirement = Integer.parseInt(noteLenPref);
        int keyTimerLength = Integer.parseInt(keySensPref);
        droneModel.getKeyFinderModel().getKeyFinder().setKeyTimerLength(keyTimerLength);
        droneModel.getPitchProcessorModel().noteFilterLength = noteLengthRequirement;

        droneModel.startDroneProcess();
    }

    /**
     * Update the piano image on screen.
     * @param       noteIx int; index of note.
     */
    public void setPianoImage(int noteIx) {
        if (noteIx == Constants.NULL_NOTE_IX) {
            piano.setImageResource(R.drawable.piano_null);
        }
        else {
            String piano_text = nameToResIdName.get(Constants.NOTES_SHARP[noteIx]);
            int resID = getResources().getIdentifier(piano_text, "drawable", getPackageName());
            piano.setImageResource(resID);
        }
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Listener for sending initial midi messages when the Sonivox
     * synthesizer has been started, such as program change.
     */
    @Override
    public void onMidiStart() {
        droneModel.getMidiDriverModel().sendMidiSetup();
    }

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        activeKeyText.setText("Active Key: " + Constants.NOTES_FLAT[droneModel
                .getKeyFinderModel().getKeyFinder().getActiveKey().getIx()]);
    }

    /**
     * Toggle state of drone; active or inactive.
     * Updates drawable on toggle button.
     */
    public void toggleDrone(View view) {
        droneModel.toggleDrone();

        if (droneModel.droneIsActive()) {
            controlButton.setImageResource(R.drawable.ic_stop_drone);
        }
        else {
            activeKeyText.setText("Active Key: ");
            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
    }

    /**
     * Open drone settings activity.
     * @param       view View; view that button is displayed on.
     */
    public void openDroneSettings(View view) {
        // Deactivate drone if active.
        if (droneModel.droneIsActive()) {
            droneModel.deactivateDrone();
        }
        Intent droneSettingsIntent = new Intent(this, DroneSettingsActivity.class);
        startActivity(droneSettingsIntent);
    }

    /**
     * Toggle to next voicing.
     * @param       view View; view that button is displayed on.
     */
    public void changeVoicing(View view) {
        droneModel.changeUserVoicing();
    }

    /**
     * Builds hashmap for getting name of piano image.
     */
    public void createPianoMap() {
        String str;
        for (int i = 0; i < 12; i++) {
            str = "piano_";
            str += Character.toLowerCase(Constants.NOTES_SHARP[i].charAt(0));
            if (Constants.NOTES_SHARP[i].length() == 2) {
                str += "_sharp";
            }
            nameToResIdName.put(Constants.NOTES_SHARP[i], str);
        }
    }
}