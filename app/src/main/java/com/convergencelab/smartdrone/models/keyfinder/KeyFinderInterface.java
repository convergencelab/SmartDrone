package com.convergencelab.smartdrone.models.keyfinder;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

public interface KeyFinderInterface {

    void start();

    void clear();

    void addNote(Note toAdd);

    void startNoteTimer(Note toStart);

    void cancelNoteTimer(Note toCancel);

    AbstractKey getActiveKey();

    void setKeyTimerLen(int timerLen);

    void setParentScale(int parentScale);

    ModeTemplate getModeTemplate(int templateIx);

}
