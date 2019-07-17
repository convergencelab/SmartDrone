package com.convergencelab.smartdrone.templatecreator;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;
import com.example.keyfinder.Tone;

import java.util.List;

public interface TemplateCreatorContract {

    interface View extends BaseView<Presenter> {

        void showEmptyNameError();

        void showIllegalCharacterError();

        void showDuplicateNameError();

        void showEmptyTemplateError();

        void showDroneSoundSettings();

    }

    interface Presenter extends BasePresenter {

        void cancel();

        // These will be on click listeners
        void playTone(Tone toPlay);

        // These will be on click listeners
        void stopTone(Tone toStop);

        void saveTemplate(String name, List<Tone> chordTones);

        // Add error check methods, will be called when saveTemplate is called.

    }

}
