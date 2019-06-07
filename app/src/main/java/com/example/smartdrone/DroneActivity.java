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
 *
 * 'What is a drone' you ask?:
 *     https://www.youtube.com/watch?v=8CnhcGpmH9Y
 */

//todo:
// REFACTOR CODE SO THAT CODE PASSES NOTE OBJECTS INSTEAD OF INDICES
// WRITE WRAPPER METHODS TO AVOID DEEP CALLS
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.smartdrone.Models.DroneModel;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashMap;


public class DroneActivity extends AppCompatActivity
        implements MidiDriver.OnMidiStartListener {

    /**
     * Map note name to piano image file name.
     * Key: Note Name.
     * Value: Name of piano image file.
     */
    private HashMap<String, Integer> noteToPianoImgId;

    /**
     * Handles all drone logic.
     */
    private DroneModel droneModel;

    /**
     * Button for toggling state of drone.
     */
    ImageButton controlButton;

    /**
     * Image of piano on drone main screen.
     */
    ImageView piano;

    /**
     * Button that displays active key.
     * Click function will sustain playback of the active drone. //todo make it so this comment isn't a lie
     */
    Button activeKeyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.MESSAGE_LOG_ACTV, "create");
        setContentView(R.layout.activity_drone_main);

        noteToPianoImgId = new HashMap<>();

        // Handles drone logic.
        droneModel = new DroneModel(this);
        // Construct Midi Driver.
        droneModel.getMidiDriverModel().getMidiDriver().setOnMidiStartListener(this);

        // Builds hashmap for note name to piano image name.
        buildPianoMap();

        controlButton = findViewById(R.id.drone_control_button);
        activeKeyButton = findViewById(R.id.active_key_button);
        piano = findViewById(R.id.image_piano);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.drone_preferences, false);

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

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Constants.MESSAGE_LOG_ACTV, "stop");
        if (droneModel.isActive()) {
            droneModel.deactivateDrone();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.MESSAGE_LOG_ACTV, "pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.MESSAGE_LOG_ACTV, "resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.MESSAGE_LOG_ACTV, "destroy");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Constants.MESSAGE_LOG_ACTV, "start");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(Constants.MESSAGE_LOG_ACTV, "restart");
    }

    //todo: save state of drone model when screen is rotated.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            piano.setImageResource(noteToPianoImgId.get(Constants.NOTES_SHARP[noteIx]));
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
        activeKeyButton.setTextSize(64);
        activeKeyButton.setText(Constants.NOTES_FLAT[droneModel
                .getKeyFinderModel().getKeyFinder().getActiveKey().getIx()]);
    }

    /**
     * Toggle state of drone; active or inactive.
     * Updates drawable on toggle button.
     */
    public void toggleDroneState(View view) {
        droneModel.toggleDroneState();

        if (droneModel.isActive()) {
            controlButton.setImageResource(R.drawable.ic_stop_drone);
        }
        else {
            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
    }

    /**
     * Open drone settings activity.
     * @param       view View; view that button is displayed on.
     */
    public void openDroneSettings(View view) {
        // Deactivate drone if active.
        if (droneModel.isActive()) {
            droneModel.deactivateDrone();
            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
        Intent droneSettingsIntent = new Intent(this, DroneSettingsActivity.class);
        startActivity(droneSettingsIntent);
    }

    /**
     * Builds hash map for (note name -> piano image file name).
     */
    public void buildPianoMap() {
        String pianoImgName;
        for (int i = 0; i < 12; i++) {
            pianoImgName = "piano_";
            pianoImgName += Character.toLowerCase(Constants.NOTES_SHARP[i].charAt(0));
            // Note name has accidental.
            if (Constants.NOTES_SHARP[i].length() == 2) {
                pianoImgName += "_sharp";
            }
            int resID = getResources().getIdentifier(pianoImgName, "drawable", getPackageName());
            noteToPianoImgId.put(Constants.NOTES_SHARP[i], resID);
        }
    }
}