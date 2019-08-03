package com.convergencelabstfx.smartdrone.models;

import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.KeyFinder;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public class DroneSoundModel {
    private HarmonyGenerator generator;
    private VoicingModel voicingModel;
    private MidiDriverModel midiDriverModel;
    private KeyFinder keyFinder;

    private int modeIx;
    private boolean hasBassNote;
    private VoicingTemplate curTemplate;

    public DroneSoundModel(int pluginIx, int modeIx, boolean hasBassNote, VoicingTemplate voicingTemplate) {
        voicingModel = new VoicingModel();
        midiDriverModel = new MidiDriverModel();
        keyFinder = new KeyFinder();
        generator = new HarmonyGenerator();

        midiDriverModel.setPlugin(pluginIx);
        this.modeIx = modeIx;
        this.hasBassNote = hasBassNote;
        this.curTemplate = voicingTemplate;
    }

    public int getModeIx() {
        return modeIx;
    }

    public void setModeIx(int modeIx) {
        this.modeIx = modeIx;
        changePlayBack();
    }

    public VoicingTemplate getCurTemplate() {
        return curTemplate;
    }

    public void setCurTemplate(VoicingTemplate curTemplate) {
        this.curTemplate = curTemplate;
        changePlayBack();
    }

    public MidiDriverModel getMidiDriverModel() {
        return midiDriverModel;
    }

    public void setMidiDriverModel(MidiDriverModel midiDriverModel) {
        this.midiDriverModel = midiDriverModel;
    }

    public void initializePlayback() {
        midiDriverModel.getMidiDriver().start();
        midiDriverModel.sendMidiSetup();
    }

    public void stopPlayback() {
        midiDriverModel.getMidiDriver().stop();
    }

    public void updatePlaybackPlugin() {
        midiDriverModel.sendMidiSetup();
    }

    public void setPlugin(int plugin) {
        this.midiDriverModel.setPlugin(plugin);
        changePlayBack();
    }

    public void changePlayBack() {
        midiDriverModel.sendMidiSetup();
        //todo comeback to this later
        Voicing voicing = generator.generateVoicing(curTemplate, keyFinder.getModeTemplate(modeIx), keyFinder.getKeyAtIndex(0)); // todo: default c major for now
        midiDriverModel.playVoicing(voicing);
    }

    public void setHasBassNote(boolean hasBassNote) {
        this.hasBassNote = hasBassNote;
        changePlayBack();
    }

    public KeyFinder getKeyFinder() {
        return keyFinder;
    }
}
