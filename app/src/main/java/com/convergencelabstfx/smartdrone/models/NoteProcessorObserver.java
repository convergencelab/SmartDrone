package com.convergencelabstfx.smartdrone.models;

import com.convergencelabstfx.keyfinder.Note;

public interface NoteProcessorObserver {

    void notifyNoteDetected(int note);

    void notifyNoteUndetected(int note);

}
