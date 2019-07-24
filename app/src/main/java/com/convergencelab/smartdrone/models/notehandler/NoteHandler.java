package com.convergencelab.smartdrone.models.notehandler;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.ModeTemplate;

import java.util.Observer;

public interface NoteHandler {

    void start();

    void clear();

    void handleNote(int noteIx);

    AbstractKey getActiveKey();

    void setKeyTimerLen(int timerLen);

    void setNoteLengthFilter(int millis);

    void setParentScale(int parentScale);

    ModeTemplate getModeTemplate(int templateIx);

    void addKeyChangeListener(KeyChangeListener toAdd);

    void removeKeyChangeListener(KeyChangeListener toRemove);

}
