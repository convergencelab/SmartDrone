package com.example.smartdrone.Models;

import com.example.smartdrone.Constants;
import com.example.smartdrone.KeyFinder;

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
}
