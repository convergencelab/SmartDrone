package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.VoicingTemplate;

public interface TemplateCreatorDataSource {

    void initialize();

    void toggleTonePlayback(int toneIx);

    boolean saveTemplate(String name);

}
