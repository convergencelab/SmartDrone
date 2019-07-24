package com.convergencelab.smartdrone.drone;

import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.notehandler.KeyChangeListener;
import com.convergencelab.smartdrone.models.notehandler.NoteHandler;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelab.smartdrone.models.signalprocessor.SignalProcessor;
import com.convergencelab.smartdrone.models.signalprocessor.PitchProcessorObserver;
import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.utility.Utility;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.Voicing;

public class DronePresenter implements DroneContract.Presenter, PitchProcessorObserver, KeyChangeListener {

    enum State {
        ON, OFF
    }

    // Models

    private DroneContract.View mDroneView;

    private DroneDataSource mDataSource;

    private NoteHandler mNoteHandler;

    private DronePlayer mPlayer;

    private SignalProcessor mProcessor;

    private Chords mChords;

    private State mState;

    /**
     * Constructor.
     * @param dataSource persistent data.
     * @param droneView view to interact with.
     * @param noteHandler object for determining key.
     * @param player object for midi synthesis.
     * @param processor object for digital signal processing.
     * @param chords object for generating harmony.
     */
    DronePresenter(DroneDataSource dataSource,
                   DroneContract.View droneView,
                   NoteHandler noteHandler,
                   DronePlayer player,
                   SignalProcessor processor,
                   Chords chords) {
        mDataSource = dataSource;
        mDroneView = droneView;
        mNoteHandler = noteHandler;
        mPlayer = player;
        mProcessor = processor;
        mChords = chords;

        mDroneView.setPresenter(this);
    }

    @Override
    public void start() {
        mProcessor.addPitchListener(this);
        mNoteHandler.addKeyChangeListener(this);

        // Put init model stuff here

        mChords.setVoicingTemplate(mDataSource.getTemplate());
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mDataSource.getModeIx()));

        mState = State.OFF;
    }

    @Override
    public void toggleDroneState() {
        if (mState == State.OFF) {
            activateDrone();
            mState = State.ON;
        }
        else {
            deactivateDrone();
            mState = State.OFF;
        }
    }

    @Override
    public void handleActivityChange() {
        if (mState == State.ON) {
            deactivateDrone();
            mState = State.OFF;
        }
    }

    @Override
    public void setActiveKey(int toSet) {
        // Todo: Future implementation. (piano key touch)
    }

    @Override
    public void sustainKey() {
        // Todo: Future implementation. (active key button touch)
    }

    @Override
    public void handlePitchResult(int pitch) {
        mDroneView.showNoteActive(pitch);
        mNoteHandler.handleNote(pitch);
    }

    @Override
    public void handleKeyChange(AbstractKey activeKey) {
        mChords.setKey(activeKey);
        Voicing curVoicing = mChords.makeVoicing();

        int[] toPlay = Utility.voicingToIntArray(curVoicing);
        mPlayer.play(toPlay);

        mDroneView.showActiveKey(activeKey.getName(),
                mNoteHandler.getModeTemplate(mDataSource.getParentScale()).getName());
    }

    private void activateDrone() {
        mPlayer.start();
        mNoteHandler.start();
    }

    private void deactivateDrone() {
        mProcessor.stop();
        mNoteHandler.clear();
        mPlayer.stop();
        mDroneView.showNullPitch();
    }
}
