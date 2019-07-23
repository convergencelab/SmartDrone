package com.convergencelab.smartdrone.Models.notefilter;

import android.content.SharedPreferences;

import com.convergencelab.smartdrone.Utility.DronePreferences;

public class NoteFilterModel implements NoteFilterInterface {
    /**
     * Used to retrieve saved data for note length filter.
     */
    private SharedPreferences mPrefs;


    NoteFilterModel(SharedPreferences prefs) {
        mPrefs = prefs;
    }

    /**
     * Get number of milliseconds for note filter.
     * @return length of note filter.
     */
    @Override
    public int getFilterLength() {
        return Integer.parseInt(DronePreferences.getNoteFilterLenPref(mPrefs));
    }
}
