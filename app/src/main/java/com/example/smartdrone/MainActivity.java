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
 * - fix audio bug when switching activities.
 * - look into api for signal filtering.
 * - create Voiceleader java library.
 * - exception handling
 * - organize source code
 * - follow proper naming conventions
 * - if two keys are equal contenders, app will pick key with lowest index (timer starts first)
 *     - make it more random.
 * - remove unused import statements
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

    public ViewHelper viewHelper;

    public AudioDispatcher dispatcher = PitchProcessorHelper.getDispatcher();

    /*
    // Button noteTimerButton;
    Button keyTimerButton;
    Button noteLengthFilterButton;
    Button userModeButton;
    Button volumeButton;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // proccess.start() ...
        // Update text views.
        // setPitchText(pitchInHz);
        // setNoteText(pitchInHz);

        viewHelper = new ViewHelper(this);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
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

        // The amount of time a note must be registered for until it is added to the active note list.
        // noteLengthFilter = 60; //ph
        KeyFinderHelper.getKeyFinder().setKeyTimerLength(3);
        KeyFinderHelper.getKeyFinder().setNoteTimerLength(2);

        // Button for Note Timer
        // noteTimerButton = (Button) findViewById(R.id.noteTimerButton);
        KeyFinderHelper.setNoteTimerLength(KeyFinderHelper.getKeyFinder().getNoteTimerLength());
        viewHelper.noteTimerButton.setText("" + KeyFinderHelper.getNoteTimerLength());

        // Button for Key timer.
        // keyTimerButton = (Button) findViewById(R.id.keyTimerButton);
        KeyFinderHelper.setKeyTimerLength(KeyFinderHelper.getKeyFinder().getKeyTimerLength());
        viewHelper.keyTimerButton.setText("" + KeyFinderHelper.getKeyTimerLength());

        // Button for note length requirement.\
        // noteLengthFilterButton = (Button) findViewById(R.id.noteFilterButton);
        viewHelper.noteLengthFilterButton.setText("" + PitchProcessorHelper.getNoteLengthFilter());

        // User mode button
        // userModeIx = 0;
        // userModeButton = (Button) findViewById(R.id.userModeButton);
        // userModeButton.setText(userModeName[userModeIx]);
        viewHelper.userModeButton.setText("" + VoicingsHelper.getNameAtIx(VoicingsHelper.getUserVoicingIx()));

        // volumeButton = findViewById(R.id.volumeButton);
        viewHelper.volumeButton.setText("" + MidiDriverHelper.getVolume());

        // Construct Midi Driver.
        MidiDriverHelper.getMidiDriver().setOnMidiStartListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (MidiDriverHelper.getMidiDriver() != null)
            MidiDriverHelper.getMidiDriver().start();
    }

    // ph
    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = KeyFinderHelper.getKeyFinder().getAllNotes().getNoteAtIndex(noteIx);
        KeyFinderHelper.getKeyFinder().addNoteToList(curNote);
        // Log.d(MESSAGE_LOG_ADD, curNote.getName());
        // Log.d(MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        KeyFinderHelper.setPrevAddedNoteIx(noteIx);

        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

    //ph
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
            setNoteText(Constants.NOTES[convertPitchToIx(pitchInHz)]);
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

    //ph
    /**
     * Utilizes other single purpose methods.
     * 1. Converts pitch to ix.
     * 2. Adds note (based on ix) to active note list.
     * 3. Updates the text views on screen.
     * @param       pitchInHz float; current pitch being heard.
     */
    public void processPitch(float pitchInHz) {
        // Convert pitch to midi key.
        int curIx = convertPitchToIx((double) pitchInHz); // No note will return -1

        // Debug statement to see how fast the engine runs.
        // if (midiKey != -1) {
            //Log.d(MESSAGE_LOG_SPEED, "Processing: " + keyFinder.getAllNotes().getNoteAtIndex(midiKey));
        // }

        // Note change is detected.
        if (curIx != KeyFinderHelper.getPrevAddedNoteIx()) {
            // If previously added note is no longer heard.
            if (KeyFinderHelper.getPrevAddedNoteIx() != -1) {
                // Start timer.
                KeyFinderHelper.getKeyFinder().getAllNotes().getNoteAtIndex(
                        KeyFinderHelper.getPrevAddedNoteIx()).startNoteTimer(KeyFinderHelper.getKeyFinder(), KeyFinderHelper.getNoteTimerLength());
                // Log.d(MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
                //        prevAddedNoteIx).getName() + ": Started");
            }
            // No note is heard.
            if (pitchInHz == -1) {
                KeyFinderHelper.setCurNoteIx(-1);
                KeyFinderHelper.setPrevAddedNoteIx(-1);
            }
            // Different note is heard.
            else if (curIx != KeyFinderHelper.getCurNoteIx()) {
                KeyFinderHelper.setCurNoteIx(curIx);
                // timeRegistered = System.currentTimeMillis();
                PitchProcessorHelper.setTimeRegistered(System.currentTimeMillis());
            }
            // Current note is heard.
            else if (PitchProcessorHelper.noteMeetsConfidence()) {
                addNote(curIx);
                KeyFinderHelper.getKeyFinder().getAllNotes().getNoteAtIndex(curIx).cancelNoteTimer();
                // Log.d(MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
                //        prevAddedNoteIx).getName() + ": Cancelled");
            }
        }
        // Note removal detected.
        if (KeyFinderHelper.getKeyFinder().getNoteHasBeenRemoved()) {
            KeyFinderHelper.getKeyFinder().setNoteHasBeenRemoved(false);
            // Log.d(MESSAGE_LOG_REMOVE, KeyFinderHelper.getKeyFinder().getRemovedNote().getName());
            // Log.d(MESSAGE_LOG_LIST, KeyFinderHelper.getKeyFinder().getActiveNotes().toString());
        }
        // If active key has changed.
        if (KeyFinderHelper.getKeyFinder().getActiveKeyHasChanged()) {
            playActiveKeyNote();
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
     * Plays the tone(s) of the current active key.
     */
    public void playActiveKeyNote() {
        //TODO:  This may have to be refactored so that it won't differentiate between same notes of
        //TODO:  a different octave.
        // prevActiveKey = curActiveKey;
        KeyFinderHelper.setPrevActiveKeyIx(KeyFinderHelper.getCurActiveKeyIx());
        if (KeyFinderHelper.getKeyFinder().getActiveKey() == null) {
            return;
        }
        KeyFinderHelper.setCurActiveKeyIx(KeyFinderHelper.getKeyFinder().getActiveKey().getIx() + 36); // 36 == C
        if (KeyFinderHelper.getPrevActiveKeyIx() != KeyFinderHelper.getCurActiveKeyIx()) {
            printActiveKeyToScreen(); // FOR TESTING

            /*
            //TODO: Send everything as an array (work for any number of notes)
            // Stop the current note.
            sendMidi(0X80, prevActiveKey + modeOffset, 0);
            // Start the new note.
            sendMidi(0X90, curActiveKey + modeOffset, 63);
            */

            sendMidiChord(0X80, VoicingsHelper.getCurVoicing(), 0, KeyFinderHelper.getPrevActiveKeyIx());
            sendMidiChord(0X90, VoicingsHelper.getCurVoicing(), MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
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
        msg[1] = (byte) MidiDriverHelper.getPlugin();
        MidiDriverHelper.getMidiDriver().write(msg);
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
        MidiDriverHelper.getMidiDriver().write(msg);
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

    /**
     * Update the text view that displays the current active key.
     */
    public void printActiveKeyToScreen() {
        TextView activeKeyText = findViewById(R.id.activeKeyPlainText);
        activeKeyText.setText("Active Key: " + KeyFinderHelper.getKeyFinder().getActiveKey().getName());
    }

    public void incrementNoteExpiration(View view) {
        KeyFinderHelper.setNoteTimerLength((KeyFinderHelper.getNoteTimerLength() % 5) + 1);
        KeyFinderHelper.getKeyFinder().setNoteTimerLength(KeyFinderHelper.getNoteTimerLength());
        viewHelper.noteTimerButton.setText("" + KeyFinderHelper.getNoteTimerLength());
    }

    public void incrementKeyTimer(View view) {
        KeyFinderHelper.setKeyTimerLength((KeyFinderHelper.getKeyTimerLength() % 5) + 1);
        KeyFinderHelper.getKeyFinder().setKeyTimerLength(KeyFinderHelper.getKeyTimerLength());
        viewHelper.keyTimerButton.setText("" + KeyFinderHelper.getKeyTimerLength());
    }

    public void incrementNoteLengthRequirement(View view) {
        // noteLengthFilter = (noteLengthFilter + 15) % 165;
        PitchProcessorHelper.incrementNoteLengthFilter();
        viewHelper.noteLengthFilterButton.setText("" + PitchProcessorHelper.getNoteLengthFilter());
    }

    public void changeUserMode(View view) {
        sendMidiChord(0X80, VoicingsHelper.getCurVoicing(), 0, KeyFinderHelper.getCurActiveKeyIx());
        VoicingsHelper.incrementIx();
        viewHelper.userModeButton.setText(VoicingsHelper.getCurVoicingName());
        sendMidiChord(0X90, VoicingsHelper.getCurVoicing(), 63, KeyFinderHelper.getCurActiveKeyIx());
        // Log.d(MESSAGE_LOG_REMOVE, "hi");
    }

    public void incrementVolume(View view) {
        MidiDriverHelper.incrementVolume();
        sendMidiChord(0X80, VoicingsHelper.getCurVoicing(), 0, KeyFinderHelper.getCurActiveKeyIx());
        sendMidiChord(0X90, VoicingsHelper.getCurVoicing(), MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
        viewHelper.volumeButton.setText("" + MidiDriverHelper.getVolume());
    }
}