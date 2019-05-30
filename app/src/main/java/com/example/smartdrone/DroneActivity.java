/*
 * @SOURCES:
 * Midi Driver         : https://github.com/billthefarmer/mididriver
 * Signal Processing   : https://github.com/JorenSix/TarsosDSP
 * TarsosDSP Example   : https://stackoverflow.com/questions/31231813/tarsosdsp-pitch-analysis-for-dummies
 *
 * The terms midikey and ix are used interchangeably throughout this application.
 * They both refer to the integer that represents the note, though,
 * midikey sometimes references the octave as well as the note name,
 * where ix only refers to the note name; ix only has 12 possible values.
 */

/*
 * TODO MASTER:
 * - SAVE DRONE STATE ON DRONE SETTINGS TAB
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
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.R;

import org.billthefarmer.mididriver.MidiDriver;
import org.w3c.dom.Text;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;


public class DroneActivity extends AppCompatActivity
        implements MidiDriver.OnMidiStartListener {

    public DroneModel droneModel = new DroneModel();


    public static AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
    public static MidiDriver midi;
//    public KeyFinder keyFinder = new KeyFinder();

    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.smartdrone";

    // Used for accessing note names.
    public static final String[] notes =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    public static final String MESSAGE_LOG_ADD        = "note_add";
    public static final String MESSAGE_LOG_REMOVE     = "note_remove";
    public static final String MESSAGE_LOG_LIST       = "note_list";
    public static final String MESSAGE_LOG_SPEED      = "process_speed";
    public static final String MESSAGE_LOG_NOTE_TIMER = "note_timer";

    static final String STATE_KEYFINDER = "stateKeyFinder";

    // Variables for tracking active keys/notes
    int prevActiveKeyIx = -1;
    int curActiveKeyIx = -1;
    int prevAddedNoteIx = -1;
    int curNoteIx = -1;
    boolean droneActive;

    int noteExpirationLength;
    int keyTimerLength;

    int[] drone             = { 0                     };
    int[] majorTriad        = { 0,  7, 16             };
    int[] maj7Voicing       = { 0,  7, 16, 23, 26     };
    int[] gabeVoicing       = { 2, 12, 17, 23         };
    int[] lydianVoicing     = { 5, 12, 19, 26, 33, 40 };
    int[] mixolydianVoicing = { 7, 17, 21, 24, 28, 33 };
    int[] phrygianVoicing   = { 4, 17, 21, 23, 28     };
    int[][] voicings = {
            drone,
            majorTriad,
            maj7Voicing,
            gabeVoicing,
            lydianVoicing,
            mixolydianVoicing,
            phrygianVoicing};

    int userModeIx = 0;
    String[] userModeName = {
            "Drone",
            "Major Triad",
            "Major7",
            "Gabe",
            "Lydian",
            "Mixolydian",
            "Phrygian", };

    // Used to keep track how long a note was heard.
    public long timeRegistered;
    public static int noteLengthRequirement;

    public int midiVolume;

    Button expirationButton;
    Button keyTimerButton;
    Button noteLengthRequirementButton;
    Button userModeButton;
    Button volumeButton;
    ImageButton playButton;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    // TODO: Add user parameter.
    public static int plugin = 52;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        droneActive = false;
        playButton = findViewById(R.id.control_drone_button);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();

        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        prevAddedNoteIx = -1;
        curNoteIx = -1;

        // The amount of time a note must be registered for until it is added to the active note list.
        noteLengthRequirement = mPreferences.getInt(DroneSettingsActivity.NOTE_LEN_KEY, 60);
        Log.d("noteLenFilter", Integer.toString(noteLengthRequirement));

        Log.d("notelen", Integer.toString(noteLengthRequirement));

        keyTimerLength = mPreferences.getInt(DroneSettingsActivity.KEY_SENS_KEY, 3);
        droneModel.getKeyFinder().setKeyTimerLength(keyTimerLength);
        droneModel.getKeyFinder().setNoteTimerLength(2);

        // Button for Note Timer
        expirationButton = (Button) findViewById(R.id.expirationButton);
        noteExpirationLength = droneModel.getKeyFinder().getNoteTimerLength();
        expirationButton.setText("" + noteExpirationLength);

        // Button for Key timer.
        keyTimerButton = (Button) findViewById(R.id.keyTimerButton);
        // keyTimerLength = droneModel.getKeyFinder().getKeyTimerLength();
        keyTimerButton.setText("" + keyTimerLength);

        // Button for note length requirement.
        noteLengthRequirementButton = (Button) findViewById(R.id.noteLengthTimerButton);
        noteLengthRequirementButton.setText("" + noteLengthRequirement);

        // User mode button
        userModeIx = 0;
        userModeButton = (Button) findViewById(R.id.userModeButton);
        userModeButton.setText(userModeName[userModeIx]);

        midiVolume = 65;
        volumeButton = findViewById(R.id.volumeButton);
        volumeButton.setText("" + midiVolume);

        // Construct Midi Driver.
        midi = new MidiDriver();
        midi.setOnMidiStartListener(this);

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this, R.xml.drone_preferences, false);

        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String noteLenPref = sharedPref.getString(DroneSettingsActivity.NOTE_LEN_KEY, "60");
        String keySensPref = sharedPref.getString(DroneSettingsActivity.KEY_SENS_KEY, "3");
        noteLengthRequirement = Integer.parseInt(noteLenPref);
        keyTimerLength = Integer.parseInt(keySensPref);
        noteLengthRequirementButton.setText(noteLenPref);
        keyTimerButton.setText(keySensPref);
        // Toast.makeText(this, switchPref/*.toString()*/, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        if (midi != null)
//            midi.start();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (midi != null) {
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
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(noteIx);
        droneModel.getKeyFinder().addNoteToList(curNote);
        Log.d(MESSAGE_LOG_ADD, curNote.getName());
        Log.d(MESSAGE_LOG_LIST, droneModel.getKeyFinder().getActiveNotes().toString());
        prevAddedNoteIx = noteIx;

        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, droneModel.getKeyFinder().getActiveNotes().toString()); // active note list
    }

    /**
     * Converts pitch (hertz) to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    public int convertPitchToIx(double pitchInHz) {
        // No note is heard.
        if (pitchInHz == -1) {
            return -1;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz) % 12;
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
            setNoteText(notes[convertPitchToIx(pitchInHz)]);
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
     * Utilizes other single purpose methods.
     * 1. Converts pitch to ix.
     * 2. Adds note (based on ix) to active note list.
     * 3. Updates the text views on screen.
     * @param       pitchInHz float; current pitch being heard.
     */
    public void processPitch(float pitchInHz) {
        // Convert pitch to midi key.
        int curKey = convertPitchToIx((double) pitchInHz); // No note will return -1

        // Debug statement to see how fast the engine runs.
        // if (midiKey != -1) {
        //Log.d(MESSAGE_LOG_SPEED, "Processing: " + droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(midiKey));
        // }

        // Note change is detected.
        if (curKey != prevAddedNoteIx) {
            // If previously added note is no longer heard.
            if (prevAddedNoteIx != -1) {
                // Start timer.
                droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(droneModel.getKeyFinder(), noteExpirationLength);
                Log.d(MESSAGE_LOG_NOTE_TIMER, droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).getName() + ": Started");
            }
            // No note is heard.
            if (pitchInHz == -1) {
                curNoteIx = -1;
                prevAddedNoteIx = -1;
            }
            // Different note is heard.
            else if (curKey != curNoteIx) {
                curNoteIx = curKey;
                timeRegistered = System.currentTimeMillis();
            }
            // Current note is heard.
            else if (noteMeetsConfidence()) {
                addNote(curKey);
                droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
                Log.d(MESSAGE_LOG_NOTE_TIMER, droneModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).getName() + ": Cancelled");
            }
        }
        // Note removal detected.
        if (droneModel.getKeyFinder().getNoteHasBeenRemoved()) {
            droneModel.getKeyFinder().setNoteHasBeenRemoved(false);
            Log.d(MESSAGE_LOG_REMOVE, droneModel.getKeyFinder().getRemovedNote().getName());
            Log.d(MESSAGE_LOG_LIST, droneModel.getKeyFinder().getActiveNotes().toString());
        }
        // If active key has changed.
        if (droneModel.getKeyFinder().getActiveKeyHasChanged()) {
            playActiveKeyNote();
        }
        // Update text views.
        setPitchText(pitchInHz);
        setNoteText(pitchInHz);
    }

    public boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > noteLengthRequirement;
    }

    /**
     * Start the simulation activity.
     * @param       view View; current view.
     */
    public void simulationActivity(View view) {
        Intent intent = new Intent(this, SimulationActivity.class);
        startActivity(intent);
    }

    /**
     * Plays the tone(s) of the current active key.
     */
    public void playActiveKeyNote() {
        prevActiveKeyIx = curActiveKeyIx;
        if (droneModel.getKeyFinder().getActiveKey() == null) {
            return;
        }
        if (!droneActive) {
            return;
        }
        curActiveKeyIx = droneModel.getKeyFinder().getActiveKey().getIx() + 36; // 36 == C
        if (prevActiveKeyIx != curActiveKeyIx) {
            printActiveKeyToScreen(); // FOR TESTING

            /*
            //TODO: Send everything as an array (work for any number of notes)
            // Stop the current note.
            sendMidi(0X80, prevActiveKeyIx + modeOffset, 0);
            // Start the new note.
            sendMidi(0X90, curActiveKeyIx + modeOffset, 63);
            */

            sendMidiChord(0X80, voicings[userModeIx], 0, prevActiveKeyIx);
            sendMidiChord(0X90, voicings[userModeIx], midiVolume, curActiveKeyIx);
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
        sendMidiSetup();
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Initial setup data for midi.
     */
    protected void sendMidiSetup() {
        byte msg[] = new byte[2];
        msg[0] = (byte) 0XC0;    // 0XC0 == PROGRAM CHANGE
        msg[1] = (byte) plugin;
        midi.write(msg);
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Send data that is to be synthesized by midi driver.
     * @param       event int; type of event.
     * @param       midiKey int; index of note (uses octaves).
     * @param       volume int; volume of note.
     */
    protected void sendMidi(int event, int midiKey, int volume) {
        byte msg[] = new byte[3];
        msg[0] = (byte) event;
        msg[1] = (byte) midiKey;
        msg[2] = (byte) volume;
        midi.write(msg);
    }

    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    protected void sendMidiChord(int event, int[] midiKeys, int volume) {
        for (int key : midiKeys) {
            sendMidi(event, key, volume);
        }
    }

    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    protected void sendMidiChord(int event, int[] midiKeys, int volume, int rootIx) {
        int octaveAdjustment = 0;
        if (midiKeys[0] + rootIx > 47) {
            octaveAdjustment = -12;
        }

        for (int key : midiKeys) {
            sendMidi(event, key + rootIx + octaveAdjustment, volume);
        }
    }

    /* TEST VOICINGS */

    protected void sendMidiChordMajor(int event, int midiKey, int volume) {
        sendMidi(event, midiKey + MusicTheory.MAJOR_TRAID_SEQUENCE[0], volume);
        sendMidi(event, midiKey + MusicTheory.MAJOR_TRAID_SEQUENCE[1] + 12, volume);
        sendMidi(event, midiKey + MusicTheory.MAJOR_TRAID_SEQUENCE[2], volume);
    }

    protected void sendMidiChordPhrygian(int event, int midiKey, int volume) {
        sendMidi(event, midiKey, volume);
        sendMidi(event, midiKey + 13, volume);
        sendMidi(event, midiKey + 17, volume);
        sendMidi(event, midiKey + 19, volume);
    }

    protected void sendMidiChordDorian(int event, int midiKey, int volume) {
        sendMidi(event, midiKey, volume);
        sendMidi(event, midiKey + 7, volume);
        sendMidi(event, midiKey + 17, volume);
        sendMidi(event, midiKey + 22, volume);
        sendMidi(event, midiKey + 27, volume);
        sendMidi(event, midiKey + 31, volume);
    }

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + droneModel.getKeyFinder().getActiveKey().getName());
    }

    public void incrementNoteExpiration(View view) {
        noteExpirationLength = (noteExpirationLength % 5) + 1;
        droneModel.getKeyFinder().setNoteTimerLength(noteExpirationLength);
        expirationButton.setText("" + noteExpirationLength);
    }

    public void incrementKeyTimer(View view) {
        keyTimerLength = (keyTimerLength % 5) + 1;
        droneModel.getKeyFinder().setKeyTimerLength(keyTimerLength);
        keyTimerButton.setText("" + keyTimerLength);
    }

    public void incrementNoteLengthRequirement(View view) {
        noteLengthRequirement = (noteLengthRequirement + 15) % 165;
        noteLengthRequirementButton.setText("" + noteLengthRequirement);
    }

    public void changeUserMode(View view) {
        sendMidiChord(0X80, voicings[userModeIx], 0, curActiveKeyIx);
        userModeIx = (userModeIx + 1) % voicings.length;
        userModeButton.setText(userModeName[userModeIx]);
        sendMidiChord(0X90, voicings[userModeIx], 63, curActiveKeyIx);
        // Log.d(MESSAGE_LOG_REMOVE, "hi");
    }

    public void incrementVolume(View view) {
        midiVolume = (midiVolume + 5) % 105;
        sendMidiChord(0X80, voicings[userModeIx], 0, curActiveKeyIx);
        sendMidiChord(0X90, voicings[userModeIx], midiVolume, curActiveKeyIx);
        volumeButton.setText("" + midiVolume);
    }

    public void controlDrone(View view) {
        if (droneActive) {
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
        if (midi != null) {
            midi.start();
            droneActive = true;
            playButton.setImageResource(R.drawable.ic_stop_drone);
        }
    }

    /**
     * Stop tone(s) being produced by drone.
     */
    public void stopDrone() {
        if (!droneActive) {
            return;
        }
        if (midi != null) {
            midi.stop();
            droneActive = false;
            droneModel.getKeyFinder().cleanse();
            TextView tv = findViewById(R.id.activeKeyPlainText);
            tv.setText("Active Key: ");
            playButton.setImageResource(R.drawable.ic_play_drone);
        }
    }

    public void openDroneSettings(View view) {
        stopDrone();
        Intent intent = new Intent(this, DroneSettingsActivity.class);
        startActivity(intent);
        playDrone(); // TODO: this line is playing the drone before the second activity starts
    }
}
