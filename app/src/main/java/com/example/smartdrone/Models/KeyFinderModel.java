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
     * Add note to Active Note list.
     * @param       toAdd Note; note to add.
     */
    void addNote(Note toAdd) {
        keyFinder.addNoteToList(toAdd);
        Log.d(Constants.MESSAGE_LOG_ADD, toAdd.getName());
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
     * Start timer for note.
     * @param       toStart Note; note to start.
     */
    void startNoteTimer(Note toStart) {
        toStart.startNoteTimer(keyFinder, Constants.NOTE_TIMER_LEN); // todo refactor library. Timers should be a method of KeyFinder class.
    }

    /**
     * Cancels timer for note.
     * Will stop the scheduled task of removing note from active note list.
     * @param       toCancel Note; note to cancel.
     */
    void cancelNoteTimer(Note toCancel) {
        toCancel.cancelNoteTimer();
    }
}
