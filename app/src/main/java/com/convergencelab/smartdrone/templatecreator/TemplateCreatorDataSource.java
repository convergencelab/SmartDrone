package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

public interface TemplateCreatorDataSource {

    void initialize();

//    void toggleTonePlayback(int toneIx);

//    String validateName(String name);

    boolean isDuplicateName(String name);

    void saveTemplate(VoicingTemplate template);

    void playTone(Tone toPlay);

    void stopTone(Tone toStop);

    void endPlayback();
}
