package com.convergencelab.smartdrone.models.pitchprocessor;


public interface PitchProcessorInterface {
    
    void start();
    
    void stop();
    
    void addPitchListener(PitchProcessorObserver observer);

    void removePitchListener(PitchProcessorObserver observer);

}
