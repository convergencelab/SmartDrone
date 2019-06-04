package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.Note;

public class KeyFinderModel {
    /**
     * Handles note/key relationship.
     */
    private KeyFinder keyFinder;

    /**
     * Length of key timer.
     * Key timer controls how long an inactive key must
     * be contender before it becomes active key.
     */
    private int keyTimerLength;

    /**
     * Constructor.
     */
    KeyFinderModel() {
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
    void addNote(int noteIx) {
        Note curNote = keyFinder.getAllNotes().getNoteAtIndex(noteIx);
        keyFinder.addNoteToList(curNote);
        Log.d(Constants.MESSAGE_LOG_ADD, curNote.getName());
        Log.d(Constants.MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
    }

    /**
     * Get key timer length.
     * @return      int; length of key timer in seconds.
     */
    public int getKeyTimerLength() {
        return keyTimerLength;
    }

    /**
     * Set key timer length.
     * @param       seconds int; length of key timer in seconds.
     */
    public void setKeyTimerLength(int seconds) {
        this.keyTimerLength = seconds;
    }

    /**
     * Starts timer for note.
     * Timer will remove note from active notes.
     * @param       noteIx int; index of note.
     */
    void startNoteTimer(int noteIx) {
        keyFinder.getAllNotes().getNoteAtIndex(noteIx)
                .startNoteTimer(keyFinder, Constants.NOTE_TIMER_LEN);
    }

    /**
     * Cancels timer for note.
     * Will stop the scheduled task of removing note from active note list.
     * @param       noteIx int; index of note.
     */
    void cancelNoteTimer(int noteIx) {
        keyFinder.getAllNotes().getNoteAtIndex(noteIx)
                .cancelNoteTimer();
    }
}
