package com.convergencelab.smartdrone.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class DronePreferences {

    private static final String BASS_NOTE_KEY = "bassNoteEnabled";
    public static final String USER_MODE_KEY = "userModeIx";


    /**
     * Construct and return shared preferences object.
     * @param       context Context; context.
     * @return      SharedPreferences; shared preferences object.
     */
    private static SharedPreferences getSharedPrefs(Context context) {
        return android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get bass switch status from shared preferences.
     * @param       context Context; context.
     * @return      boolean; bass switch boolean.
     */
    public static boolean getStoredBassPref(Context context) {
        return getSharedPrefs(context).getBoolean(BASS_NOTE_KEY, true);
    }

    /**
     * Put bass switch status in shared preferences.
     * @param       context Context, context.
     * @param       bool boolean; bass switch boolean.
     */
    public static void setStoredBassPref(Context context, boolean bool) {
        getSharedPrefs(context).edit().putBoolean(BASS_NOTE_KEY, bool).apply();
    }

    /**
     * Get mode index from shared preferences.
     * @param       context Context; context.
     * @return      int; mode index.
     */
    public static int getStoredModePref(Context context) {
        return getSharedPrefs(context).getInt(USER_MODE_KEY, 0);
    }

    /**
     * Put mode index in shared preferences.
     * @param       context Context; context.
     * @param       modeIx int; mode index.
     */
    public static void setStoredModePref(Context context, int modeIx) {
        getSharedPrefs(context).edit().putInt(USER_MODE_KEY, modeIx).apply();
    }
}
