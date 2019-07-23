package com.convergencelab.smartdrone.drone;

import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.keyfinder.KeyFinderInterface;
import com.convergencelab.smartdrone.models.mididriver.MidiDriverInterface;
import com.convergencelab.smartdrone.models.pitchprocessor.PitchProcessorInterface;
import com.convergencelab.smartdrone.models.pitchprocessor.PitchProcessorObserver;
import com.convergencelab.smartdrone.models.voicing.VoicingInterface;

public class DronePresenter implements DroneContract.Presenter, PitchProcessorObserver {

    private DroneDataSource mDataSource;

    private DroneContract.View mDroneView;

    private KeyFinderInterface mKeyFinder;

    private MidiDriverInterface mDriver;

    private PitchProcessorInterface mProcessor;

    private VoicingInterface mVoicing;

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
                   KeyFinderInterface keyFinder,
                   MidiDriverInterface driver,
                   PitchProcessorInterface processor,
                   VoicingInterface voicing) {
        mDataSource = dataSource;
        mDroneView = droneView;
        mKeyFinder = keyFinder;
        mDriver = driver;
        mProcessor = processor;
        mVoicing = voicing;
    }

    @Override
    public void start() {
        mProcessor.addObserver(this);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setActiveKey(int toSet) {
        // Todo: Future implementation. (piano key touch)
    }

    @Override
    public void sustainKey() {
        // Todo: Future implementation. (active key button touch)
    }

    /**
     * Meaty function.
     * @param pitch pitch to be handled.
     */
    @Override
    public void handlePitchResult(int pitch) {
        mKeyFinder.handleNote(pitch);
    }
}
