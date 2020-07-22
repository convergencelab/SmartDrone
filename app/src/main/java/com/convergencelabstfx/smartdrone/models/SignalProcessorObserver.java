package com.convergencelabstfx.smartdrone.models;

public interface SignalProcessorObserver {

    void handlePitchResult(int pitch, float probability, boolean isPitched);

}
