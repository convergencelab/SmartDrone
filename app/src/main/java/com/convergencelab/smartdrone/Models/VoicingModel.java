package com.convergencelab.smartdrone.Models;

import com.example.smartdrone.VoicingTemplateCollection;

import java.util.ArrayList;

public class VoicingModel {
    /* Stock Voicing Indices */
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
            DRONE,
            TRIAD_CLOSED,
            TRIAD_OPEN,
            SEVENTH_CLOSED,
            SEVENTH_DROP_II,
            V_MAJOR_OVER_I,
    };

    public ArrayList<String> defaultVoicings = new ArrayList<>();


    /**
     * Voicing container.
     */
    private VoicingTemplateCollection voicingTemplateCollection;

    /**
     * Constructor.
     * Constructs stock voicings.
     */
    public VoicingModel() {
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
}
