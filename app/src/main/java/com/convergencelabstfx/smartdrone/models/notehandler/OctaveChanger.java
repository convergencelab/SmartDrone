package com.convergencelabstfx.smartdrone.models.notehandler;

import android.util.Log;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

import java.util.ArrayList;
import java.util.List;

public class OctaveChanger implements NoteHandler {

    private KeyFinder keyFinder;

    private int lowOctaveIxLower = 40; // Low E
    private int lowOctaveIxUpper = 51; // Low Eb / D#

    private int activeKeyIx = -1;

    // If the lower octave was heard
    private boolean isLowerOctaveHeard;

    // Index of the lower octave heard
    private int lowerOctaveHeardIx;

    // Time the note was heard
    private long timeLowerOctaveHeard;

    private int mLenFilter;

    private List<KeyChangeListener> listeners;

    /**
     * Used to stamp time of note heard.
     */
    private long mTimeHeard;

    /**
     * Last heard note.
     */
    private int mLastHeard;

    /**
     * Last added note.
     */
    private int mLastAdded;

    /**
     * Note ready to have timer started.
     */
    private int mToStart;

    public OctaveChanger() {
        listeners = new ArrayList<>();
        isLowerOctaveHeard = true;
        lowerOctaveHeardIx = -1;
        keyFinder = new KeyFinder();
    }

    @Override
    public void start() {

    }

    @Override
    public void clear() {
        isLowerOctaveHeard = false;
        lowerOctaveHeardIx = -1;
    }

    @Override
    public void handleNote(int noteIx) {
        int curHeardIx;
        curHeardIx = noteIx;

        // Different note heard (can be null)
        if (noteChangeDetected(curHeardIx)) {
            mLastHeard = curHeardIx;
            mTimeHeard = System.currentTimeMillis();
            mLastAdded = -1;
        }
        // Same note heard
        else if (noteCanBeAdded(curHeardIx)) {
            Log.d("debug", "lowerOctaveHeardIx" + lowerOctaveHeardIx);
            Log.d("debug", "curHeard" + curHeardIx);

            // If lower already heard
            if (isLowerOctaveHeard) {
                if (curHeardIx == lowerOctaveHeardIx + 12
                        && System.currentTimeMillis() - timeLowerOctaveHeard < 1000) {
                    if (activeKeyIx != curHeardIx % 12) {
                        setActiveKey(curHeardIx % 12);
                    }
                }
            }
            if (curHeardIx >= lowOctaveIxLower && curHeardIx <= lowOctaveIxUpper){
                lowerOctaveHeardIx = curHeardIx;
                timeLowerOctaveHeard = System.currentTimeMillis();
            }
        }
    }

    @Override
    public AbstractKey getActiveKey() {
        return null;
    }

    @Override
    public void setKeyTimerLen(int timerLen) {

    }

    @Override
    public void setNoteLengthFilter(int millis) {
        mLenFilter = millis;
    }

    @Override
    public void setParentScale(int parentScale) {
        keyFinder.setParentKeyList(parentScale);
    }

    @Override
    public ModeTemplate getModeTemplate(int templateIx) {
        return keyFinder.getModeTemplate(templateIx);
    }

    @Override
    public void addKeyChangeListener(KeyChangeListener toAdd) {
        // Todo: Throw exception
        if (listeners.contains(toAdd)) {
            // Todo: Throw exception
            return;
        }
        listeners.add(toAdd);
    }

    @Override
    public void removeKeyChangeListener(KeyChangeListener toRemove) {
        if (!listeners.contains(toRemove)) {
            // Todo: Throw exception
            return;
        }
        listeners.remove(toRemove);
    }

    private boolean noteChangeDetected(int curNoteIx) {
        return curNoteIx != mLastHeard;
    }

    private boolean noteCanBeAdded(int toCheck) {
        Log.d("debug", "ix: " + String.valueOf(toCheck));
        return toCheck != -1  && noteFilterLengthMet()
                && toCheck >= lowOctaveIxLower && toCheck <= lowOctaveIxUpper + 12;
    }

    private boolean noteFilterLengthMet() {
        return (System.currentTimeMillis() - mTimeHeard) >= mLenFilter;
    }

    private void setActiveKey(int newIx) {
        Log.d("debug", "set Active key");

        activeKeyIx = newIx;
        AbstractKey activeKey = keyFinder.getKeyAtIndex(newIx);

        for (KeyChangeListener listener : listeners) {
            listener.handleKeyChange(activeKey);
        }
    }
}
