package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.DroneActivity;
import com.example.smartdrone.Voicing;

import java.io.Serializable;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


public class DroneModel implements Serializable {
    /**
     * Previous voicing for drone.
     */
    private Voicing prevVoicing;

    /**
     * Activity class that the model communicates with.
     */
    private DroneActivity droneActivity;

    /**
     * Handles Voicing objects.
     */
    private VoicingModel voicingModel;

    /**
     * Handles KeyFinder object.
     */
    private KeyFinderModel keyFinderModel;

    /**
     * Handles MidiDriver object.
     */
    private MidiDriverModel midiDriverModel;

    /**
     * Handles PitchProcessor object.
     */
    private PitchProcessorModel pitchProcessorModel;

    /**
     * Index of current active key.
     */
    private int curActiveKeyIx;

    /**
     * Index previous active key.
     */
    private int prevActiveKeyIx;

    /**
     * Controls the user voicing.
     */
    private int userVoicingIx;

    /**
     * True if drone has just been started without an active key or is producing output.
     */
    private boolean isActive;

    /**
     * Constructor.
     */
    public DroneModel(DroneActivity droneActivity) {
        this.droneActivity = droneActivity;
        keyFinderModel = new KeyFinderModel();
        midiDriverModel = new MidiDriverModel();
        pitchProcessorModel = new PitchProcessorModel();
        voicingModel = new VoicingModel();

        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        isActive = false;
        userVoicingIx = 0;
        prevVoicing = null;
    }

    /**
     * Check if drone is active.
     * @return      boolean; true if drone is active.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Processes pitch.
     * 1) Adds processed notes to key finder.
     * 2) Update text views that depend on pitch processor.
     *
     * @param pitchInHz       float; pitch in hertz.
     * @param droneActivity   DroneActivity; activity that displays pitch.
     * @param keyFinderModel  KeyFinderModel; object control note processing.
     */
    private void processPitch(float pitchInHz, DroneActivity droneActivity, KeyFinderModel keyFinderModel) {
        int noteIx = pitchProcessorModel.processPitch(pitchInHz, keyFinderModel);

        // Note removal detected.
        if (keyFinderModel.getKeyFinder().getNoteHasBeenRemoved()) {
            keyFinderModel.getKeyFinder().setNoteHasBeenRemoved(false);
            Log.d(Constants.MESSAGE_LOG_REMOVE, keyFinderModel.getKeyFinder().getRemovedNote().getName());
            Log.d(Constants.MESSAGE_LOG_LIST, keyFinderModel.getKeyFinder().getActiveNotes().toString());
        }
        if (pitchProcessorModel.noteHasChanged()) {
            droneActivity.setPianoImage(noteIx);
            pitchProcessorModel.setNoteHasChanged(false);
        }
    }

    //todo: move to MidiDriverModel.
    /**
     * Plays the tone(s) of the current active key.
     */
    private void playActiveKeyNote() {
        // No active key, or drone is inactive.
        if (noActiveKey() || !isActive) {
            return;
        }
        curActiveKeyIx = keyFinderModel.getKeyFinder().getActiveKey().getIx();
        if (prevActiveKeyIx != curActiveKeyIx) {
            // Stop chord.
            Voicing v = voicingModel.getVoicingTemplateCollection()
                    .getVoicingTemplate("7th (Drop II)")
                    .generateVoicing(keyFinderModel.getKeyFinder().getActiveKey(), 0, 4);
            midiDriverModel.playVoicing(v);
        }
    }

    /**
     * Switches drone state from active and inactive.
     */
    public void toggleDroneState() {
        if (isActive) {
            deactivateDrone();
        }
        else {
            activateDrone();
        }
    }

    /**
     * Activates drone.
     * Drone will process pitch and output audio in this state.
     */
    private void activateDrone() {
        if (midiDriverModel.getMidiDriver() != null) {
            isActive = true;
            midiDriverModel.getMidiDriver().start();
        }
    }

    /**
     * Deactivates drone.
     * Drone will wait to be activated in this state.
     */
    public void deactivateDrone() {
        if (midiDriverModel.getMidiDriver() != null) {
            isActive = false;
            midiDriverModel.getMidiDriver().stop();
            cleanseDrone();
            keyFinderModel.getKeyFinder().cleanse();
            droneActivity.setPianoImage(Constants.NULL_NOTE_IX);
        }
    }

    //todo clean up method, whatever that means
    /**
     * Starts the drone process.
     * Process notes & monitor active key.
     */
    public void startDroneProcess() {
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                droneActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isActive) {
                            Log.d("speed", "*speed test*");
                            //todo optimize. No need to run these if drone is inactive
                            processPitch(pitchInHz, droneActivity, keyFinderModel);
                            monitorActiveKey();
                        }
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        pitchProcessorModel.getDispatcher().addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(pitchProcessorModel.getDispatcher(), "Audio Thread");
        audioThread.start();
    }

    /**
     * Get key finder model.
     * @return      KeyFinderModel; key finder model.
     */
    public KeyFinderModel getKeyFinderModel() {
        return keyFinderModel;
    }

    /**
     * Get midi driver model.
     * @return      MidiDriverModel; midi driver model.
     */
    public MidiDriverModel getMidiDriverModel() {
        return midiDriverModel;
    }

    /**
     * Get pitch processor model.
     * @return      PitchProcessorModel;  pitch processor model.
     */
    public PitchProcessorModel getPitchProcessorModel() {
        return pitchProcessorModel;
    }

    /**
     * Monitors the active key.
     * If active key changes this method will update all active key dependencies.
     */
    private void monitorActiveKey() {
        if (keyFinderModel.getKeyFinder().getActiveKeyHasChanged()) {
            prevActiveKeyIx = curActiveKeyIx;
            playActiveKeyNote();
            droneActivity.printActiveKeyToScreen();
            keyFinderModel.getKeyFinder().setActiveKeyHasChanged(false);
        }
    }

    /**
     * Set all note/key indices to -1.
     */
    private void cleanseDrone() {
        prevActiveKeyIx = Constants.NULL_KEY_IX;
        curActiveKeyIx = Constants.NULL_KEY_IX;
        pitchProcessorModel.setLastAdded(Constants.NULL_NOTE_IX);
        pitchProcessorModel.setLastHeard(Constants.NULL_NOTE_IX);
        midiDriverModel.setCurVoicing(null);
        prevVoicing = null;
    }

    /**
     * Get previous voicing.
     * @return      Voicing; previous voicing.
     */
    public Voicing getPrevVoicing() {
        return prevVoicing;
    }

    /**
     * Set previous voicing.
     * @param       voicing Voicing; previous voicing.
     */
    public void setPrevVoicing(Voicing voicing) {
        this.prevVoicing = voicing;
    }

    /**
     * Check if there is an active key.
     * @return      boolean; true if no active key.
     */
    private boolean noActiveKey() {
        return keyFinderModel.getKeyFinder().getActiveKey() == null;
    }
}
