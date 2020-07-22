package com.convergencelabstfx.smartdrone.models;

public interface NoteProcessorObserver {

    // Millis heard refers to the consecutive amount of time
    // that the note has been heard for.
    void notifyNoteResult(int note, int millisHeard);

}
