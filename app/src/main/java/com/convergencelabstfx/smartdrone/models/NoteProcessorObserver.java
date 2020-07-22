package com.convergencelabstfx.smartdrone.models;

import com.convergencelabstfx.keyfinder.Note;

public interface NoteProcessorObserver {

    void notifyNoteDetected(Note note);

    void notifyNoteUndetected(Note note);

}
