package com.convergencelab.smartdrone.models.keyfinder;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;

public interface KeyFinderInterface {

    void start();

    void clear();

    void handleNote(int noteIx);

    AbstractKey getActiveKey();

    void setKeyTimerLen(int timerLen);

    void setNoteLengthFilter(int millis);

    void setParentScale(int parentScale);

    ModeTemplate getModeTemplate(int templateIx);

}
