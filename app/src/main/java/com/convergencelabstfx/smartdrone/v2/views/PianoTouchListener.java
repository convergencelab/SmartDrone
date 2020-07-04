package com.convergencelabstfx.smartdrone.v2.views;

public interface PianoTouchListener {

    // todo: possibly pass the pianoview itself as a parameter
    void onPianoTouch(int key);

    void onPianoClick(int key);

}
