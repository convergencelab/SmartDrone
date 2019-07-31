package com.convergencelab.smartdrone.drone;

import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.data.Plugin;
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

    private Plugin[] mPlugins;

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
        mProcessor.addPitchListener(this);
        mNoteHandler.addKeyChangeListener(this);
    }

    @Override
    public void start() {
        mState = State.OFF;

        mPlugins = mDataSource.getPlugins();
        mPlayer.setPlugin(mPlugins[mDataSource.getPluginIx()].getPlugin());

        mNoteHandler.setParentScale(mDataSource.getParentScale());
        mNoteHandler.setNoteLengthFilter(mDataSource.getNoteLengthFilter());

        mChords.setVoicingTemplate(mDataSource.getTemplate());
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mDataSource.getModeIx()));
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
    public void handleActiveKeyButtonClick() {
        if (mState == State.OFF) {
            activateDrone();
            mState = State.ON;
        }
        else {
            // Todo: Sustain key
        }
    }

    @Override
    public void stop() {
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
    public void handlePitchResult(int pitch) {
        mDroneView.showNoteActive(pitch);
        mNoteHandler.handleNote(pitch);
    }

    @Override
    public void handleKeyChange(AbstractKey activeKey) {
        mChords.setKey(activeKey.getDegree(mDataSource.getModeIx()).getIx());

        Voicing curVoicing = mChords.makeVoicing();

        int[] toPlay = Utility.voicingToIntArray(curVoicing);
        mPlayer.play(toPlay);

        int mode = mDataSource.getModeIx();

        mDroneView.showActiveKey(
                activeKey.getDegree(mode).getName(),
                mNoteHandler.getModeTemplate(mode).getName());
    }

    private void activateDrone() {
        mProcessor.start();
        mNoteHandler.start();
        mPlayer.start();
        mDroneView.showDroneActive();
    }

    private void deactivateDrone() {
        mProcessor.stop();
        mNoteHandler.clear();
        mPlayer.stop();
        mDroneView.showDroneInactive();
    }
}
