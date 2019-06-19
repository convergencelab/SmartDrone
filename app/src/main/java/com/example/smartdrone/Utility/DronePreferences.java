package com.example.smartdrone.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class DronePreferences {

    private static final String BASS_NOTE_KEY = "bassNoteEnabled";

    /**
     * Construct and return shared preferences object.
     * @param       context Context; context.
     * @return      SharedPreferences; shared preferences object.
     */
    private static SharedPreferences getSharedPrefs(Context context) {
        return android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get boolean for bass switch.
     * @param       context Context; context.
     * @return      boolean; data for bass switch.
     */
    public static boolean getStoredBassPref(Context context) {
        return getSharedPrefs(context).getBoolean(BASS_NOTE_KEY, true);
    }

    /**
     * Set boolean for bass switch.
     * @param       context Context, context.
     * @param       bool boolean; bass switch data.
     */
    public static void setStoredBassPref(Context context, boolean bool) {
        getSharedPrefs(context).edit().putBoolean(BASS_NOTE_KEY, bool).apply();
    }
}
