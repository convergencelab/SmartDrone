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

    private int mLastNoteHeardIx = -1;

    public NoteProcessor() {

    }

    // todo: for now, pitch detection is only monophonic;
    //       if polyphonic detection is added in later, then
    //       this will need to be reworked
    public void onPitchDetected(int noteIx, float probability, boolean isPitched) {
        // todo: use other parameters later to better filter out noise / incorrect guesses
        if (noteIx != mLastNoteHeardIx) {
            if (mLastNoteHeardIx != -1) {
                for (NoteProcessorObserver listener : mListeners) {
                    listener.notifyNoteUndetected(mLastNoteHeardIx);
                }
            }
            if (noteIx != -1) {
                for (NoteProcessorObserver listener : mListeners) {
                    listener.notifyNoteDetected(noteIx);
                }
            }
            mLastNoteHeardIx = noteIx;
        }
    }

    public void addNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.add(listener);
    }

    public void removeNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.remove(listener);
    }

}
