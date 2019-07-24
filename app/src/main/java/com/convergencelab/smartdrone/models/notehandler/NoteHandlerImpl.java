package com.convergencelab.smartdrone.models.notehandler;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.ModeTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

// Todo: Add key change listener in future.

public class NoteHandlerImpl implements NoteHandler, Observer {

    private final int mLenFilter;

    private KeyFinder mKeyFinder;

    private List<KeyChangeListener> listeners;

    NoteHandlerImpl(int parentScale, int lenFilter) {
        mKeyFinder = new KeyFinder();
        listeners = new ArrayList<>();

        mKeyFinder.setParentKeyList(parentScale);
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
        // Todo: this is where I left off
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

        // Do whatever
    }
}
