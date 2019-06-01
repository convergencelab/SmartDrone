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

/*
 * TODO MASTER:
 * - SAVE DRONE STATE ON DRONE SETTINGS ACTIVITY STARTED
 * - MOVE NOTE/PITCH DISPLAY TO ABSTRACT PIANO
 * - Display notes with correct enharmonic spelling; ex: (C# -> Db), (A# -> Bb)
 * - Make it so if main activity is stopped; pitch processing does not happen
 * - debug accuracy of note detection.
 * - look into api for signal filtering.
 * - add feature that active keys can generate chords.
 * - make drone settings affect drone
 * - convert activity into fragments
 * - make drone settings stretch across full width of screen
 * - request user permission for audio recording
 * - user preference for screen staying on during use
 */

package com.example.smartdrone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.smartdrone.Models.DroneModel;

import org.billthefarmer.mididriver.MidiDriver;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class DroneActivity extends AppCompatActivity
        implements MidiDriver.OnMidiStartListener {

    public DroneModel droneModel;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.smartdrone";

    static final String STATE_KEYFINDER = "stateKeyFinder";

    public int noteExpirationLength;
    int keyTimerLength;


//    int[] drone             = { 0                     };
//    int[] majorTriad        = { 0,  7, 16             };
//    int[] maj7Voicing       = { 0,  7, 16, 23, 26     };
//    int[] gabeVoicing       = { 2, 12, 17, 23         };
//    int[] lydianVoicing     = { 5, 12, 19, 26, 33, 40 };
//    int[] mixolydianVoicing = { 7, 17, 21, 24, 28, 33 };
//    int[] phrygianVoicing   = { 4, 17, 21, 23, 28     };
//    public int[][] voicings = {
//            drone,
//            majorTriad,
//            maj7Voicing,
//            gabeVoicing,
//            lydianVoicing,
//            mixolydianVoicing,
//            phrygianVoicing};
//
//    public String[] userModeName = {
//            "Drone",
//            "Major Triad",
//            "Major7",
//            "Gabe",
//            "Lydian",
//            "Mixolydian",
//            "Phrygian"
//    };

    public int noteLengthRequirement;

    public int midiVolume;

    public Button userModeButton;
    ImageButton playButton;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    // TODO: Add user parameter.
//    public int plugin = 52;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone_main);

        droneModel = new DroneModel(this);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        playButton = findViewById(R.id.drone_control_button);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        droneModel.processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        droneModel.getDispatcher().addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(droneModel.getDispatcher(), "Audio Thread");
        audioThread.start();

        // The amount of time a note must be registered for until it is added to the active note list.
        noteLengthRequirement = mPreferences.getInt(DroneSettingsActivity.NOTE_LEN_KEY, 60);
        Log.d("noteLenFilter", Integer.toString(noteLengthRequirement));

        Log.d("notelen", Integer.toString(noteLengthRequirement));

        keyTimerLength = mPreferences.getInt(DroneSettingsActivity.KEY_SENS_KEY, 3);
        droneModel.getKeyFinder().setKeyTimerLength(keyTimerLength);
        droneModel.getKeyFinder().setNoteTimerLength(2);

        noteExpirationLength = droneModel.getKeyFinder().getNoteTimerLength();


        // User mode button
//        userModeButton = (Button) findViewById(R.id.userModeButton);
//        userModeButton.setText(userModeName[droneModel.getUserModeIx()]);

        midiVolume = 65;

        // Construct Midi Driver.
        droneModel.getMidiDriver().setOnMidiStartListener(this);

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
        if (droneModel.getMidiDriver() != null) {
            stopDrone();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        // preferencesEditor.putInt(COUNT_KEY, mCount); // THIS LINE TO PUT PREFERENCES
        // preferencesEditor.apply();                   // APPLY CHANGES
    }

    /**
     * Update the note text on screen.
     * @param       newNote String; name of note.
     */
    public void setNoteText(String newNote) {
        TextView noteText = (TextView) findViewById(R.id.noteText);
        noteText.setText(newNote);
    }

    /**
     * Update the note text on screen.
     * @param       pitchInHz double; pitch of note (hertz).
     */
    public void setNoteText(double pitchInHz) {
        if (pitchInHz != -1) {
            setNoteText(Constants.notes[droneModel.convertPitchToIx(pitchInHz)]); // todo
        } else {
            setNoteText("");
        }
    }

    /**
     * Update the pitch text on screen.
     * @param       pitchInHz double; pitch of note (hertz).
     */
    public void setPitchText(double pitchInHz) {
        TextView pitchText = (TextView) findViewById(R.id.pitchText); //TODO optimize
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
        droneModel.sendMidiSetup();
    }

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + droneModel.getKeyFinder().getActiveKey().getName());
    }

    public void controlDrone(View view) {
        if (droneModel.isDroneActive()) {
            stopDrone();
        }
        else {
            playDrone();
        }
    }

    /**
     * Start tone(s) being produced by drone.
     */
    public void playDrone() {
        if (droneModel.getMidiDriver() != null) {
            droneModel.getMidiDriver().start();
//            droneActive = true;
            droneModel.setIsDroneActive(true);
            playButton.setImageResource(R.drawable.ic_stop_drone);
        }
        Log.d("prefs", "keyTimer: " + Integer.toString(keyTimerLength)
                + "; filter len: " + Integer.toString(noteLengthRequirement));
    }

    /**
     * Stop tone(s) being produced by drone.
     */
    public void stopDrone() {
        if (!droneModel.isDroneActive()) {
            return;
        }
        if (droneModel.getMidiDriver() != null) {
            droneModel.getMidiDriver().stop();
            droneModel.setIsDroneActive(false);
            droneModel.getKeyFinder().cleanse();
            TextView tv = findViewById(R.id.activeKeyPlainText);
            tv.setText("Active Key: ");
            playButton.setImageResource(R.drawable.ic_play_drone);
        }
    }

    public void openDroneSettings(View view) {
        Intent intent = new Intent(this, DroneSettingsActivity.class);
        startActivity(intent);
    }

    public void changeVoicing(View view) {
        droneModel.changeUserMode();
    }
}