package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;

import com.convergencelab.smartdrone.Models.KeyFinderModel;
import com.convergencelab.smartdrone.Models.MidiDriverModel;
import com.convergencelab.smartdrone.Utility.DronePreferences;
import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public class TemplateCreatorDataSourceImpl implements TemplateCreatorDataSource {

    // Initial tones sent for playback.
    private VoicingTemplate INIT_TEMP = new VoicingTemplate(
            new int[]{0}, new int[]{0});
    private static final int NUM_TONES = 14;
    private static final int DEFAULT_KEY_IX = 0;

    private MidiDriverModel mMidiDriverModel;
    private KeyFinderModel mKeyFinderModel;
    private HarmonyGenerator mHarmonyGenerator;
    private SharedPreferences mPreferences;

    private AbstractKey mCurKey;
    private ModeTemplate mCurMode;

    private boolean[] mToneIsActive;
    private Tone[] mTones;

    // Todo: Make private and create method getInstance()
    public TemplateCreatorDataSourceImpl (SharedPreferences preferences) {
        mMidiDriverModel = new MidiDriverModel();
        mKeyFinderModel = new KeyFinderModel();
        mHarmonyGenerator = new HarmonyGenerator();

        mPreferences = preferences;
        mToneIsActive = new boolean[NUM_TONES]; // Todo: Will have to make it work for bass notes as well
        mToneIsActive[0] = true; // Todo: clean up,
        initializeTones();
    }

    @Override
    public void initialize() {
        loadKeyFinderData();
        Voicing initVoicing = mHarmonyGenerator.generateVoicing(INIT_TEMP, mCurMode, mCurKey);
        mMidiDriverModel.sendMidiSetup();
        mMidiDriverModel.playVoicing(initVoicing);
    }

    @Override
    public void toggleTonePlayback(int toneIx) {
        if (mToneIsActive[toneIx]) {
            mToneIsActive[toneIx] = false;
            stopTone(toneIx);
        }
        else {
            mToneIsActive[toneIx] = true;
            playTone(toneIx);
        }
    }

    @Override
    public void saveTemplate() {
        // Todo write this method.
        // should save newly made template as cur template.
    }

    // Todo: only chord tones for now.
    private void initializeTones() {
        for (int i = 0; i < NUM_TONES; i++) {
            mTones[i] = new Tone(i, Tone.TONE_CHORD);
        }
    }

    private void loadKeyFinderData() {
        int modeIx = DronePreferences.getStoredModePref(mPreferences);
        int keyIx = DEFAULT_KEY_IX; // C

        mCurKey = mKeyFinderModel.getKeyFinder().getKeyAtIndex(keyIx);
        mCurMode = mKeyFinderModel.getKeyFinder().getModeTemplate(modeIx);
    }

    private Note convertToneToNote(Tone toConvert) {
        return mHarmonyGenerator.generateNote(toConvert, mCurMode, mCurKey);
    }

    private void playTone(int toPlay) {
        mMidiDriverModel.addNoteToPlayback(convertToneToNote(mTones[toPlay]));
    }

    private void stopTone(int toStop) {
        mMidiDriverModel.removeNoteFromPlayback(convertToneToNote(mTones[toStop]));
    }
}
