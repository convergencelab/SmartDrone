package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

import java.util.List;

public class TemplateCreatorPresenter implements TemplateCreatorContract.Presenter {

    private final TemplateCreatorDataSource mTemplateCreatorDataSource;

    private final TemplateCreatorContract.View mTemplateCreatorView;


    public TemplateCreatorPresenter(TemplateCreatorDataSource templateCreatorDataSource,
                                    TemplateCreatorContract.View templateCreatorView) {
        mTemplateCreatorDataSource = templateCreatorDataSource;
        mTemplateCreatorView = templateCreatorView;
    }
    // Constructor
        // model
        // view

        // view.setPresenter(this)

    @Override
    public void start() {
        // Not sure yet
    }

    @Override
    public void cancel() {
        mTemplateCreatorView.showDroneSoundSettings();
    }

    @Override
    public void playTone(Tone toPlay) {
        mTemplateCreatorDataSource.playTone(toPlay);
    }

    @Override
    public void stopTone(Tone toStop) {
        mTemplateCreatorDataSource.stopTone(toStop);
    }

    @Override
    public void saveTemplate(String name, List<int> chordTones) {
        if (isDuplicateName(name)) {
            mTemplateCreatorView.showDuplicateNameError();
        }
        else if (isEmptyName(name)) {
            mTemplateCreatorView.showEmptyNameError();
        }
        else if (isEmptyTemplate(chordTones)) {
            mTemplateCreatorView.showEmptyTemplateError();
        }
        else if (containsIllegalCharacter(name)) {
            mTemplateCreatorView.showIllegalCharacterError();
        }
        else {
            VoicingTemplate template = new VoicingTemplate(name, new int[]{0}, chordTones.toArray());
            mTemplateCreatorDataSource.saveTemplate();
        }
    }

    private boolean containsIllegalCharacter(String name) {
        return name.contains("{") || name.contains("}") || name.contains("|");
    }

    private boolean isDuplicateName(String name) {
        return mTemplateCreatorDataSource.isDuplicateName(name);
    }

    private boolean isEmptyName(String name) {
        return name.length() == 0;
    }

    private boolean isEmptyTemplate(List<Tone> chordTones) {
        return chordTones.size() == 0;
    }
}
