package com.convergencelab.smartdrone.models.data;

import android.content.SharedPreferences;

import com.convergencelab.smartdrone.utility.DronePreferences;
import com.convergencelab.smartdrone.VoicingHelper;
import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;
import java.util.HashSet;

public class DroneDataSourceImpl implements DroneDataSource {

    private static final int STRINGS = 48;
    private static final int CHOIR = 52;
    private static final int BRASS = 61;
    private final HashSet<String> nameSet;

    private SharedPreferences mPrefs;

    DroneDataSourceImpl(SharedPreferences prefs) {
        mPrefs = prefs;
        nameSet = VoicingHelper
                .getSetOfAllTemplateNames(DronePreferences.getAllTemplatePref(mPrefs));
    }

    // Not the best solution, but it works.
    @Override
    public Plugin[] getPlugins() {
        Plugin[] toReturn = new Plugin[3];
        toReturn[0] = new Plugin("Strings", STRINGS);
        toReturn[1] = new Plugin("Choir", CHOIR);
        toReturn[2] = new Plugin("Brass", BRASS);
        return toReturn;
    }

    @Override
    public ArrayList<String> getTemplates() {
        return VoicingHelper.inflateTemplateList(DronePreferences.getAllTemplatePref(mPrefs));
    }

    @Override
    public int getPluginIx() {
        return DronePreferences.getStoredPluginPref(mPrefs);
    }

    @Override
    public void savePluginIx(int pluginIx) {
        DronePreferences.setStoredPluginPref(mPrefs, pluginIx);
    }

    @Override
    public int getModeIx() {
        return DronePreferences.getStoredModePref(mPrefs);
    }

    @Override
    public void saveModeIx(int modeIx) {
        DronePreferences.setStoredModePref(mPrefs, modeIx);
    }

    @Override
    public int getParentScale() {
        return DronePreferences.getStoredParentScalePref(mPrefs);
    }

    @Override
    public void saveParentScale(int scaleIx) {
        DronePreferences.setStoredParentScalePref(mPrefs, scaleIx);
    }

    @Override
    public VoicingTemplate getTemplate() {
        String temp = DronePreferences.getCurTemplatePref(mPrefs);
        return VoicingHelper.decodeTemplate(temp);
    }

    @Override
    public void saveTemplate(VoicingTemplate template) {
        String temp = VoicingHelper.encodeTemplate(template);
        DronePreferences.setCurTemplatePref(mPrefs, temp);
    }

    // Todo: For some reason is still stored as string.
    @Override
    public int getNoteLengthFilter() {
        return Integer.parseInt(DronePreferences.getNoteFilterLenPref(mPrefs));
    }

    // Todo: For some reason is still stored as string.
    @Override
    public int getActiveKeySensitivity() {
        return Integer.parseInt(DronePreferences.getActiveKeySensPref(mPrefs));
    }

    @Override
    public boolean isDuplicateName(String name) {
        return nameSet.contains(name);
    }
}
