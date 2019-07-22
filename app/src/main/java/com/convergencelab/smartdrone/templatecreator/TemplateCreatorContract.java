package com.convergencelab.smartdrone.templatecreator;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;
import com.example.keyfinder.Tone;

public interface TemplateCreatorContract {

    interface View extends BaseView<Presenter> {

        void showEmptyNameError();

        void showIllegalCharacterError();

        void showDuplicateNameError();

        void showEmptyTemplateError();

        void cancelTemplateCreator();

    }

    interface Presenter extends BasePresenter {

        void toggleChordTone(int degree);

        void toggleBassTone(int degree);

        void cancel();

        void saveTemplate(String name);

    }

}
