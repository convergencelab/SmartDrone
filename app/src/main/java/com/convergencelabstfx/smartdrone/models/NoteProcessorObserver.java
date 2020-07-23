package com.convergencelabstfx.smartdrone.models;

public interface NoteProcessorObserver {

    void notifyNoteDetected(int note);

    void notifyNoteUndetected(int note);

}
