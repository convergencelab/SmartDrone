package com.example.smartdrone;

import android.app.Activity;

import org.billthefarmer.mididriver.MidiDriver;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

public class DroneModel {
    /**
     * Activity that the model interacts with.
     */
    private Activity activity;

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
     * Constructor.
     */
    public DroneModel(Activity activity) {
        this.activity = activity;

        keyFinder = new KeyFinder();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        midiDriver = new MidiDriver();
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
}
