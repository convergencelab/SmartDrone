package com.convergencelab.smartdrone.Models.Data;


import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;

public interface DroneDataSource {

    Plugin[] getPlugins();

    ArrayList<String> getTemplates();

    int getPluginIx();

    void savePluginIx(int pluginIx);

    int getModeIx();

    void saveModeIx(int modeIx);

    int getParentScale();

    void saveParentScale(int scaleIx);

    VoicingTemplate getTemplate();

    void saveTemplate(VoicingTemplate template);

    int getNoteLengthFilter();

    int getActiveKeySensitivity();
}
