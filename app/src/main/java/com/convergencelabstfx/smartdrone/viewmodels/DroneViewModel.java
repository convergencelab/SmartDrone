package com.convergencelabstfx.smartdrone.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.convergencelabstfx.keyfinder.Scale;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictor;
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictorListener;
import com.convergencelabstfx.keyfinder.keypredictor.Phrase;
import com.convergencelabstfx.keyfinder.keypredictor.PhrasePredictor;
import com.convergencelabstfx.smartdrone.models.ChordConstructor;
import com.convergencelabstfx.smartdrone.models.MidiPlayer;
import com.convergencelabstfx.smartdrone.models.NoteProcessor;
import com.convergencelabstfx.smartdrone.models.NoteProcessorObserver;
import com.convergencelabstfx.smartdrone.models.SignalProcessorKt;
import com.convergencelabstfx.smartdrone.models.SignalProcessorObserver;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Drone pipeline:
 * 1) Process signal; TarsosDSP signal processing
 * 2) Process note data; analyze note probability, how long note was heard, etc... (filter out noise)
 * 3) Determine key; analyze processed notes (KeyPredictor)
 * 4) Determine chord;
 * 5) Play chord; MidiDriver
 * 6) Notify UI;
 */

public class DroneViewModel extends ViewModel {

    // todo: remove; just a place holder field

    private SignalProcessorKt mSignalProcessor = new SignalProcessorKt();

    private NoteProcessor mNoteProcessor = new NoteProcessor();

    private KeyPredictor mKeyPredictor;

    private ChordConstructor mChordConstructor = new ChordConstructor();

    private MidiPlayer mMidiPlayer = new MidiPlayer();

    public MutableLiveData<Boolean> mDroneIsActive = new MutableLiveData<>();

    public MutableLiveData<Integer> mDetectedNote = new MutableLiveData<>();
    public MutableLiveData<Integer> mUndetectedNote = new MutableLiveData<>();

    public MutableLiveData<Integer> mDetectedKey = new MutableLiveData<>();

    public MutableLiveData<Scale> mCurScale = new MutableLiveData<>();

    public DroneViewModel() {
        testMethod_setupKeyPredictor();
        testMethod_setupChordConstructor();
        testMethod_setupMidiPlayer();
        initPipeline();
    }

    public void startDrone() {
        mSignalProcessor.start();
        mMidiPlayer.start();
        mDetectedKey.setValue(-1);
        mDroneIsActive.setValue(true);
    }

    public void stopDrone() {
        mSignalProcessor.stop();
        mMidiPlayer.stop();
        mDetectedNote.setValue(-1);
        mDetectedKey.setValue(-1);
        mDroneIsActive.setValue(false);
    }

    public void setKeyChange(int key) {
        if (isRunning() && mDetectedKey.getValue() != null && key != mDetectedKey.getValue()) {
            mChordConstructor.setKey(key);
            mDetectedKey.setValue(key);
            mMidiPlayer.clear();
            mMidiPlayer.playChord(mChordConstructor.makeVoicing());
        }
    }

    public boolean isRunning() {
        return mDroneIsActive.getValue() != null && mDroneIsActive.getValue();
    }

    // todo: figure how this should work
    public String getCurModeName() {
        return null;
    }

    // todo: just a method for development purposes; should delete later
    private void testMethod_setupKeyPredictor() {
        // todo: find a better place for this eventually
        final Phrase mOctavePhrase = new Phrase();
        mOctavePhrase.addNote(0);
        mOctavePhrase.addNote(12);
        final PhrasePredictor predictor = new PhrasePredictor();
        predictor.targetPhrase = mOctavePhrase;

        mKeyPredictor = predictor;
    }

    private void testMethod_setupChordConstructor() {
        final List<Integer> mode = new ArrayList<>();
        mode.add(0);
        mode.add(2);
        mode.add(3);
        mode.add(5);
        mode.add(7);
        mode.add(9);
        mode.add(10);

        final VoicingTemplate template = new VoicingTemplate();
        template.addBassTone(0);
        template.addBassTone(4);

        template.addChordTone(1);
        template.addChordTone(2);
        template.addChordTone(4);
        template.addChordTone(8);

        mChordConstructor.setMode(mode);
        mChordConstructor.setKey(0);
        mChordConstructor.setTemplate(template);
        mChordConstructor.setBounds(36, 60, 48, 72);
    }

    private void testMethod_setupMidiPlayer() {
        // todo: yeah it's hardcoded for now
        mMidiPlayer.setPlugin(48);
    }

    // todo: gotta figure out exactly where a note index should turn into a note object
    // todo: make consist naming (observer/listener, notify/handle, onKeyPrediction etc...)
    private void initPipeline() {
        mSignalProcessor.addPitchListener(new SignalProcessorObserver() {
            @Override
            public void handlePitchResult(int pitch, float probability, boolean isPitched) {
                mDetectedNote.setValue(pitch);
                mNoteProcessor.onPitchDetected(pitch, probability, isPitched);
            }
        });

        mNoteProcessor.addNoteProcessorListener(new NoteProcessorObserver() {
            @Override
            public void notifyNoteDetected(int note) {
                Timber.i("Detected: " + note);
                mKeyPredictor.noteDetected(note);

            }

            @Override
            public void notifyNoteUndetected(int note) {
                mKeyPredictor.noteUndetected(note);
//                mUndetectedNote.setValue(note);
            }
        });

        mKeyPredictor.addListener(new KeyPredictorListener() {
            @Override
            public void notifyKeyPrediction(int newKey) {
                Timber.i("key: %s", newKey);
                mChordConstructor.setKey(newKey);
                mDetectedKey.setValue(newKey);
                // todo: implement
                mMidiPlayer.clear();
                mMidiPlayer.playChord(mChordConstructor.makeVoicing());
                Timber.i("newKey: %s", newKey);
            }
        });

    }

}
