package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.DroneActivity;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

//TODO
// Split model into multiple models (KeyFinder, MidiDriver, PitchProcessor, etc.)

public class DroneModel {
    /**
     * Activity class that the model communicates with.
     */
    private DroneActivity droneActivity;

    private long timeRegistered;

    private VoicingModel voicingModel;

    //todo refactor: better naming convention
    /**
     * Controls the user voicing.
     */
    private int userVoicingIx;

    /**
     * Handles key finder object.
     */
    private KeyFinderModel keyFinderModel;

    /**
     * Handles MidiDriver object.
     */
    private MidiDriverModel midiDriverModel;

    /**
     * Audio dispatcher connected to microphone.
     */
    private AudioDispatcher dispatcher;

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

    /**
     * Constructor.
     */
    public DroneModel(DroneActivity droneActivity) {
        this.droneActivity = droneActivity;
        keyFinderModel = new KeyFinderModel();
        midiDriverModel = new MidiDriverModel();

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        droneActive = false;
        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        prevAddedNoteIx = -1;
        curNoteIx = -1;
        voicingModel = new VoicingModel();
        userVoicingIx = 0;
    }

    /**
     * Get audio dispatcher.
     * @return      AudioDispatcher; audio dispatcher.
     */
    public AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    public boolean isDroneActive() {
        return droneActive;
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
                keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(keyFinderModel.getKeyFinder(), droneActivity.noteExpirationLength); // todo
                Log.d(Constants.MESSAGE_LOG_NOTE_TIMER, keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(
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
                keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
            }
        }
        // Note removal detected.
        if (keyFinderModel.getKeyFinder().getNoteHasBeenRemoved()) {
            keyFinderModel.getKeyFinder().setNoteHasBeenRemoved(false);
            Log.d(Constants.MESSAGE_LOG_REMOVE, keyFinderModel.getKeyFinder().getRemovedNote().getName());
            Log.d(Constants.MESSAGE_LOG_LIST, keyFinderModel.getKeyFinder().getActiveNotes().toString());
        }
        // If active key has changed.
        if (keyFinderModel.getKeyFinder().getActiveKeyHasChanged()) {
            playActiveKeyNote();
        }

        // Update text views.
        droneActivity.setPitchText(pitchInHz);
        droneActivity.setNoteText(pitchInHz);
    }

    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        // Adds note to active note list.
        keyFinderModel.addNote(noteIx);

        prevAddedNoteIx = noteIx;
        playActiveKeyNote();
    }

    /**
     * Plays the tone(s) of the current active key.
     */
    public void playActiveKeyNote() {
        prevActiveKeyIx = curActiveKeyIx;
        // No active key, or drone is inactive.
        if (keyFinderModel.getKeyFinder().getActiveKey() == null || !droneActive) {
            return;
        }
        curActiveKeyIx = keyFinderModel.getKeyFinder().getActiveKey().getIx() + 36; // 36 == C
        if (prevActiveKeyIx != curActiveKeyIx) {
            droneActivity.printActiveKeyToScreen(); //todo create listener for key change
            // Stop chord.
            midiDriverModel.sendMidiChord(Constants.STOP_NOTE,
                    voicingModel.getVoicingCollection()
                    .getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userVoicingIx]).getVoiceIxs(),
                    Constants.VOLUME_OFF, prevActiveKeyIx);
            // Start chord.
            midiDriverModel.sendMidiChord(Constants.START_NOTE,
                    voicingModel.getVoicingCollection()
                    .getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userVoicingIx]).getVoiceIxs(),
                    midiDriverModel.getVolume(), curActiveKeyIx);
        }
    }

    public boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > droneActivity.noteLengthRequirement;
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

    public void changeUserMode() {
        midiDriverModel.sendMidiChord(Constants.STOP_NOTE, voicingModel.getVoicingCollection().
                getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userVoicingIx]).getVoiceIxs(), 0, curActiveKeyIx); // todo refactor. send midi chord should accept Voicing, not int[]

        userVoicingIx = (userVoicingIx + 1) % voicingModel.STOCK_VOICINGS_NAMES.length;

        midiDriverModel.sendMidiChord(Constants.START_NOTE, voicingModel.getVoicingCollection().
                getVoicing(voicingModel.STOCK_VOICINGS_NAMES[userVoicingIx]).getVoiceIxs(), midiDriverModel.getVolume(), curActiveKeyIx);
    }

    public void toggleDrone() {
        if (droneActive) {
            deactivateDrone();
        }
        else {
            activateDrone();
        }
    }

    public void activateDrone() {
        if (midiDriverModel.getMidiDriver() != null) {
            midiDriverModel.getMidiDriver().start();
            droneActive = true;
        }
    }

    public void deactivateDrone() {
        //todo Should clear all the active key and active note stuff
        if (midiDriverModel.getMidiDriver() != null) {
            midiDriverModel.getMidiDriver().stop();
            droneActive = false;
            keyFinderModel.getKeyFinder().cleanse();
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

    public KeyFinderModel getKeyFinderModel() {
        return keyFinderModel;
    }

    public MidiDriverModel getMidiDriverModel() {
        return midiDriverModel;
    }
}
