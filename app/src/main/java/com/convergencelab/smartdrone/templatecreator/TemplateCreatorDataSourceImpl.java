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
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

import java.util.HashSet;

public class TemplateCreatorDataSourceImpl implements TemplateCreatorDataSource {

    // Initial tones sent for playback.
    private VoicingTemplate INIT_TEMP = new VoicingTemplate(
            new int[]{0}, new int[]{0});

//    private static final int NUM_TONES = 14;
//    private static final int MAX_LEN_NAME = 20;
    private static final int DEFAULT_KEY_IX = 0;

//    private static final int VALID = 0;
//    private static final int DUPLICATE_NAME = 1;
//    private static final int ILLEGAL_CHARACTERS = 2;
//    private static final int INVALID_LENGTH = 3;

    private MidiDriverModel mMidiDriverModel;
    private KeyFinderModel mKeyFinderModel;
    private HarmonyGenerator mHarmonyGenerator;
    private SharedPreferences mPreferences;

    private final HashSet<String> nameSet;

    private AbstractKey mCurKey;
    private ModeTemplate mCurMode;

//    private boolean[] mToneIsActive;
//    private Tone[] mTones;

    // Todo: Make private and create method getInstance()
    public TemplateCreatorDataSourceImpl (SharedPreferences preferences) {
        mMidiDriverModel = new MidiDriverModel();
        mKeyFinderModel = new KeyFinderModel();
        mHarmonyGenerator = new HarmonyGenerator();

        mPreferences = preferences;
//        mToneIsActive = new boolean[NUM_TONES]; // Todo: Will have to make it work for bass notes as well
//        mToneIsActive[0] = true; // Todo: clean up,
        nameSet = VoicingHelper
                .getSetOfAllTemplateNames(DronePreferences.getAllTemplatePref(mPreferences));
//        initializeTones();
    }

    @Override
    public void initialize() {
        loadKeyFinderData();
        Voicing initVoicing = mHarmonyGenerator.generateVoicing(INIT_TEMP, mCurMode, mCurKey);
        mMidiDriverModel.sendMidiSetup();
        mMidiDriverModel.playVoicing(initVoicing);
    }

//    @Override
//    public void toggleTonePlayback(int toneIx) {
//        if (mToneIsActive[toneIx]) {
//            mToneIsActive[toneIx] = false;
//            stopTone(toneIx);
//        }
//        else {
//            mToneIsActive[toneIx] = true;
//            playTone(toneIx);
//        }
//    }


    @Override
    public void saveTemplate(VoicingTemplate template) {
        String flattenedTemplate = VoicingHelper.flattenTemplate(template);
        VoicingHelper.addTemplateToPref(mPreferences, template);

//        // Todo write this method.
//        boolean toneFound = false;
//        // make string for flattened template
//        // add name (parameter)
////        String flattenedTemplate = name; // don't think the extra var is necessary
//
//        // Validate name
////        int nameStatus = validateName(name);
////        if (nameStatus != VALID) {
////
////        }
//
//        // add each tone to string
//        String bassTonesStr = "{0}"; // Todo: Doesn't have bass options yet; default {0}
//
//        String templateTones = "";
//        templateTones += '{';
//        for (int i = 0; i < mToneIsActive.length; i++) {
//            if (mToneIsActive[i]) {
//                if (!toneFound) {
//                    templateTones += Integer.toString(i);
//                    toneFound = true;
//                }
//                else {
//                    templateTones += ',' + Integer.toString(i);
//                }
//            }
//        }
//        templateTones += '}';
//
//        // No tones entered.
//        if (templateTones.length() == 2) {
//            return false;
//        }
//
//
//        // save newly made template as cur template.
//
//
//
//        return false; // Todo fix this when time comes
    }

    @Override
    public void playTone(Tone toPlay) {
        mMidiDriverModel.addNoteToPlayback(convertToneToNote(toPlay));

    }

    @Override
    public void stopTone(Tone toStop) {
        mMidiDriverModel.removeNoteFromPlayback(convertToneToNote(toStop));
    }

//    @Override
//    public String validateName(String name) {
//        if (checkDuplicate(name)) {
//            return "Name '" + name + "' already taken. Please choose another name";
//        }
//        if (checkContainsIllegalCharacters(name)) {
//            return "Name cannot contain characters '{', '}' or '|'.";
//        }
//        if (checkLength(name)) {
//            return "Name must contain at least one character";
//        }
//        return null;
//    }

    @Override
    public boolean isDuplicateName(String name) {
        return nameSet.contains(name);
    }

//    private boolean checkContainsIllegalCharacters(String name) {
//        return name.contains("{") || name.contains("}") || name.contains("|");
//    }

//    private boolean checkLength(String name) {
//        return 0 == name.length();
//    }

    // Todo: only chord tones for now.

//    /**
//     * Constructs tone list with object for each tone.
//     */
//    // Todo: move to activity
//    private void initializeTones() {
//        for (int i = 0; i < NUM_TONES; i++) {
//            mTones[i] = new Tone(i, Tone.TONE_CHORD);
//        }
//    }

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


//    private void playTone(int toPlay) {
//        mMidiDriverModel.addNoteToPlayback(convertToneToNote(mTones[toPlay]));
//    }
//
//    private void stopTone(int toStop) {
//        mMidiDriverModel.removeNoteFromPlayback(convertToneToNote(mTones[toStop]));
//    }
}
