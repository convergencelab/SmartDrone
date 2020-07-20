package com.convergencelabstfx.smartdrone.v2.models;

import androidx.fragment.app.FragmentActivity;

import com.convergencelabstfx.smartdrone.models.signalprocessor.PitchProcessorObserver;

import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;


public class SignalProcessor2 {

    private static final int SAMPLE_RATE = 22050;
    private static final int AUDIO_BUFFER_SIZE = 1024;
    private static final int BUFFER_OVERLAP = 0;

    // todo: find a way to get rid of activity dependency
    private FragmentActivity mActivity = null;
    private AudioDispatcher dispatcher = null;

    private List<PitchProcessorObserver> observers = new ArrayList<>();
    private boolean mIsRunning = false;

    public SignalProcessor2() {
    }

    public void start() {
        PitchDetectionHandler handler = (result, event) -> {
            final float pitchInHz = result.getPitch();
            mActivity.runOnUiThread(() -> {
                int pitchAsInt = convertPitchToIx(pitchInHz);
                for (PitchProcessorObserver observer : observers) {
                    observer.handlePitchResult(pitchAsInt);
                }
            });
        };
        be.tarsos.dsp.AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                SAMPLE_RATE,
                AUDIO_BUFFER_SIZE,
                handler);
        dispatcher = AudioDispatcherFactory
                .fromDefaultMicrophone(
                        SAMPLE_RATE,
                        AUDIO_BUFFER_SIZE,
                        BUFFER_OVERLAP);
        dispatcher.addAudioProcessor(pitchProcessor);
        Thread audioThread = new Thread(dispatcher, "Pitch Processing Thread");
        audioThread.start();

        mIsRunning = true;
    }

    public void stop() {
        if (dispatcher == null) {
            // Todo: throw exception here. ?
            return;
        }
        dispatcher.stop();
        dispatcher = null;

        mIsRunning = false;
    }

    // todo: can delete this function after activity dependency is removed
    public void setActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    public void addPitchListener(PitchProcessorObserver observer) {
        observers.add(observer);
    }

    public void removePitchListener(PitchProcessorObserver observer) {
        observers.remove(observer);
    }

    private int convertPitchToIx(double pitchInHz) {
        if (pitchInHz == -1) {
            return -1;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz);
    }

    public boolean isRunning() {
        return mIsRunning;
    }

}
