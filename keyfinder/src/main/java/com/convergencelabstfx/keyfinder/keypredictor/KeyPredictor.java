package com.convergencelabstfx.keyfinder.keypredictor;


import java.util.ArrayList;
import java.util.List;

public abstract class KeyPredictor {

    public static final int TYPE_PHRASE_PREDICTOR = 0;
    public static final int TYPE_ANL_PREDICTOR = 1;

    public static final int DEFAULT_NOTE_EXPIRATION_LENGTH = 3000;

    // todo: i'm not totally sold on noteExpiration being a part of this class
    private int noteExpirationLength = DEFAULT_NOTE_EXPIRATION_LENGTH;

    final private List<KeyPredictorListener> listeners = new ArrayList<>();

    public abstract void noteDetected(int note);

    // Todo: couldn't think of what the proper name should actually be, so it is 'undetected' for now
    public abstract void noteUndetected(int note);

    public int getNoteExpirationLength() {
        return noteExpirationLength;
    }

    public void setNoteExpirationLength(int noteExpirationLength) {
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
