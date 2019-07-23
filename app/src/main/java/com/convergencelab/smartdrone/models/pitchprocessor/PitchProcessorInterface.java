package com.convergencelab.smartdrone.models.pitchprocessor;


public interface PitchProcessorInterface {
    
    void start();
    
    void stop();
    
    void addObserver(PitchProcessorObserver observer);

    void removeObserver(PitchProcessorObserver observer);

}
