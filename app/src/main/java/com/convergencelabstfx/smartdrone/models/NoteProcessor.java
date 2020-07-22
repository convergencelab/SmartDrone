package com.convergencelabstfx.smartdrone.models;

import com.convergencelabstfx.keyfinder.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Process incoming pitch data so that noise can be filtered out from actual notes.
 */
public class NoteProcessor {

    // todo: tinker with this value
    final private float PROBABILITY_THRESHOLD = 0.0f;

    final private List<NoteProcessorObserver> mListeners = new ArrayList<>();

    private Note mLastNoteHeard = null;

    public NoteProcessor() {

    }

    public void onPitchDetected(int noteIx, float probability, boolean isPitched) {
        // todo: use other parameters later to better filter out noise / incorrect guesses
        // No note heard
        if (noteIx == -1) {
            return;
        }
        if (mLastNoteHeard == null || noteIx != mLastNoteHeard.getIx()) {
            final Note note = new Note(noteIx);
            for (NoteProcessorObserver listener : mListeners) {
                listener.notifyNoteDetected(note);
                if (mLastNoteHeard != null) {
                    listener.notifyNoteUndetected(mLastNoteHeard);
                }
            }
            mLastNoteHeard = note;
        }
    }

    public void addNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.add(listener);
    }

    public void removeNoteProcessorListener(NoteProcessorObserver listener) {
        mListeners.remove(listener);
    }

}
