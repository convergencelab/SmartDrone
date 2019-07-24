package com.convergencelab.smartdrone.models.pitchprocessor;

import androidx.appcompat.app.AppCompatActivity;

import com.convergencelab.smartdrone.Constants;
import com.example.keyfinder.MusicTheory;

import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

public class PitchProcessorModelRefac implements PitchProcessorInterface {
    /**
     * Audio dispatcher connected to microphone.
     */
    private AudioDispatcher dispatcher;

    /**
     * Requested sample rate.
     */
    private static final int SAMPLE_RATE = 22050;

    /**
     * Size of the audio buffer (in samples).
     */
    private static final int AUDIO_BUFFER_SIZE = 1024;

    /**
     * Size of the overlap (in samples).
     */
    private static final int BUFFER_OVERLAP = 0;

    /**
     * Context needed for processor to run on UI thread.
     * There may be a better way to do this.
     */
    private AppCompatActivity mActivity;

    /**
     * List of observers to notify of pitch result.
     */
    private List<PitchProcessorObserver> observers;

    /**
     * Constructor.
     * @param activity activity needed to run on UI thread.
     */
    PitchProcessorModelRefac(AppCompatActivity activity) {
        mActivity = activity;

        observers = new ArrayList<>();
        dispatcher = null;
    }

    /**
     * Starts processing pitch from microphone.
     * Thread contains a runnable that will notify observers of pitch result.
     * Call method stop() to end pitch processing.
     */
    @Override
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
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                SAMPLE_RATE,
                AUDIO_BUFFER_SIZE,
                handler);
        dispatcher = AudioDispatcherFactory
                .fromDefaultMicrophone(
                Constants.SAMPLE_RATE,
                Constants.AUDIO_BUFFER_SIZE,
                Constants.BUFFER_OVERLAP);
        dispatcher.addAudioProcessor(pitchProcessor);
        Thread audioThread = new Thread(dispatcher, "Pitch Processing Thread");
        audioThread.start();
    }

    /**
     * Stops pitch processing.
     */
    @Override
    public void stop() {
        if (dispatcher == null) {
            // Throw exception here. ?
            return;
        }
        dispatcher.stop();
        dispatcher = null;
    }

    /**
     * Add observer to list.
     * @param observer to be added.
     */
    @Override
    public void addPitchListener(PitchProcessorObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove observer from list.
     * @param observer to be removed.
     */
    @Override
    public void removePitchListener(PitchProcessorObserver observer) {
        observers.remove(observer);
    }

    /**
     * Converts pitch (hertz) to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    private int convertPitchToIx(double pitchInHz) {
        if (pitchInHz == -1) {
            return -1;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz) % MusicTheory.TOTAL_NOTES;
    }

}
