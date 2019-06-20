package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.DroneActivity;
import com.example.smartdrone.Note;
import com.example.smartdrone.Voicing;
import com.example.smartdrone.VoicingTemplate;

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
     * Current active voicing template.
     */
    private VoicingTemplate curTemplate;

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
     * Mode for drone playback.
     * Refers to mode in musical context (Ionian, Dorian, Phrygian, ... )
     */
    private int userModeIx;

    /**
     * True if drone has just been started without an active key or is producing output.
     */
    private boolean isActive;

    /**
     * Boolean for bass note playback.
     * If true, a bass note will be played with voicing.
     */
    private boolean hasBassNote;

    /**
     * Constructor.
     * @param       droneActivity DroneActivity; drone activity.
     */
    public DroneModel(DroneActivity droneActivity) {
        this.droneActivity = droneActivity;
        keyFinderModel = new KeyFinderModel();
        midiDriverModel = new MidiDriverModel();
        pitchProcessorModel = new PitchProcessorModel();
        voicingModel = new VoicingModel();
        curTemplate = voicingModel.getVoicingTemplateCollection()
                .getVoicingTemplate("V Major / I"); //todo hardcoded for now because testing, but make dynamic later

        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        isActive = false;
        userVoicingIx = 0;
        prevVoicing = null;
        hasBassNote = false;
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
//        int noteIx = pitchProcessorModel.processPitch(pitchInHz, keyFinderModel);
        Note curNote = pitchProcessorModel.processPitch(pitchInHz, keyFinderModel);


        // Note removal detected.
        if (keyFinderModel.getKeyFinder().getNoteHasBeenRemoved()) {
            keyFinderModel.getKeyFinder().setNoteHasBeenRemoved(false);
            Log.d(Constants.MESSAGE_LOG_REMOVE, keyFinderModel.getKeyFinder().getRemovedNote().getName());
            Log.d(Constants.MESSAGE_LOG_LIST, keyFinderModel.getKeyFinder().getActiveNotes().toString());
        }
        if (pitchProcessorModel.noteHasChanged()) {
            if (curNote == null) {
                droneActivity.setPianoImage(-1);
            }
            else {
                droneActivity.setPianoImage(curNote.getIx());
            }
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
            Voicing v = curTemplate.generateVoicing(keyFinderModel.getKeyFinder().getActiveKey(), 
                    userModeIx, 4, hasBassNote);
            midiDriverModel.playVoicing(v);
        }
    }

    /**
     * Toggles drone state between active and inactive.
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
            midiDriverModel.sendMidiSetup();
            startDroneProcess();
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
                        // interface method
            public void handlePitch(PitchDetectionResult result, AudioEvent event){
                final float pitchInHz = result.getPitch();
                droneActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //todo optimize. No need to run these if drone is inactive
                        processPitch(pitchInHz, droneActivity, keyFinderModel);
                        monitorActiveKey();
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, pdh);
        if (pitchProcessorModel.getDispatcher() == null) {
            pitchProcessorModel.setDispatcher(pitchProcessorModel.constructDispatcher());
        }
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
        pitchProcessorModel.setLastAdded(null);
        pitchProcessorModel.setLastHeard(null);
        midiDriverModel.setCurVoicing(null);
        prevVoicing = null;
        if (pitchProcessorModel.getDispatcher() != null) {
            pitchProcessorModel.getDispatcher().stop();
        }
        pitchProcessorModel.setDispatcher(null);
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

    /**
     * Get user mode index.
     * @return      int; user mode index.
     */
    public int getUserModeIx() {
        return userModeIx;
    }

    /**
     * Set user mode index.
     * @param       userModeIx int; user mode index.
     */
    public void setUserModeIx(int userModeIx) {
        this.userModeIx = userModeIx;
    }

    /**
     * Sets boolean for bass note playback.
     * @param       hasBassNote boolean; true if bass note.
     */
    public void sethasBassNote(boolean hasBassNote) {
        this.hasBassNote = hasBassNote;
    }

    /**
     * Get voicing template.
     * @return      VoicingTemplate; voicing template.
     */
    public VoicingTemplate getCurTemplate() {
        return curTemplate;
    }

    /**
     * Set voicing template.
     * @param       curTemplate VoicingTemplate; voicing template.
     */
    public void setCurTemplate(VoicingTemplate curTemplate) {
        this.curTemplate = curTemplate;
    }

    public VoicingModel getVoicingModel() {
        return voicingModel;
    }
}
