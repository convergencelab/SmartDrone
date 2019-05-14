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
 * - refactor code.
 * - debug accuracy of note detection.
 * - add user parameter for sensitivity.
 *     - add user parameter than decides how long a note stays in the cue before it expires.
 *       (default = 5)
 * - add user parameter for mode.
 * - fix sound distortion bug when switching activities.
 * - improve functionality.
 *     - note must be heard for a variable amount of time before it's added to list
 *       (to prevent adding erroneous notes)
 * - look into api for signal filtering.
 * - Debug features that displays the current active notes on the screen
 *   (or logcat?)
 * - add feature that active keys can generate chords.
 */

package com.example.smartdrone;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    // TarsosDSP
    public AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
    // Midi Driver
    public MidiDriver midi;
    // Key Finder
    public KeyFinder keyFinder;

    // Used for accessing note names.
    public static final String[] notes =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    // Variables for tracking active keys/notes
    int prevActiveKey = -1;
    int curActiveKey = -1;
    int prevAddedNote = -1;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    public static int plugin = 52;
    // TODO: Turn into user parameter.
    // Used for practicing different modes.
    public static int mode = 2; // 0 = Ionian; 1 = Dorian; 2 = Phyrgian; ... (update later for melodic minor, other tonalities...)

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

        keyFinder = new KeyFinder();
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
        keyFinder.addNoteToList(keyFinder.getAllNotes().getNoteAtIndex(noteIx));
        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

    /**
     * Converts pitch to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    public int convertPitchToIx(double pitchInHz) {
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
     * @param       pitchInHz double; pitch of note, in hertz.
     */
    public void setNoteText(double pitchInHz) {
        if (pitchInHz != -1) {
            setNoteText(notes[convertPitchToIx(pitchInHz)]);
        } else {
            setNoteText("null");
        }
    }

    /**
     * Update the pitch text on screen.
     * @param       pitchInHz double; pitch of note, in hertz.
     */
    public void setPitchText(double pitchInHz) {
        TextView pitchText = (TextView) findViewById(R.id.pitchText);
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
        int midiKey = convertPitchToIx((double) pitchInHz);
        // If new note is heard.
        if (midiKey != prevAddedNote && midiKey != -1) {
            addNote(midiKey);
        }
        // Update text views.
        setPitchText(pitchInHz);
        setNoteText(pitchInHz);
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
     * Plays the tone of the current active key.
     */
    public void playActiveKeyNote() {
        prevActiveKey = curActiveKey;
        curActiveKey = keyFinder.getActiveKey().getIx() + 36; // 36 == C
        int modeOffset = MusicTheory.MAJOR_SCALE_SEQUENCE[mode];
        if (prevActiveKey != curActiveKey) {
            printActiveKeyToScreen();

            /*
            // Stop the current note.
            sendMidi(0x80, prevActiveKey + modeOffset, 0);
            // Start the new note.
            sendMidi(0x90, curActiveKey + modeOffset, 63);
            */
        }
    }

    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Listener for sending initial midi messages when the Sonivox
    // synthesizer has been started, such as program change.
    @Override
    public void onMidiStart() {
        sendMidi();
    }

    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Send a midi message, 2 bytes
    protected void sendMidi() {
        byte msg[] = new byte[2];

        msg[0] = (byte) 0xc0;
        msg[1] = (byte) plugin;

        midi.write(msg);
    }

    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    /**
     * Send data that is to be synthesized by midi driver.
     * @param       m // TODO: Find out variable names
     * @param       midiKey int; index of note (uses octaves).
     * @param       volume int; volume of note.
     */
    protected void sendMidi(int m, int midiKey, int volume) {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) midiKey;
        msg[2] = (byte) volume;

        midi.write(msg);
    }

    /**
     * Sends multiple messages to be synthesized by midi dirver.
     * @param       m // TODO: Find out variable names
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    protected void sendMidiChord(int m, int[] midiKeys, int volume) {
        for (int key : midiKeys) {
            sendMidi(m, key, volume);
        }
    }

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + keyFinder.getActiveKey().getName());
    }
}