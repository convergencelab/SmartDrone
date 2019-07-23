package com.convergencelab.smartdrone.Models.pitchprocessor;


public interface PitchProcessorInterface {
    
    void start();
    
    void stop();
    
    void addObserver(PitchProcessorObserver observer);

    void removeObserver(PitchProcessorObserver observer);

}
