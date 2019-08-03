package com.convergencelabstfx.smartdrone.models.chords;

import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public class ChordsImpl implements Chords {
    private HarmonyGenerator mGenerator;
    private int mKeyIx;
    private ModeTemplate mModeTemplate;
    private VoicingTemplate mVoicingTemplate;
    private Tone mTone;

    public ChordsImpl() {
        mGenerator = new HarmonyGenerator();
    }

    @Override
    public Voicing makeVoicing(VoicingTemplate voicingTemplate,
                               ModeTemplate modeTemplate,
                               int keyIx) {
        return mGenerator.generateVoicing(voicingTemplate, modeTemplate, keyIx);
    }

    @Override
    public Note makeNote(Tone tone,
                         ModeTemplate modeTemplate,
                         int keyIx) {
        return mGenerator.generateNote(tone, modeTemplate, keyIx);
    }

    @Override
    public Voicing makeVoicing() {
        return mGenerator.generateVoicing(mVoicingTemplate, mModeTemplate, mKeyIx);
    }

    @Override
    public Note makeNote() {
        return mGenerator.generateNote(mTone, mModeTemplate, mKeyIx);
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
    public void setKey(int keyIx) {
        mKeyIx = keyIx;
    }
}
