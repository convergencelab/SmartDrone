package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;

import com.convergencelab.smartdrone.Models.KeyFinderModel;
import com.convergencelab.smartdrone.Models.MidiDriverModel;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Voicing;

public class TemplateCreatorDataSourceImpl implements TemplateCreatorDataSource {

    private MidiDriverModel mMidiDriverModel;
    private KeyFinderModel mKeyFinderModel; // Todo: dunno if needed.
    private HarmonyGenerator mHarmonyGenerator;
    private SharedPreferences mPreferences;

    private AbstractKey mCurKey;
    private ModeTemplate mCurMode;

    // Todo: Make private and create method getInstance()
    public TemplateCreatorDataSourceImpl (SharedPreferences preferences) {
        mMidiDriverModel = new MidiDriverModel();
        mKeyFinderModel = new KeyFinderModel();
        mHarmonyGenerator = new HarmonyGenerator();
        mPreferences = preferences;
    }

    @Override
    public void initialize() {
        loadKeyFinderData();
        Voicing initVoicing = mHarmonyGenerator.generateVoicing(INIT_TEMP, mCurMode, mCurKey);
        mMidiDriverModel.sendMidiSetup();
        mMidiDriverModel.playVoicing(initVoicing);
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

    private void loadKeyFinderData() {
        // Todo: Add in DI for shared prefs.
        //       For now will just be C major.
        int modeIx = 0;
        int keyIx = 0;

        mCurKey = mKeyFinderModel.getKeyFinder().getKeyAtIndex(keyIx);
        mCurMode = mKeyFinderModel.getKeyFinder().getModeTemplate(modeIx);
    }


}
