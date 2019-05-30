package com.example.smartdrone;

public class DroneModel {

    /**
     * KeyFinder used for analyzing note data.
     */
    private KeyFinder keyFinder;

    /**
     * Constructor.
     */
    public DroneModel() {
        keyFinder = new KeyFinder();
    }

    /**
     * Get KeyFinder.
     * @return      KeyFinder; KeyFinder.
     */
    public KeyFinder getKeyFinder() {
        return keyFinder;
    }

}
