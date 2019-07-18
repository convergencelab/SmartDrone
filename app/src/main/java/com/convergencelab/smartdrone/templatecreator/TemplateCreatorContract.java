package com.convergencelab.smartdrone.templatecreator;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;
import com.example.keyfinder.Tone;

public interface TemplateCreatorContract {

    interface View extends BaseView<Presenter> {

        void showToneActive(Tone toShow);

        void showToneInactive(Tone toShow);

        void showBassTonesActive(int toShow);

        void showBassTonesInactive(int toShow);

        void showEmptyNameError();

        void showIllegalCharacterError();

        void showDuplicateNameError();

        void showEmptyTemplateError();

        void cancelTemplateCreator();

    }

    interface Presenter extends BasePresenter {

        void toggleToneStatus(int toneDegree, int toneType);

        void selectBassTones(int ix);

        void cancel();

        void saveTemplate(String name);

    }

}
