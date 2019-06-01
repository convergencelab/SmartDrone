package com.example.smartdrone.Models;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.smartdrone.Constants;
import com.example.smartdrone.DroneActivity;
import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.Note;
import com.example.smartdrone.R;

import org.billthefarmer.mididriver.MidiDriver;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

// TODO
// Split model into multiple models (KeyFinder, MidiDriver, PitchProcessor, etc.)

public class DroneModel
//        implements MidiDriver.OnMidiStartListener
{
    /**
     * Activity class that the model communicates with.
     */
    private DroneActivity droneActivity;

    private long timeRegistered;

    private VoicingModel voicingModel;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    //todo user parameter
    private int plugin;

    //todo refactor: better naming convention
    /**
     * Controls the user voicing.
     */
    private int userModeIx;

    /**
     * KeyFinder used for analyzing note data.
     */
    private KeyFinder keyFinder;

    /**
     * Audio dispatcher connected to microphone.
     */
    private AudioDispatcher dispatcher;

    /**
     * Midi driver for outputting audio.
     */
    private MidiDriver midiDriver;

    /**
     * Current key being output.
     */
    private int prevActiveKeyIx;

    /**
     * Previous key that was output.
     */
    private int curActiveKeyIx;

    /**
     * Last note that was added to active note list.
     */
    private int prevAddedNoteIx;

    /**
     * Current note being monitored.
     */
    private int curNoteIx;

    /**
     * True if drone has just been started without an active key or is producing output.
     */
    private boolean droneActive;

    private int midiDriverVolume;

    /**
     * Constructor.
     */
    public DroneModel(DroneActivity droneActivity) {
        this.droneActivity = droneActivity;
        keyFinder = new KeyFinder();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        midiDriver = new MidiDriver();
        droneActive = false;
        plugin = Constants.PLUGIN_CHOIR;
        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        prevAddedNoteIx = -1;
        curNoteIx = -1;
        voicingModel = new VoicingModel();
        userModeIx = 0;
        keyFinder.setNoteTimerLength(2);
        midiDriverVolume = 65;
    }

    /**
     * Get key finder.
     * @return      KeyFinder; key finder.
     */
    public KeyFinder getKeyFinder() {
        return keyFinder;
    }

    /**
     * Get audio dispatcher.
     * @return      AudioDispatcher; audio dispatcher.
     */
    public AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Get midi driver.
     * @return      MidiDriver; midi driver.
     */
    public MidiDriver getMidiDriver() {
        return midiDriver;
    }

    public boolean isDroneActive() {
        return droneActive;
    }

    public void setIsDroneActive(boolean isActive) {
        droneActive = isActive;
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
        int curKey = convertPitchToIx((double) pitchInHz); // No note will return -1 // todo

        // Note change is detected.
        if (curKey != prevAddedNoteIx) {
            // If previously added note is no longer heard.
            if (prevAddedNoteIx != -1) {
                // Start timer.
                keyFinder.getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(keyFinder, droneActivity.noteExpirationLength); // todo
                Log.d(Constants.MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
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
                keyFinder.getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
            }
        }
        // Note removal detected.
        if (keyFinder.getNoteHasBeenRemoved()) {
            keyFinder.setNoteHasBeenRemoved(false);
            Log.d(Constants.MESSAGE_LOG_REMOVE, keyFinder.getRemovedNote().getName());
            Log.d(Constants.MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        }
        // If active key has changed.
        if (keyFinder.getActiveKeyHasChanged()) {
            playActiveKeyNote();
        }

        // Update text views.
        droneActivity.setPitchText(pitchInHz); // todo
        droneActivity.setNoteText(pitchInHz);  // todo
    }

    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = keyFinder.getAllNotes().getNoteAtIndex(noteIx);
        keyFinder.addNoteToList(curNote);
        Log.d(Constants.MESSAGE_LOG_ADD, curNote.getName());
        Log.d(Constants.MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        prevAddedNoteIx = noteIx;

        playActiveKeyNote(); // todo
    }


    /**
     * Plays the tone(s) of the current active key.
     */
    public void playActiveKeyNote() {
        prevActiveKeyIx = curActiveKeyIx;
        if (keyFinder.getActiveKey() == null) {
            return;
        }
        if (!droneActive) {
            return;
        }
        curActiveKeyIx = keyFinder.getActiveKey().getIx() + 36; // 36 == C
        if (prevActiveKeyIx != curActiveKeyIx) {
            droneActivity.printActiveKeyToScreen(); // FOR TESTING

            Log.d("debug", "sending midi");
            // todo Restore these
            // sendMidiChord(Constants.STOP_NOTE, droneActivity.voicings[userModeIx], Constants.VOLUME_OFF, prevActiveKeyIx);
            // sendMidiChord(Constants.START_NOTE, droneActivity.voicings[userModeIx], droneActivity.midiVolume, curActiveKeyIx);
            Log.d(Constants.MESSAGE_LOG_VOICING, "before stop");

            // Stop chord.
            sendMidiChord(Constants.STOP_NOTE,
                    voicingModel.getVoicingCollection()
                    .getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userModeIx]).getVoiceIxs(),
                    Constants.VOLUME_OFF, prevActiveKeyIx);

            Log.d(Constants.MESSAGE_LOG_VOICING, "after stop");

            // Start chord.
            sendMidiChord(Constants.START_NOTE,
                    voicingModel.getVoicingCollection()
                    .getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userModeIx]).getVoiceIxs(),
                    midiDriverVolume, curActiveKeyIx);

            Log.d(Constants.MESSAGE_LOG_VOICING, "after play");
        }
    }

    public boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > droneActivity.noteLengthRequirement;
    }

    public int getUserModeIx() {
        return userModeIx;
    }

    public void setUserModeIx(int i) {
        userModeIx = i;
    }

    public int getCurActiveKeyIx() {
        return curActiveKeyIx;
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
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Initial setup data for midi.
     */
    public void sendMidiSetup() {
        byte msg[] = new byte[2];
        msg[0] = (byte) Constants.PROGRAM_CHANGE;    // 0XC0 == PROGRAM CHANGE
        msg[1] = (byte) plugin;
        midiDriver.write(msg);
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
        midiDriver.write(msg);
    }

    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    public void sendMidiChord(int event, int[] midiKeys, int volume) {
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
    public void sendMidiChord(int event, int[] midiKeys, int volume, int rootIx) {
        int octaveAdjustment = 0;
        if (midiKeys[0] + rootIx > 47) {
            octaveAdjustment = -12;
        }

        for (int key : midiKeys) {
            sendMidi(event, key + rootIx + octaveAdjustment, volume);
        }
    }

    public void changeUserMode() {
        sendMidiChord(Constants.STOP_NOTE, voicingModel.getVoicingCollection().
                getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userModeIx]).getVoiceIxs(), 0, curActiveKeyIx); // todo refactor. send midi chord should accept Voicing, not int[]

        userModeIx = (userModeIx + 1) % voicingModel.STOCK_VOICINGS_NAMES.length;

        sendMidiChord(Constants.START_NOTE, voicingModel.getVoicingCollection().
                getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userModeIx]).getVoiceIxs(), midiDriverVolume, curActiveKeyIx);
    }

    public void toggleDrone() {
        if (droneActive) {
            deactivateDrone();
//            TextView tv = findViewById(R.id.activeKeyPlainText);
//            tv.setText("Active Key: ");
//            controlButton.setImageResource(R.drawable.ic_play_drone);
        }
        else {
            activateDrone();
//            controlButton.setImageResource(R.drawable.ic_stop_drone);
        }
    }

    public void activateDrone() {
        if (midiDriver != null) {
            midiDriver.start();
            droneActive = true;
        }
    }

    public void deactivateDrone() {
        //todo Should clear all the activekey and active note stuff
        if (midiDriver != null) {
            midiDriver.stop();
            droneActive = false;
            getKeyFinder().cleanse();
        }
    }

    public void startPitchProcessor() {
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                droneActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        getDispatcher().addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(getDispatcher(), "Audio Thread");
        audioThread.start();
    }
}
