package com.convergencelabstfx.smartdrone.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Process incoming pitch data so that noise can be filtered out from actual notes.
 */
public class NoteProcessor {

    // todo: tinker with this value
    final private float PROBABILITY_THRESHOLD = 0.0f;

    final private List<NoteProcessorListener> mListeners = new ArrayList<>();

    private int mMillisRequired = 150;

    private int mPrevNoteHeardIx = -1;
    private long mPrevHeardTimeStamp = -1;

    private int mLastDetectedNoteIx = -1;
    private boolean mHasNotifiedDetected = false;
    private boolean mHasNotifiedUndetected = false;


    public NoteProcessor() {

    }

    // todo: for now, pitch detection is only monophonic;
    //       if polyphonic detection is added in later, then
    //       this will need to be reworked
    public void onPitchDetected(int noteIx, float probability, boolean isPitched) {
        // todo: use other parameters later to better filter out noise / incorrect guesses
        if (noteIx != mPrevNoteHeardIx) {
            mHasNotifiedDetected = false;
            if (!mHasNotifiedUndetected && mLastDetectedNoteIx != -1) {
                for (NoteProcessorListener listener : mListeners) {
                    listener.notifyNoteUndetected(mLastDetectedNoteIx);
                }
                mHasNotifiedUndetected = true;
            }
            if (noteIx != -1) {
                mPrevHeardTimeStamp = System.currentTimeMillis();
            }
            mPrevNoteHeardIx = noteIx;
        }
        else if (noteIx != -1 && !mHasNotifiedDetected && hasMetFilterLengthThreshold()) {
            for (NoteProcessorListener listener : mListeners) {
                listener.notifyNoteDetected(noteIx);
            }
            mLastDetectedNoteIx = noteIx;
            mHasNotifiedUndetected = false;
            mHasNotifiedDetected = true;
        }
    }

    public void addNoteProcessorListener(NoteProcessorListener listener) {
        mListeners.add(listener);
    }

    public void removeNoteProcessorListener(NoteProcessorListener listener) {
        mListeners.remove(listener);
    }

    private boolean hasMetFilterLengthThreshold() {
        return (int) (System.currentTimeMillis() - mPrevHeardTimeStamp) > mMillisRequired;
    }

}
