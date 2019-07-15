package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.KeyFinder;

import org.billthefarmer.mididriver.MidiDriver;

public class TemplateCreatorDataSourceImpl implements TemplateCreatorDataSource {

    private MidiDriver mMidiDriver;
    private KeyFinder mKeyFinder;
    private HarmonyGenerator mHarmonyGenerator;
    // MidiDriver
    // KeyFinder
    // HarmonyGenerator

    @Override
    public void initializePlayback() {
        // send midi setup
    }

    @Override
    public void playNote() {

    }

    @Override
    public void stopNote() {

    }

    @Override
    public void saveTemplate() {

    }


}
