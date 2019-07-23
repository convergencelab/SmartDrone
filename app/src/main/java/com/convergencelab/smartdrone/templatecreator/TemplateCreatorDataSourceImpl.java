package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;

import com.convergencelab.smartdrone.Models.KeyFinderModel;
import com.convergencelab.smartdrone.Models.MidiDriverModel;
import com.convergencelab.smartdrone.Utility.DronePreferences;
import com.convergencelab.smartdrone.VoicingHelper;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

import java.util.HashSet;

public class TemplateCreatorDataSourceImpl implements TemplateCreatorDataSource {

    private static final int DEFAULT_KEY_IX = 0;

    private final MidiDriverModel mMidiDriverModel;
    private final KeyFinderModel mKeyFinderModel;
    private final HarmonyGenerator mHarmonyGenerator;
    private final SharedPreferences mPreferences;

    private final HashSet<String> nameSet;

    private AbstractKey mCurKey;
    private ModeTemplate mCurMode;

    // Todo: Make private and create method getInstance()
    public TemplateCreatorDataSourceImpl (SharedPreferences preferences) {
        mMidiDriverModel = new MidiDriverModel();
        mKeyFinderModel = new KeyFinderModel();
        mHarmonyGenerator = new HarmonyGenerator();

        mPreferences = preferences;
        nameSet = VoicingHelper
                .getSetOfAllTemplateNames(DronePreferences.getAllTemplatePref(mPreferences));
    }

    @Override
    public void initialize() {
        loadKeyFinderData();
        mMidiDriverModel.setPlugin(48); // Todo fix harcoded string
        mMidiDriverModel.getMidiDriver().start();
        mMidiDriverModel.sendMidiSetup();
    }

    @Override
    public void saveTemplate(VoicingTemplate template) {
        VoicingHelper.addTemplateToPref(mPreferences, template);
        DronePreferences.setCurTemplatePref(mPreferences, VoicingHelper.encodeTemplate(template));
    }

    @Override
    public void playTone(Tone toPlay) {
        mMidiDriverModel.addNoteToPlayback(convertToneToNote(toPlay));
    }

    @Override
    public void stopTone(Tone toStop) {
        mMidiDriverModel.removeNoteFromPlayback(convertToneToNote(toStop));
    }

    @Override
    public boolean isDuplicateName(String name) {
        return nameSet.contains(name);
    }

    /**
     * Loads KeyFinder data: parent key pref; mode pref.
     */
    private void loadKeyFinderData() {
        int parentScaleIx = DronePreferences.getStoredParentScalePref(mPreferences);
        int modeIx = DronePreferences.getStoredModePref(mPreferences);

        mKeyFinderModel.getKeyFinder().setParentKeyList(parentScaleIx);
        mCurKey = mKeyFinderModel.getKeyFinder().getKeyAtIndex(DEFAULT_KEY_IX);
        mCurMode = mKeyFinderModel.getKeyFinder().getModeTemplate(modeIx);
    }

    // Todo: make this some sort of utility method
    private Note convertToneToNote(Tone toConvert) {
        return mHarmonyGenerator.generateNote(toConvert, mCurMode, mCurKey);
    }

    @Override
    public void endPlayback() {
        mMidiDriverModel.getMidiDriver().stop();
    }
}
