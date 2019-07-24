package com.convergencelab.smartdrone.models.chords;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public class ChordsImpl implements Chords {
    private HarmonyGenerator mGenerator;



    ChordsImpl() {
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
}
