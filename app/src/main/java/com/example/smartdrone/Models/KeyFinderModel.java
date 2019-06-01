package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.Note;

public class KeyFinderModel {
    /**
     * Handles note/key relationship.
     */
    public KeyFinder keyFinder;

    /**
     * Constructor.
     */
    public KeyFinderModel() {
        keyFinder = new KeyFinder();
        keyFinder.setNoteTimerLength(Constants.NOTE_TIMER_LEN);
    }

    /**
     * Get key finder.
     * @return      KeyFinder; key finder.
     */
    public KeyFinder getKeyFinder() {
        return keyFinder;
    }

    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = keyFinder.getAllNotes().getNoteAtIndex(noteIx);
        keyFinder.addNoteToList(curNote);
        Log.d(Constants.MESSAGE_LOG_ADD, curNote.getName());
        Log.d(Constants.MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
    }
}
