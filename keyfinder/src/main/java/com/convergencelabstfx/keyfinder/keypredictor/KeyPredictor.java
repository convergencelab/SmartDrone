package com.convergencelabstfx.keyfinder.keypredictor;


import com.convergencelabstfx.keyfinder.Note;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyPredictor {

    // todo: i'm not totally sold on noteExpiration being a part of this class
    // Milliseconds
    private long noteExpirationLength = -1;

    final private List<KeyPredictorListener> listeners = new ArrayList<>();

    public abstract void noteDetected(Note note);

    // Todo: couldn't think of what the proper name should actually be, so it is 'undetected' for now
    public abstract void noteUndetected(Note note);

    public long getNoteExpirationLength() {
        return noteExpirationLength;
    }

    public void setNoteExpirationLength(long noteExpirationLength) {
        this.noteExpirationLength = noteExpirationLength;
    }

    public void addListener(KeyPredictorListener listener) {
        listeners.add(listener);
    }

    // TODO: test remove function; not neccessary right now
    public void removeListener(KeyPredictorListener listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(int newKey) {
        for (KeyPredictorListener listener : listeners) {
            listener.notifyKeyPrediction(newKey);
        }
    }

}
