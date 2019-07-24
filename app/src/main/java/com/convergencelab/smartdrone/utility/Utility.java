package com.convergencelab.smartdrone.utility;

import com.example.keyfinder.MusicTheory;
import com.example.keyfinder.Voicing;

public class Utility {

    public static int[] voicingToIntArray(Voicing toConvert) {
        int[] toReturn = new int[toConvert.numVoices()];
        for (int i = 0; i < toConvert.numVoices(); i++) {
            toReturn[i] = toConvert.getVoice(i).getRawIx();
        }
        return toReturn;
    }

    public static final String[][] MODE_NAMES = {
            MusicTheory.MAJOR_MODE_NAMES,
            MusicTheory.MELODIC_MINOR_MODE_NAMES
    };

}
