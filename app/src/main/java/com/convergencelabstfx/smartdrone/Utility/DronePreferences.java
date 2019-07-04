package com.convergencelabstfx.smartdrone.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.convergencelabstfx.smartdrone.Constants;

public class DronePreferences {

    private static final String BASS_NOTE_KEY = "bassNoteEnabled";
    private static final String USER_MODE_KEY = "userModeIx";
    private static final String CUR_TEMP_KEY = "curTemplate";
    private static final String ALL_TEMP_KEY = "allTemplates";
    private static final String USER_PLUGIN_KEY = "userPlugin";
    public static final String NOTE_LEN_KEY = "noteLen";
    public static final String KEY_SENS_KEY = "keySens"; // TODO: refactor drone_preferences; has hardcoded key


    //todo refactor in these keys
    private static final String SAVED_VOICING_KEY = "saved_voicing_key"; //todo why tf does this one use different naming convention?
    private static final String ACTIVE_KEY_IX_KEY = "active_key_ix";

    /**
     * Get bass switch status from shared preferences.
     *
     * @param context Context; context.
     * @return boolean; bass switch boolean.
     */
    public static boolean getStoredBassPref(Context context) {
        return getSharedPrefs(context).getBoolean(BASS_NOTE_KEY, true);
    }

    /**
     * Put bass switch status in shared preferences.
     *
     * @param context Context, context.
     * @param bool    boolean; bass switch boolean.
     */
    public static void setStoredBassPref(Context context, boolean bool) {
        getSharedPrefs(context).edit().putBoolean(BASS_NOTE_KEY, bool).apply();
    }

    /**
     * Get mode index from shared preferences.
     *
     * @param context Context; context.
     * @return int; mode index.
     */
    public static int getStoredModePref(Context context) {
        return getSharedPrefs(context).getInt(USER_MODE_KEY, 0);
    }

    /**
     * Put mode index in shared preferences.
     *
     * @param context Context; context.
     * @param modeIx  int; mode index.
     */
    public static void setStoredModePref(Context context, int modeIx) {
        getSharedPrefs(context).edit().putInt(USER_MODE_KEY, modeIx).apply();
    }

    /**
     * Get plugin index from shared preferences.
     *
     * @param context Context; context.
     * @return int; plugin index.
     */
    public static int getStoredPluginPref(Context context) {
        return getSharedPrefs(context).getInt(USER_PLUGIN_KEY, 0);
    }

    /**
     * Put plugin index in shared preferences.
     *
     * @param context  Context; context.
     * @param pluginIx int; plugin index.
     */
    public static void setStoredPluginPref(Context context, int pluginIx) {
        getSharedPrefs(context).edit().putInt(USER_PLUGIN_KEY, pluginIx).apply();
    }

    /**
     * Get current voicing template from shared preferences.
     *
     * @param context Context; context.
     * @return String; flattened template.
     */
    public static String getCurTemplatePref(Context context) {
        return getSharedPrefs(context).getString(CUR_TEMP_KEY, Constants.DEFAULT_TEMPLATE);
    }

    /**
     * Store current voicing template in shared preferences.
     *
     * @param context           Context; context.
     * @param flattenedTemplate String; flattened template.
     */
    public static void setCurTemplatePref(Context context, String flattenedTemplate) {
        getSharedPrefs(context).edit().putString(CUR_TEMP_KEY, flattenedTemplate).apply();
    }

    /**
     * Get current voicing template from shared preferences.
     *
     * @param context Context; context.
     * @return String; flattened template list.
     */
    public static String getAllTemplatePref(Context context) {
        return getSharedPrefs(context).getString(ALL_TEMP_KEY, Constants.DEFAULT_TEMPLATE_LIST);
    }

    /**
     * Store all voicing templates in shared preferences.
     *
     * @param context               Context; context.
     * @param flattenedTemplateList String; list of flattened templates.
     */
    public static void setAllTemplatePref(Context context, String flattenedTemplateList) {
        getSharedPrefs(context).edit().putString(ALL_TEMP_KEY, flattenedTemplateList).apply();
    }

    /**
     * Get active key sensitivity from shared preferences.
     *
     * @param context Context; activity making function call.
     */
    public static String getActiveKeySensPref(Context context) {
        return getSharedPrefs(context).getString(KEY_SENS_KEY, "3"); //todo refactor hardcoded AND change over to integers
    }

    /**
     * Get length of note filter from shared preferences.
     *
     * @param context Context; activity making function call.
     */
    public static String getNoteFilterLenPref(Context context) {
        return getSharedPrefs(context).getString(NOTE_LEN_KEY, "60"); //todo refactor hardcoded AND change over to integers
    }

    /**
     * Return shared preferences object.
     *
     * @param context Context; context.
     * @return SharedPreferences; shared preferences object.
     */
    private static SharedPreferences getSharedPrefs(Context context) {
        return android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }
}