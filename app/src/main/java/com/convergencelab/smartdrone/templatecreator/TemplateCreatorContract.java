package com.convergencelab.smartdrone.templatecreator;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;
import com.example.keyfinder.Note;

public interface TemplateCreatorContract {

    interface View extends BaseView<Presenter> {

        void showNoteActive(Note toShow);

        void showNoteInactive(Note toShow);

    }

    interface Presenter extends BasePresenter {

        void start();

        void toggleNoteStatus(Note toToggle);

        void saveTemplate();

        void cancel();

    }

}
