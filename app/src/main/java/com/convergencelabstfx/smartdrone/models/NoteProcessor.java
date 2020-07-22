package com.convergencelabstfx.smartdrone.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Process incoming pitch data so that noise can be filtered out from actual notes.
 */
public class NoteProcessor {

    // todo: tinker with this value
    final private float PROBABILITY_THRESHOLD = 0.0f;

    final private List<NoteProcessorObserver> mListeners = new ArrayList<>();

    private int mLastNoteHeard = -1;

    private long mLastNoteInitTimeHeard;

    public NoteProcessor() {

    }

    public void onPitchDetected(int noteIx, float probability, boolean isPitched) {
        // todo: use other parameters later to better filter out noise / incorrect guesses
        if (noteIx != mLastNoteHeard) {
            mLastNoteInitTimeHeard = System.currentTimeMillis();
            for (NoteProcessorObserver listener : mListeners) {
                listener.notifyNoteResult(noteIx, 0);
            }
        }
        else {
            for (NoteProcessorObserver listener : mListeners) {
                listener.notifyNoteResult(noteIx, (int) (System.currentTimeMillis() - mLastNoteInitTimeHeard));
            }
        }
    }

    public void addNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.add(listener);
    }

    public void removeNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.remove(listener);
    }

}
