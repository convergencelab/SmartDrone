package com.example.smartdrone.Models;

import com.example.smartdrone.KeyFinder;
import com.example.smartdrone.VoicingTemplate;

public class DroneSoundModel {
    private VoicingModel voicingModel;
    private MidiDriverModel midiDriverModel;
    private KeyFinder keyFinder;

    private int pluginIx;
    private int modeIx;
    private VoicingTemplate curTemplate;

    public DroneSoundModel(int pluginIx, int modeIx, VoicingTemplate voicingTemplate) {
        voicingModel = new VoicingModel();
        midiDriverModel = new MidiDriverModel();
        keyFinder = new KeyFinder();

        this.pluginIx = pluginIx;
        this.modeIx = modeIx;
        this.curTemplate = voicingTemplate;
    }


    public int getPluginIx() {
        return pluginIx;
    }

    public void setPluginIx(int pluginIx) {
        this.pluginIx = pluginIx;
    }

    public int getModeIx() {
        return modeIx;
    }

    public void setModeIx(int modeIx) {
        this.modeIx = modeIx;
    }

    public VoicingTemplate getCurTemplate() {
        return curTemplate;
    }

    public void setCurTemplate(VoicingTemplate curTemplate) {
        this.curTemplate = curTemplate;
    }
}
