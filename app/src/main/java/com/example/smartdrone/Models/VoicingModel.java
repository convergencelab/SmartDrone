package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.VoicingCollection;

class VoicingModel {
    /* Stock Voicing Indices */
    private int[] DRONE       = { 0                     };
    private int[] MAJOR_TRIAD = { 0,  7, 16             };
    private int[] MAJOR_9     = { 0,  7, 16, 23, 26     };
    private int[] GABE        = { 2, 12, 17, 23         };
    private int[] LYDIAN      = { 5, 12, 19, 26, 33, 40 };
    private int[] MIXOLYDIAN  = { 7, 17, 21, 24, 28, 33 };
    private int[] PHRYGIAN    = { 4, 17, 21, 23, 28     };

    //todo create database for all current names.
    /**
     * Names of stock voicings.
     */
    String[] STOCK_VOICINGS_NAMES = { // todo change to private later
            "Drone",
            "Major Triad",
            "Major 9",
            "Gabe",
            "Lydian",
            "Mixolydian",
            "Phrygian"
    };

    /**
     * Voicings indices container.
     */
    private int[][] STOCK_VOICINGS_INDICES = {
            DRONE,
            MAJOR_TRIAD,
            MAJOR_9,
            GABE,
            LYDIAN,
            MIXOLYDIAN,
            PHRYGIAN
    };

    /**
     * Voicing container.
     */
    private VoicingCollection voicingCollection;

    /**
     * Constructor.
     * Constructs stock voicings.
     */
    VoicingModel() {
        this.voicingCollection = new VoicingCollection();
        // Load stock voicings into voicing collection. // todo adapt for persistent data
        for (int i = 0; i < STOCK_VOICINGS_NAMES.length ; i++) {
            Log.d(Constants.MESSAGE_LOG_VOICING, "Adding voicing: " + STOCK_VOICINGS_NAMES[i]); //todo remove when debugged
            this.voicingCollection.addVoicing(STOCK_VOICINGS_NAMES[i], STOCK_VOICINGS_INDICES[i]);
        }
    }

    /**
     * Get voicing collection.
     * @return      VoicingCollection; voicing collection.
     */
    VoicingCollection getVoicingCollection() {
        return voicingCollection;
    }
}
