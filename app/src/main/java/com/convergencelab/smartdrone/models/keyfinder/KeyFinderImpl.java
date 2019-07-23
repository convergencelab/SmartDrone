package com.convergencelab.smartdrone.models.keyfinder;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

public class KeyFinderImpl implements KeyFinderInterface {
    private KeyFinder mKeyFinder;

    KeyFinderImpl(int parentScale) {
        mKeyFinder = new KeyFinder();
        mKeyFinder.setParentKeyList(parentScale);
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
    public void addNote(Note toAdd) {
        mKeyFinder.addNoteToList(toAdd);
    }

    @Override
    public void startNoteTimer(Note toStart) {
        mKeyFinder.scheduleNoteRemoval(toStart);
    }

    @Override
    public void cancelNoteTimer(Note toCancel) {
        mKeyFinder.cancelNoteRemoval(toCancel);
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
    public void setParentScale(int parentScale) {
        mKeyFinder.setParentKeyList(parentScale);
    }

    @Override
    public ModeTemplate getModeTemplate(int templateIx) {
        return mKeyFinder.getModeTemplate(templateIx);
    }
}
