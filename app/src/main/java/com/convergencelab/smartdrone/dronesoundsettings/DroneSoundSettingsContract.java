package com.convergencelab.smartdrone.dronesoundsettings;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;

public class DroneSoundSettingsContract {

    interface View extends BaseView<Presenter> {

        void showParentScale(String name);

        void showMode(String name);

        void showPlugin(String name);

        void showTemplateActive();

        void showPlaybackUnmuted();

        void showPlaybackMuted();

        void showTemplateCreatorActivity();

        void showDroneMainActivity();

    }

    interface Presenter extends BasePresenter {

        void togglePlayback();

        void nextParentScale();

        void nextMode();

        void nextPlugin();

        void selectTemplate(int templateIx);

        void finish();

    }

}
