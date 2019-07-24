package com.convergencelab.smartdrone.drone;

import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.notehandler.KeyChangeListener;
import com.convergencelab.smartdrone.models.notehandler.NoteHandler;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelab.smartdrone.models.pitchprocessor.PitchProcessorInterface;
import com.convergencelab.smartdrone.models.pitchprocessor.PitchProcessorObserver;
import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.utility.Utility;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.Voicing;

public class DronePresenter implements DroneContract.Presenter, PitchProcessorObserver, KeyChangeListener {

    // Models

    private DroneContract.View mDroneView;

    private DroneDataSource mDataSource;

    private NoteHandler mNoteHandler;

    private DronePlayer mPlayer;

    private PitchProcessorInterface mProcessor;

    private Chords mChords;

    /**
     * Constructor.
     * @param dataSource persistent data.
     * @param droneView view to interact with.
     * @param keyFinder key finder for determining key.
     * @param driver midi driver for midi synthesis.
     * @param processor processor for digital signal processing.
     * @param voicing voicing for generating harmony.
     */
    DronePresenter(DroneDataSource dataSource,
                   DroneContract.View droneView,
                   NoteHandler keyFinder,
                   DronePlayer driver,
                   PitchProcessorInterface processor,
                   Chords voicing) {
        mDataSource = dataSource;
        mDroneView = droneView;
        mNoteHandler = keyFinder;
        mPlayer = driver;
        mProcessor = processor;
        mChords = voicing;
    }

    @Override
    public void start() {
        mProcessor.addPitchListener(this);
        mNoteHandler.addKeyChangeListener(this);

        // Put init model stuff here

        mChords.setVoicingTemplate(mDataSource.getTemplate());
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mDataSource.getModeIx()));
    }

    @Override
    public void activateDrone() {
        mPlayer.start();
        mNoteHandler.start();
    }

    @Override
    public void stop() {
        mProcessor.stop();
        mNoteHandler.clear();
        mPlayer.stop();
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
        mNoteHandler.handleNote(pitch);
    }

    @Override
    public void handleKeyChange(AbstractKey activeKey) {
        // view -> show active key
        mChords.setKey(activeKey);
        Voicing curVoicing = mChords.makeVoicing();

        int[] toPlay = Utility.voicingToIntArray(curVoicing);
        mPlayer.play(toPlay);

        mDroneView.showActiveKey(activeKey.getName(), mNoteHandler.getModeTemplate(mDataSource.getParentScale()).getName());
    }
}
