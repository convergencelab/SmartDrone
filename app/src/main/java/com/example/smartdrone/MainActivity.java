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

    // public ViewHelper tvr;
    public TextViewResources tvr;

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

        tvr = new TextViewResources(this);

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
        tvr.noteTimerButton.setText("" + KeyFinderHelper.getNoteTimerLength());

        // Button for Key timer.
        // keyTimerButton = (Button) findViewById(R.id.keyTimerButton);
        KeyFinderHelper.setKeyTimerLength(KeyFinderHelper.getKeyFinder().getKeyTimerLength());
        tvr.keyTimerButton.setText("" + KeyFinderHelper.getKeyTimerLength());

        // Button for note length requirement.\
        // noteLengthFilterButton = (Button) findViewById(R.id.noteFilterButton);
        tvr.noteLengthFilterButton.setText("" + PitchProcessorHelper.getNoteLengthFilter());

        // User mode button
        // userModeIx = 0;
        // userModeButton = (Button) findViewById(R.id.userModeButton);
        // userModeButton.setText(userModeName[userModeIx]);
        tvr.userModeButton.setText("" + VoicingsHelper.getNameAtIx(VoicingsHelper.getUserVoicingIx()));

        // volumeButton = findViewById(R.id.volumeButton);
        tvr.volumeButton.setText("" + MidiDriverHelper.getVolume());

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

            MidiDriverHelper.sendMidiChord(0X80, VoicingsHelper.getCurVoicing(), 0, KeyFinderHelper.getPrevActiveKeyIx());
            MidiDriverHelper.sendMidiChord(0X90, VoicingsHelper.getCurVoicing(), MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
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
        MidiDriverHelper.sendMidiSetup();
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
        tvr.noteTimerButton.setText("" + KeyFinderHelper.getNoteTimerLength());
    }

    public void incrementKeyTimer(View view) {
        KeyFinderHelper.setKeyTimerLength((KeyFinderHelper.getKeyTimerLength() % 5) + 1);
        KeyFinderHelper.getKeyFinder().setKeyTimerLength(KeyFinderHelper.getKeyTimerLength());
        tvr.keyTimerButton.setText("" + KeyFinderHelper.getKeyTimerLength());
    }

    public void incrementNoteLengthRequirement(View view) {
        PitchProcessorHelper.incrementNoteLengthFilter();
        tvr.noteLengthFilterButton.setText("" + PitchProcessorHelper.getNoteLengthFilter());
    }

    public void changeUserMode(View view) {
        MidiDriverHelper.sendMidiChord(Constants.STOP_NOTE, VoicingsHelper.getCurVoicing(),
                0, KeyFinderHelper.getCurActiveKeyIx());
        VoicingsHelper.incrementIx();
        tvr.userModeButton.setText(VoicingsHelper.getCurVoicingName());
        MidiDriverHelper.sendMidiChord(
                Constants.START_NOTE, VoicingsHelper.getCurVoicing(),
                MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
    }

    public void incrementVolume(View view) {
        MidiDriverHelper.incrementVolume();
        MidiDriverHelper.sendMidiChord(
                Constants.STOP_NOTE, VoicingsHelper.getCurVoicing(),
                0, KeyFinderHelper.getCurActiveKeyIx());
        MidiDriverHelper.sendMidiChord(
                Constants.START_NOTE, VoicingsHelper.getCurVoicing(),
                MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
        tvr.volumeButton.setText("" + MidiDriverHelper.getVolume());
    }
}