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
 * TODO:
 * - debug accuracy of note detection.
 * - add user parameter for sensitivity.
 **    - add user parameter than decides how long a note stays in the cue before it expires.
 *       (default = 5)
 **    - update java library so new key has to take over
 * - add user parameter for mode.
 * - fix sound distortion bug when switching activities.
 * - improve functionality.
 **    - note must be heard for a variable amount of time before it's added to list
 *       (to prevent adding erroneous notes)
 * - look into api for signal filtering.
 * - Debug features that displays the current active notes on the screen
 *   (or logcat?)
 * - add feature that active keys can generate chords.
 * - make it so note timer doesn't start for curnote until another note is detected
 * - Find out how to create listener for note being removed from list (expired).
 */

package com.example.smartdrone;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity
    implements MidiDriver.OnMidiStartListener {

    public AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
    public MidiDriver midi;
    public static KeyFinder keyFinder = new KeyFinder();

    // Used for accessing note names.
    public static final String[] notes =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    public static final String MESSAGE_LOG_ADD        = "mainActivityDebugAdd";
    public static final String MESSAGE_LOG_REMOVE     = "mainActivityDebugRemove";
    public static final String MESSAGE_LOG_LIST       = "mainActivityDebugList";
    public static final String MESSAGE_LOG_SPEED      = "mainActivityDebugSpeed";
    public static final String MESSAGE_LOG_NOTE_TIMER = "mainActivityDebugNTimer";

    // Variables for tracking active keys/notes
    int prevActiveKey = -1;
    int curActiveKey = -1;
    int prevAddedNote = -1; //TODO Refactor; should have ix in variable name.
    int curNoteIx = -1;

    int noteExpirationLength;
    int keyTimerLength;

    int[] drone           = { 0                     };
    int[] majorTriad      = { 0,  7, 16             };
    int[] maj7Voicing     = { 0,  7, 16, 23, 26     };
    int[] dorianVoicing   = { 2, 12, 17, 23         };
    int[] lydianVoicing   = { 5, 12, 19, 26, 33, 40 };
    int[] susVoicing      = { 7, 17, 21, 24, 28, 33 };
    int[] phrygianVoicing = { 4, 17, 21, 23, 28     };
    int[][] voicings = {
            drone,
            majorTriad,
            maj7Voicing,
            dorianVoicing,
            lydianVoicing,
            susVoicing,
            phrygianVoicing};
    int[] curVoicing;
    int[] prevVoicing;

    int userModeIx = 0;
    String[] userModeName = {
            "Drone",
            "Major Triad",
            "Major7",
            "Gabe Voicing",
            "Lydian",
            "Sus/Mixolydian",
            "Phrygian", };


    // Used to keep track how long a note was heard.
    public long timeRegistered;
    public int noteLengthRequirement;

    Button expirationButton;
    Button keyTimerButton;
    Button noteLengthRequirementButton;
    Button userModeButton;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    // TODO: Add user parameter.
    public static int plugin = 52;
    // Used for practicing different modes.
    // TODO: Add user parameter.
    public static int mode = 0; // 0 = Ionian; 1 = Dorian; 2 = Phrygian; ... (update later for melodic minor, other tonalities...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // keyFinder = new KeyFinder();     CHECKING IF STATIC MAKES IT FASTER

        // The amount of time a note must be registered for until it is added to the active note list.
        noteLengthRequirement = 60;
        keyFinder.setKeyTimerLength(2);
        keyFinder.setNoteTimerLength(2);

        // Button for Note Timer
        expirationButton = (Button) findViewById(R.id.expirationButton);
        noteExpirationLength = keyFinder.getNoteTimerLength();
        expirationButton.setText("" + noteExpirationLength);

        // Button for Key timer.
        keyTimerButton = (Button) findViewById(R.id.keyTimerButton);
        keyTimerLength = keyFinder.getKeyTimerLength();
        keyTimerButton.setText("" + keyTimerLength);

        // Button for note length requirement.\
        noteLengthRequirementButton = (Button) findViewById(R.id.noteLengthTimerButton);
        noteLengthRequirementButton.setText("" + noteLengthRequirement);

        // User mode button
        userModeIx = 0;
        userModeButton = (Button) findViewById(R.id.userModeButton);
        userModeButton.setText(userModeName[userModeIx]);

        // Construct Midi Driver.
        midi = new MidiDriver();
        midi.setOnMidiStartListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (midi != null)
            midi.start();
    }

    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = keyFinder.getAllNotes().getNoteAtIndex(noteIx);
        keyFinder.addNoteToList(curNote);
        Log.d(MESSAGE_LOG_ADD, curNote.getName());
        Log.d(MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        prevAddedNote = noteIx;

        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
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
            //Log.d(MESSAGE_LOG_SPEED, "Processing: " + keyFinder.getAllNotes().getNoteAtIndex(midiKey));
        // }

        // Note change is detected.
        if (curKey != prevAddedNote) {
            // If previously added note is no longer heard.
            if (prevAddedNote != -1) {
                // Start timer.
                keyFinder.getAllNotes().getNoteAtIndex(
                        prevAddedNote).startNoteTimer(keyFinder, noteExpirationLength);
                Log.d(MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
                        prevAddedNote).getName() + ": Started");
            }
            // No note is heard.
            if (pitchInHz == -1) {
                curNoteIx = -1;
                prevAddedNote = -1;
            }
            // Different note is heard.
            else if (curKey != curNoteIx) {
                curNoteIx = curKey;
                timeRegistered = System.currentTimeMillis();
            }
            // Current note is heard.
            else if (noteMeetsConfidence()) {
                addNote(curKey);
                keyFinder.getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
                Log.d(MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
                        prevAddedNote).getName() + ": Cancelled");
            }
        }
        // Note removal detected.
        if (keyFinder.getNoteHasBeenRemoved()) {
            keyFinder.setNoteHasBeenRemoved(false);
            Log.d(MESSAGE_LOG_REMOVE, keyFinder.getRemovedNote().getName());
            Log.d(MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        }
        // If active key has changed.
        if (keyFinder.getActiveKeyHasChanged()) {
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
        //TODO:  This may have to be refactored so that it won't differentiate between same notes of
        //TODO:  a different octave.
        prevActiveKey = curActiveKey;
        if (keyFinder.getActiveKey() == null) {
            return;
        }
        curActiveKey = keyFinder.getActiveKey().getIx() + 36; // 36 == C
        int modeOffset = MusicTheory.MAJOR_SCALE_SEQUENCE[mode];
        if (prevActiveKey != curActiveKey) {
            printActiveKeyToScreen(); // FOR TESTING

            /*
            //TODO: Send everything as an array (work for any number of notes)
            // Stop the current note.
            sendMidi(0X80, prevActiveKey + modeOffset, 0);
            // Start the new note.
            sendMidi(0X90, curActiveKey + modeOffset, 63);
            */

            sendMidiChord(0X80, voicings[userModeIx], 0, prevActiveKey);
            sendMidiChord(0X90, voicings[userModeIx], 63, curActiveKey);
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
        tv.setText("Active Key: " + keyFinder.getActiveKey().getName());
    }

    public void incrementNoteExpiration(View view) {
        noteExpirationLength = (noteExpirationLength % 5) + 1;
        keyFinder.setNoteTimerLength(noteExpirationLength);
        expirationButton.setText("" + noteExpirationLength);
    }

    public void incrementKeyTimer(View view) {
        keyTimerLength = (keyTimerLength % 5) + 1;
        keyFinder.setKeyTimerLength(keyTimerLength);
        keyTimerButton.setText("" + keyTimerLength);
    }

    public void incrementNoteLengthRequirement(View view) {
        noteLengthRequirement = (noteLengthRequirement + 15) % 165;
        noteLengthRequirementButton.setText("" + noteLengthRequirement);
    }

    public void changeUserMode(View view) {
        sendMidiChord(0X80, voicings[userModeIx], 0, curActiveKey);
        userModeIx = (userModeIx + 1) % voicings.length;
        userModeButton.setText(userModeName[userModeIx]);
        sendMidiChord(0X90, voicings[userModeIx], 63, curActiveKey);
        // Log.d(MESSAGE_LOG_REMOVE, "hi");
    }
}