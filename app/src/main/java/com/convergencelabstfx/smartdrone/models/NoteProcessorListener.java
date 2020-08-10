package com.convergencelabstfx.smartdrone.models;

public interface NoteProcessorListener {

    void notifyNoteDetected(int note);

    void notifyNoteUndetected(int note);

}
