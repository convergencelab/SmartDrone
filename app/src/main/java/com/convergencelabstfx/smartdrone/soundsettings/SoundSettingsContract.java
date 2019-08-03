package com.convergencelabstfx.smartdrone.soundsettings;

import com.convergencelabstfx.smartdrone.BasePresenter;
import com.convergencelabstfx.smartdrone.BaseView;
import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;

public class SoundSettingsContract {

    interface View extends BaseView<Presenter> {

        void showParentScale(String name);

        void showMode(String name);

        void showPlugin(String name);

        void showTemplateActive(int templateIx);

        void showPlaybackUnmuted();

        void showPlaybackMuted();

        void showTemplateCreatorActivity();

        void showDroneMainActivity();

        void refreshTemplates();

    }

    interface Presenter extends BasePresenter {

        void togglePlayback();

        void nextParentScale();

        void nextMode();

        void nextPlugin();

        void selectTemplate(int templateIx);

        ArrayList<VoicingTemplate> getAllTemplates();

        void finish();

        void getCurrentTemplate();

        void deleteTemplate(int templateIx);

    }

}
