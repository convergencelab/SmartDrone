package com.convergencelabstfx.smartdrone.models.chords;

import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public interface Chords {

    Voicing makeVoicing(VoicingTemplate voicingTemplate,
                        ModeTemplate modeTemplate,
                        int key);

    Note makeNote(Tone tone,
                  ModeTemplate modeTemplate,
                  int key);

    Voicing makeVoicing();

    Note makeNote();

    void setTone(Tone tone);

    void setVoicingTemplate(VoicingTemplate voicingTemplate);

    void setModeTemplate(ModeTemplate modeTemplate);

    void setKey(int keyIx);

}