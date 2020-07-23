package com.convergencelabstfx.smartdrone.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.convergencelabstfx.keyfinder.Note;
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictor;
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictorListener;
import com.convergencelabstfx.keyfinder.keypredictor.Phrase;
import com.convergencelabstfx.keyfinder.keypredictor.PhrasePredictor;
import com.convergencelabstfx.smartdrone.models.NoteProcessor;
import com.convergencelabstfx.smartdrone.models.NoteProcessorObserver;
import com.convergencelabstfx.smartdrone.models.SignalProcessorKt;
import com.convergencelabstfx.smartdrone.models.SignalProcessorObserver;

import timber.log.Timber;


/**
 * Drone pipeline:
 *   1) Process signal; TarsosDSP signal processing
 *   2) Process note data; analyze note probability, how long note was heard, etc... (filter out noise)
 *   3) Determine key; analyze processed notes (KeyPredictor)
 *   4) Determine chord;
 *   5) Play chord; MidiDriver
 *   6) Notify UI;
 *
 */

public class DroneViewModel extends ViewModel {

    // todo: remove; just a place holder field
    public MutableLiveData<String> mTestField = new MutableLiveData<>("test");

    private SignalProcessorKt mSignalProcessor = new SignalProcessorKt();

    private NoteProcessor mNoteProcessor = new NoteProcessor();

    private KeyPredictor mKeyPredictor;

    // private ChordConstructor mChordConstructor;

    // private MidiDriver mMidiDriver;

    private boolean mIsRunning;



    public DroneViewModel() {
        setupKeyPredictor();
        initPipeline();
    }

    public void startDrone() {
        Timber.i("starting");
        mSignalProcessor.start();
        mIsRunning = true;
    }

    public void stopDrone() {
        mSignalProcessor.stop();
        mIsRunning = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    // todo: just a method for development purposes; should delete later
    private void setupKeyPredictor() {
        // todo: find a better place for this eventually
        final Phrase mOctavePhrase = new Phrase();
        mOctavePhrase.addNote(new Note(0));
        mOctavePhrase.addNote(new Note(12));
        final PhrasePredictor predictor = new PhrasePredictor();
        predictor.targetPhrase = mOctavePhrase;

        mKeyPredictor = predictor;
    }

    // todo: gotta figure out exactly where a note index should turn into a note object
    // todo: make consist naming (observer/listener, notify/handle, onKeyPrediction etc...)
    private void initPipeline() {
        mSignalProcessor.addPitchListener(new SignalProcessorObserver() {
            @Override
            public void handlePitchResult(int pitch, float probability, boolean isPitched) {
                mNoteProcessor.onPitchDetected(pitch, probability, isPitched);
            }
        });

        mNoteProcessor.addNoteProcessorListener(new NoteProcessorObserver() {
            @Override
            public void notifyNoteDetected(int note) {
                mKeyPredictor.noteDetected(note);
            }
            @Override
            public void notifyNoteUndetected(int note) {
                mKeyPredictor.noteUndetected(note);
            }
        });

        mKeyPredictor.addListener(new KeyPredictorListener() {
            @Override
            public void notifyKeyPrediction(int newKey) {
                Timber.i("key: %s", newKey);
            }
        });

    }

}
