package com.convergencelabstfx.smartdrone.templatecreator;

import com.convergencelabstfx.smartdrone.BasePresenter;
import com.convergencelabstfx.smartdrone.BaseView;

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
