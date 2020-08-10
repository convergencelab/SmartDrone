package com.convergencelabstfx.smartdrone.models;

public interface SignalProcessorListener {

    void notifyPitchResult(int pitch, float probability, boolean isPitched);

}
