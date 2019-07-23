package com.convergencelab.smartdrone.models.voicing;

import com.example.keyfinder.AbstractKey;
import com.example.keyfinder.ModeTemplate;
import com.example.keyfinder.Note;
import com.example.keyfinder.Tone;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

public interface VoicingInterface {

    Voicing makeVoicing(VoicingTemplate voicingTemplate,
                        ModeTemplate modeTemplate,
                        AbstractKey key);

    Note makeNote(Tone tone,
                  ModeTemplate modeTemplate,
                  AbstractKey key);

}
