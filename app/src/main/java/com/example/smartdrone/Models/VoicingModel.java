package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Voicing;
import com.example.smartdrone.VoicingTemplate;
import com.example.smartdrone.VoicingTemplateCollection;

class VoicingModel {
    /* Stock Voicing Indices */
//    private int[] DRONE       = { 0                     };
//    private int[] MAJOR_TRIAD = { 0,  7, 16             };
//    private int[] MAJOR_9     = { 0,  7, 16, 23, 26     };
//    private int[] GABE        = { 2, 12, 17, 23         };
//    private int[] LYDIAN      = { 5, 12, 19, 26, 33, 40 };
//    private int[] MIXOLYDIAN  = { 7, 17, 21, 24, 28, 33 };
//    private int[] PHRYGIAN    = { 4, 17, 21, 23, 28     };
    private int[] DRONE           = { 0           };
    private int[] TRIAD_CLOSED    = { 0,  2, 4    };
    private int[] TRIAD_OPEN      = { 0,  4, 9    };
    private int[] SEVENTH_CLOSED  = { 0,  2, 4, 6 };
    private int[] SEVENTH_DROP_II = { 0,  4, 6, 9 };
    private int[] V_MAJOR_OVER_I  = { 0,  6, 8    };

    //todo create database for all current names.
    /**
     * Names of stock voicings.
     */
    String[] STOCK_VOICINGS_NAMES = { // todo change to private later
//            "Drone",
//            "Major Triad",
//            "Major 9",
//            "Gabe",
//            "Lydian",
//            "Mixolydian",
//            "Phrygian"
            "Drone",
            "Triad (Closed)",
            "Triad (Open)",
            "7th (Closed)",
            "7th (Drop II)",
            "V Major / I"
    };

    /**
     * Voicings indices container.
     */
    private int[][] STOCK_VOICINGS_INDICES = {
//            DRONE,
//            MAJOR_TRIAD,
//            MAJOR_9,
//            GABE,
//            LYDIAN,
//            MIXOLYDIAN,
//            PHRYGIAN
            DRONE,
            TRIAD_CLOSED,
            TRIAD_OPEN,
            SEVENTH_CLOSED,
            SEVENTH_DROP_II,
            V_MAJOR_OVER_I,
    };

    /**
     * Voicing container.
     */
    private VoicingTemplateCollection voicingTemplateCollection;

    /**
     * Constructor.
     * Constructs stock voicings.
     */
    VoicingModel() {
        this.voicingTemplateCollection = new VoicingTemplateCollection();
        // Load stock voicings into voicing collection. // todo: refactor for persistent data
        for (int i = 0; i < STOCK_VOICINGS_NAMES.length ; i++) {
            this.voicingTemplateCollection.addVoicingTemplate(STOCK_VOICINGS_NAMES[i], STOCK_VOICINGS_INDICES[i]);
        }
    }

    /**
     * Get voicing collection.
     * @return      VoicingCollection; voicing collection.
     */
    VoicingTemplateCollection getVoicingTemplateCollection() {
        return voicingTemplateCollection;
    }

//    /**
//     * Creates new voicing with transposed indexes.
//     * @param       toTranspose Voicing; voicing to transpose.
//     * @return      Voicing; transposed voicing.
//     */
//    public VoicingTemplate transpose(Voicing toTranspose, int interval) {
//        int[] transposedVoicings = new int[toTranspose.getVoiceIxs().length];
//        for (int i = 0; i < 12 ; i++) { //todo refactor hardcoded '12'
//            transposedVoicings[i] = toTranspose.getVoiceIxs()[i] + interval;
//        }
//        return new Voicing(transposedVoicings);
//    }

//    public int getTranspositionInterval(int keyIx) {
//        return key
//    }
}
