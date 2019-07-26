package com.convergencelab.smartdrone.models.chords;

import android.util.Log;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public class ChordsImpl implements Chords {
    private HarmonyGenerator mGenerator;
    private AbstractKey mKey;
    private ModeTemplate mModeTemplate;
    private VoicingTemplate mVoicingTemplate;
    private Tone mTone;

    public ChordsImpl() {
        mGenerator = new HarmonyGenerator();
    }

    @Override
    public Voicing makeVoicing(VoicingTemplate voicingTemplate,
                               ModeTemplate modeTemplate,
                               AbstractKey key) {
        return mGenerator.generateVoicing(voicingTemplate, modeTemplate, key);
    }

    @Override
    public Note makeNote(Tone tone,
                         ModeTemplate modeTemplate,
                         AbstractKey key) {
        return mGenerator.generateNote(tone, modeTemplate, key);
    }

    @Override
    public Voicing makeVoicing() {
        Log.d("debug", "makeVoicing");
        return mGenerator.generateVoicing(mVoicingTemplate, mModeTemplate, mKey);
    }

    @Override
    public Note makeNote() {
        return mGenerator.generateNote(mTone, mModeTemplate, mKey);
    }

    @Override
    public void setTone(Tone tone) {
        mTone = tone;
    }

    @Override
    public void setVoicingTemplate(VoicingTemplate voicingTemplate) {
        mVoicingTemplate = voicingTemplate;
    }

    @Override
    public void setModeTemplate(ModeTemplate modeTemplate) {
        mModeTemplate = modeTemplate;
    }

    @Override
    public void setKey(AbstractKey key) {
        mKey = key;
    }
}
