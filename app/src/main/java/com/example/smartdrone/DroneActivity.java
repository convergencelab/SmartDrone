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

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartdrone.Models.DroneModel;
import com.example.smartdrone.Utility.DronePreferences;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashMap;


public class DroneActivity extends AppCompatActivity {

    public static final String CUR_TEMP_KEY = "curTemplate";
    public static final String ALL_TEMP_KEY = "allTemplates";
    public static final String ACTIVE_KEY_IX_KEY = "active_key_ix";

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

    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.MESSAGE_LOG_ACTV, "create");
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

        /* Moved code here in case activity is not destroyed after changing preferences. */

        //todo: magic code that controls and saves user preferences
        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);

        edit = sharedPref.edit();
//        edit.clear();
//        edit.apply();

        //todo: make ints by default so no conversion is necessary
        String noteLenPref = sharedPref
                .getString(DroneSettingsActivity.NOTE_LEN_KEY, "60");
        String keySensPref = sharedPref
                .getString(DroneSettingsActivity.KEY_SENS_KEY, "3");
//        int userModeIx = sharedPref
//                .getInt(DroneSoundActivity.USER_MODE_KEY, 0);
        int userModeIx = DronePreferences.getStoredModePref(this);
        int userPluginIx = sharedPref
                .getInt(DroneSoundActivity.USER_PLUGIN_KEY, 0);
//        boolean userBassNotePref = sharedPref
//                .getBoolean(DroneSoundActivity.BASSNOTE_KEY, true);
        boolean userBassNotePref = DronePreferences.getStoredBassPref(this);
        String defTemplate = sharedPref
                .getString(CUR_TEMP_KEY, Constants.DEFAULT_TEMPLATE);


        //todo test code, remove when ready

        // Update fields to match user saved preferences.
        int noteLengthRequirement = Integer.parseInt(noteLenPref);
        int keyTimerLength = Integer.parseInt(keySensPref);
//        int userPlugin = Integer.parseInt(userPluginPref);
        droneModel.getKeyFinderModel().getKeyFinder().setKeyTimerLength(keyTimerLength);
        droneModel.getPitchProcessorModel().noteFilterLength = noteLengthRequirement;
        droneModel.setUserModeIx(userModeIx);
        droneModel.getMidiDriverModel().setPlugin(Constants.PLUGIN_INDICES[userPluginIx]);
        droneModel.sethasBassNote(userBassNotePref);
        droneModel.setCurTemplate(VoicingHelper.inflateTemplate(defTemplate)); //todo this is template code

        resetDroneScreen();
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
        activeKeyButton.setTextSize(22);
        String activeKeyStr = "";
        //todo refactor line below
        int spellingCode = droneModel.getKeyFinderModel().getKeyFinder().getActiveKey().getSpellingCode();
        activeKeyStr = droneModel.getKeyFinderModel().getKeyFinder().getActiveKey().getDegree(droneModel.getUserModeIx()).getName(spellingCode);
        String fullName = activeKeyStr + "\n" + MusicTheory.MAJOR_MODE_NAMES[droneModel.getUserModeIx()];
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
     * @param view
     */
    public void activeKeyClick(View view) {
        // Start drone
        //todo: add some sort of visual feedback that active key button has been clicked
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
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

    }
}