package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.VoicingTemplate;

public interface TemplateCreatorDataSource {

    // Initial tones sent for playback.
    VoicingTemplate INIT_TEMP = new VoicingTemplate(
            new int[]{0}, new int[]{0});

    void initialize();

    void playNote();

    void stopNote();

    void saveTemplate();

}
