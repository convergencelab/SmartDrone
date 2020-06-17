package com.convergencelabstfx.smartdrone.models.notehandler;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.ModeTemplate;

public interface NoteHandler {

    void start();

    void clear();

    void handleNote(int noteIx); // Move to notefilter

    AbstractKey getActiveKey();

    void setKeyTimerLen(int timerLen);

    void setNoteLengthFilter(int millis); // Move to notefilter

    void setParentScale(int parentScale);

    ModeTemplate getModeTemplate(int templateIx);

    void addKeyChangeListener(KeyChangeListener toAdd);

    void removeKeyChangeListener(KeyChangeListener toRemove);

}
