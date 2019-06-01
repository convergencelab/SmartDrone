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
// Use listeners to update active key text on drone activity

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.smartdrone.Models.DroneModel;

import org.billthefarmer.mididriver.MidiDriver;


public class DroneActivity extends AppCompatActivity
        implements MidiDriver.OnMidiStartListener {

    private DroneModel droneModel;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.smartdrone";

    public int noteExpirationLength;
    int keyTimerLength;

    public int noteLengthRequirement;

    ImageButton controlButton;
    TextView activeKeyText;
    TextView pitchText;
    TextView noteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_main);

        // Handles drone logic.
        droneModel = new DroneModel(this);
        droneModel.startPitchProcessor();

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        // Activate/Deactivate Drone toggle button.
        controlButton = findViewById(R.id.drone_control_button);

        // Text Views.
        activeKeyText = findViewById(R.id.activeKeyPlainText);
        pitchText     = findViewById(R.id.pitchText);
        noteText      = findViewById(R.id.noteText);

        // Get note filter len from preferences.
        noteLengthRequirement = mPreferences.getInt(DroneSettingsActivity.NOTE_LEN_KEY,
                Constants.NOTE_FILTER_LENGTH_DEFAULT);
        // Set filter length.
        noteExpirationLength = droneModel.getKeyFinderModel().getKeyFinder().getNoteTimerLength();

        // Get key sens len from preferences.
        keyTimerLength = mPreferences.getInt(DroneSettingsActivity.KEY_SENS_KEY,
                Constants.KEY_SENS_DEFAULT);
        // Set active key sensitivity.
        droneModel.getKeyFinderModel().getKeyFinder().setKeyTimerLength(keyTimerLength);

        // Construct Midi Driver.
        droneModel.getMidiDriverModel().getMidiDriver().setOnMidiStartListener(this);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.drone_preferences, false);

        // TODO: magic code that controls and saves user preferences
        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String noteLenPref = sharedPref.getString(DroneSettingsActivity.NOTE_LEN_KEY, "60"); // default value for noteLenFilter
        String keySensPref = sharedPref.getString(DroneSettingsActivity.KEY_SENS_KEY, "3");  // default value for key sensitivity
        noteLengthRequirement = Integer.parseInt(noteLenPref);
        keyTimerLength = Integer.parseInt(keySensPref);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (droneModel.getMidiDriverModel().getMidiDriver() != null) {
            droneModel.deactivateDrone();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

//        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        // preferencesEditor.putInt(COUNT_KEY, mCount); // THIS LINE TO PUT PREFERENCES
        // preferencesEditor.apply();                   // APPLY CHANGES
    }

    /**
     * Update the note text on screen.
     * @param       newNote String; name of note.
     */
    public void setNoteText(String newNote) {
        noteText.setText(newNote);
    }

    /**
     * Update the note text on screen.
     * @param       pitchInHz double; pitch of note (hertz).
     */
    public void setNoteText(double pitchInHz) {
        if (pitchInHz != -1) {
            setNoteText(Constants.notes[droneModel.convertPitchToIx(pitchInHz)]); //todo can be better
        } else {
            setNoteText("");
        }
    }

    /**
     * Update the pitch text on screen.
     * @param       pitchInHz double; pitch of note (hertz).
     */
    public void setPitchText(double pitchInHz) {
        pitchText.setText("" + (int) pitchInHz);
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
        activeKeyText.setText("Active Key: " + droneModel.getKeyFinderModel().getKeyFinder().getActiveKey().getName());
    }

    public void toggleDrone(View view) {
        // Function toggles drone Active/Inactive.
        droneModel.toggleDrone();
        // Drone now active.
        if (droneModel.isDroneActive()) {
            controlButton.setImageResource(R.drawable.ic_stop_drone);
        }
        // Drone now inactive.
        else {
            activeKeyText.setText("Active Key: ");
            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
    }

    public void openDroneSettings(View view) {
        Intent droneSettingsIntent = new Intent(this, DroneSettingsActivity.class);
        startActivity(droneSettingsIntent);
    }

    public void changeVoicing(View view) {
        droneModel.changeUserMode();
    }
}