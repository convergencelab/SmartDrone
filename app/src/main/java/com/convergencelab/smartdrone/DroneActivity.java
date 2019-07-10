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

package com.convergencelab.smartdrone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.convergencelab.smartdrone.Models.DroneModel;
import com.convergencelab.smartdrone.Utility.DronePreferences;
import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.MusicTheory;

import java.util.HashMap;


public class DroneActivity extends AppCompatActivity {

    private int MICROPHONE_PERMISSION_CODE = 1;

    /**
     * Map note name to piano image file name.
     * Key: Note Name.
     * Value: Name of piano image file.
     */
    private HashMap<String, String> noteToResIdName;

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
    ImageView pianoImage;

    /**
     * Button that displays active key.
     * Click function will sustain playback of the active drone. //todo make it so this comment isn't a lie
     */
    Button activeKeyButton;

    String[] modeNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drone_main);

        if (ContextCompat.checkSelfPermission(DroneActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicrophonePermission();
        }

        noteToResIdName = new HashMap<>();
        inflatePianoMap();

        // Handles drone logic.
        droneModel = new DroneModel(this);

        controlButton = findViewById(R.id.drone_control_button);
        activeKeyButton = findViewById(R.id.active_key_button);
        pianoImage = findViewById(R.id.image_piano);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (droneModel.isActive()) {
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

        String noteLenPref = DronePreferences.getNoteFilterLenPref(getApplicationContext());
        String keySensPref = DronePreferences.getActiveKeySensPref(getApplicationContext());
        int userParentScaleCode = DronePreferences.getStoredParentScalePref(getApplicationContext());
        int userModeIx = DronePreferences.getStoredModePref(getApplicationContext());
        int userPluginIx = DronePreferences.getStoredPluginPref(getApplicationContext());
        boolean userBassNotePref = DronePreferences.getStoredBassPref(getApplicationContext());
        String defTemplate = DronePreferences.getCurTemplatePref(getApplicationContext());

        // Update fields to match user saved preferences.
        int noteLengthRequirement = Integer.parseInt(noteLenPref);
        int keyTimerLength = Integer.parseInt(keySensPref);

        droneModel.getKeyFinderModel().getKeyFinder().setKeyTimerLength(keyTimerLength);
        droneModel.getPitchProcessorModel().noteFilterLength = noteLengthRequirement;
        droneModel.getKeyFinderModel().getKeyFinder().setActiveKeyList(userParentScaleCode);
        droneModel.setUserModeIx(userModeIx);
        droneModel.getMidiDriverModel().setPlugin(Constants.PLUGIN_INDICES[userPluginIx]);
        droneModel.sethasBassNote(userBassNotePref);

        droneModel.setCurTemplate(VoicingHelper.inflateTemplate(defTemplate));


        resetDroneScreen();

        updateModeNames();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onRestart() {
        super.onRestart();
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
            pianoImage.setImageResource(R.drawable.piano_null);
        }
        else {
            String piano_text = noteToResIdName.get(Constants.NOTES_SHARP[noteIx]);
            int resID = getResources().getIdentifier(piano_text, "drawable", getPackageName());
            pianoImage.setImageResource(resID);
        }
    }

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        activeKeyButton.setTextSize(14);
        String activeKeyStr = "";
        //todo refactor line below
        int spellingCode = droneModel.getKeyFinderModel().getKeyFinder().getActiveKey().getSpellingCode();
        activeKeyStr = droneModel.getKeyFinderModel().getKeyFinder().getActiveKey().getDegree(droneModel.getUserModeIx()).getName(spellingCode);
        String fullName = activeKeyStr + "\n" + modeNames[droneModel.getUserModeIx()]; //todo make this line dynamic
        SpannableString ss = new SpannableString(fullName);
        ss.setSpan(new RelativeSizeSpan(3f), 0, activeKeyStr.length(), 0);
        activeKeyButton.setText(ss);
    }

    /**
     * Toggle state of drone; active or inactive.
     * Updates drawable on toggle button.
     */
    public void toggleDroneState(View view) {
        if (ContextCompat.checkSelfPermission(DroneActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicrophonePermission();
        }
        else {
            droneModel.toggleDroneState();
            if (droneModel.isActive()) {
                controlButton.setImageResource(R.drawable.ic_stop_drone);
                activeKeyButton.setTextSize(64);
                activeKeyButton.setText("...");
                activeKeyButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_active)); //todo find better way to do this
            } else {
                controlButton.setImageResource(R.drawable.ic_play_drone);
                activeKeyButton.setTextSize(48);
                activeKeyButton.setText("Start");
                activeKeyButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive)); //todo find better way to do this
            }
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

    public void openSoundSettings(View view) {
        if (droneModel.isActive()) {
            droneModel.deactivateDrone();
            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
        Intent intent = new Intent(this, DroneSoundActivity.class); //todo finish activity
        startActivity(intent);
    }

    /**
     * Builds hash map for (note name -> piano image file name).
     */
    public void inflatePianoMap() {
        String str;
        for (int i = 0; i < 12; i++) {
            str = "piano_";
            str += Character.toLowerCase(Constants.NOTES_SHARP[i].charAt(0));
            if (Constants.NOTES_SHARP[i].length() == 2) {
                str += "_sharp";
            }
            noteToResIdName.put(Constants.NOTES_SHARP[i], str);
        }
    }

    /**
     * Controls click functionality of active key button.
     * Starts drone if drone stopped.
     * Sustains chord if drone active.
     * @param       view View; active key button.
     */
    public void activeKeyClick(View view) {

        if (!droneModel.isActive()) {
            toggleDroneState(view);
        }
        //todo: else -> sustain drone
    }

    private void resetDroneScreen() {
        if (droneModel.isActive()) {
            droneModel.toggleDroneState();
        }
        pianoImage.setImageResource(R.drawable.piano_null);
        controlButton.setImageResource(R.drawable.ic_play_drone);
        activeKeyButton.setTextSize(48);
        activeKeyButton.setText("Start");
        activeKeyButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive)); //todo find better way to do this
    }

    public void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed for drone.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DroneActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void updateModeNames() {
        int code = DronePreferences.getStoredParentScalePref(getApplicationContext());
        if (code == KeyFinder.CODE_MAJOR) {
            modeNames = MusicTheory.MAJOR_MODE_NAMES;
        }
        else {
            modeNames = MusicTheory.MELODIC_MINOR_MODE_NAMES;
        }
    }
}