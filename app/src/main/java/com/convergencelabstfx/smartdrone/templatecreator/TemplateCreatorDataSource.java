package com.convergencelabstfx.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

public interface TemplateCreatorDataSource {

    void initialize();

    boolean isDuplicateName(String name);

    void saveTemplate(VoicingTemplate template);

    void playTone(Tone toPlay);

    void stopTone(Tone toStop);

    void endPlayback();
}
