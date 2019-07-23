package com.convergencelab.smartdrone.models.keyfinder;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

// Todo: Add key change listener in future.

public class KeyFinderImpl implements KeyFinderInterface {

    private final int mLenFilter;

    private KeyFinder mKeyFinder;

    KeyFinderImpl(int parentScale, int lenFilter) {
        mKeyFinder = new KeyFinder();

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
}
