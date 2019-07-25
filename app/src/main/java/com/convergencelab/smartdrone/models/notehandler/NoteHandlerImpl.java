package com.convergencelab.smartdrone.models.notehandler;

import com.convergencelab.smartdrone.utility.DroneLog;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class NoteHandlerImpl implements NoteHandler, Observer {

    private int mLenFilter;

    private KeyFinder mKeyFinder;

    private List<KeyChangeListener> listeners;

    /**
     * Used to stamp time of note heard.
     */
    private long mTimeHeard;

    /**
     * Last heard note.
     */
    private Note mLastHeard;

    /**
     * Last added note.
     */
    private Note mLastAdded;

    /**
     * Note ready to have timer started.
     */
    private Note mToStart;

    /**
     * Flag if a note timer is ready to be started.
     */
    private boolean noteTimerIsQueued;

    public NoteHandlerImpl(int parentScale, int lenFilter) {
        mKeyFinder = new KeyFinder();
        listeners = new ArrayList<>();
        noteTimerIsQueued = false;

        mKeyFinder.setParentKeyList(parentScale);
        mKeyFinder.addObserver(this);
        mLenFilter = lenFilter;
    }

    @Override
    public void start() {
        // Does nothing for now.
    }

    @Override
    public void clear() {
        mKeyFinder.cleanse();
    }

    @Override
    public void handleNote(int noteIx) {
        Note curHeard;
        // No note heard
        if (noteIx == -1) {
            curHeard = null;
        }
        // Note heard
        else {
            curHeard = mKeyFinder.getNote(noteIx);
        }

        // Different note heard (can be null)
        if (noteChangeDetected(curHeard)) {
            mLastHeard = curHeard;
            mTimeHeard = System.currentTimeMillis();
            mLastAdded = null;
            if (noteTimerIsQueued) {
                mKeyFinder.scheduleNoteRemoval(mToStart);
                noteTimerIsQueued = false;
            }
        }
        // Same note heard
        else if (noteCanBeAdded(curHeard)) {
            mLastAdded = curHeard;
            mKeyFinder.addNoteToList(curHeard);
            DroneLog.noteList(mKeyFinder.getActiveNotesString()); // Debug statement
            queueNoteTimer(curHeard);
            if (mKeyFinder.noteIsScheduled(curHeard)) {
                mKeyFinder.cancelNoteRemoval(curHeard);
            }
        }
    }

    @Override
    public AbstractKey getActiveKey() {
        return mKeyFinder.getActiveKey();
    }

    @Override
    public void setKeyTimerLen(int timerLen) {
        mKeyFinder.setKeyTimerLength(timerLen);
    }

    @Override
    public void setNoteLengthFilter(int millis) {
        mLenFilter = millis;
    }

    @Override
    public void setParentScale(int parentScale) {
        mKeyFinder.setParentKeyList(parentScale);
    }

    @Override
    public ModeTemplate getModeTemplate(int templateIx) {
        return mKeyFinder.getModeTemplate(templateIx);
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

    @Override
    public void update(Observable o, Object arg) {
        KeyFinder kf = (KeyFinder) o;
        AbstractKey activeKey = kf.getActiveKey();

        for (KeyChangeListener listener : listeners) {
            listener.handleKeyChange(activeKey);
        }
    }

    /**
     * Check if current note is not the same as last heard note.
     * @param       curNote Note; current heard note.
     * @return      boolean; true if curNote is different than lastHeardNote.
     */
    private boolean noteChangeDetected(Note curNote) {
        return curNote != mLastHeard;
    }

    /**
     * Three conditions in order to return true.
     * 1) Note must be detected; not NULL_NOTE.
     * 2) Note was not the last note to be added to the list.
     * 3) Note note must be heard for the required amount of time.
     *
     * @param       toCheck Note; note to check.
     * @return      boolean; true if conditions met.
     */
    private boolean noteCanBeAdded(Note toCheck) {
        return toCheck != null && toCheck != mLastAdded && noteFilterLengthMet();
    }

    /**
     * Check if the note filter length has been reached.
     * @return      boolean; true if note has been heard as long as the filter length.
     */
    private boolean noteFilterLengthMet() {
        return (System.currentTimeMillis() - mTimeHeard) >= mLenFilter;
    }

    /**
     * Flags timer for note is ready to be started.
     * @param       toQueue Note; note to queue.
     */
    private void queueNoteTimer(Note toQueue) {
        noteTimerIsQueued = true;
        mToStart = toQueue;
    }
}
