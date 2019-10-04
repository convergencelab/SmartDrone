package com.convergencelabstfx.smartdrone.models.signalprocessor;


public interface SignalProcessor {
    
    void start();
    
    void stop();
    
    void addPitchListener(PitchProcessorObserver observer);

    void removePitchListener(PitchProcessorObserver observer);

    boolean isRunning();

}
