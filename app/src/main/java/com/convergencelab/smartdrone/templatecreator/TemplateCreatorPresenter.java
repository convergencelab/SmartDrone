package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;


public class TemplateCreatorPresenter implements TemplateCreatorContract.Presenter {

    private final TemplateCreatorDataSource mTemplateCreatorDataSource;

    private final TemplateCreatorContract.View mTemplateCreatorView;

    private static final int NUM_TONES = 14;

    private boolean[] mToneIsActive;
    private Tone[] mTones;


    public TemplateCreatorPresenter(TemplateCreatorDataSource templateCreatorDataSource,
                                    TemplateCreatorContract.View templateCreatorView) {
        mTemplateCreatorDataSource = templateCreatorDataSource;
        mTemplateCreatorView = templateCreatorView;

        mTemplateCreatorView.setPresenter(this);
    }

    @Override
    public void start() {
        mTemplateCreatorDataSource.initialize();
    }

    @Override
    public void toggleToneStatus(int toneDegree) {

    }

    @Override
    public void cancel() {
        mTemplateCreatorView.cancelTemplateCreator();
    }
//
//    @Override
//    public void playTone(Tone toPlay) {
//        mTemplateCreatorDataSource.playTone(toPlay);
//    }
//
//    @Override
//    public void stopTone(Tone toStop) {
//        mTemplateCreatorDataSource.stopTone(toStop);
//    }

    // Todo: Refactor? make template at start of function instead of last condition
    @Override
    public void saveTemplate(String name) {
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
            VoicingTemplate template = new VoicingTemplate(name, new int[]{0}, chordTones);
            mTemplateCreatorDataSource.saveTemplate(template);
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

    private boolean isEmptyTemplate(int[] chordTones) {
        return chordTones.length == 0;
    }
}
