package com.convergencelab.smartdrone.drone;

import com.convergencelab.smartdrone.BasePresenter;
import com.convergencelab.smartdrone.BaseView;

public interface DroneContract {

    interface View extends BaseView<Presenter> {

        void showNoteActive(int toShow);

        void showNoteInactive(int toShow);

        void showActiveKey(String toShow);

        void showSoundActivity();

        void showPreferencesActivity();

    }

    /* Not much here since user interaction mostly comes from microphone input */
    interface Presenter extends BasePresenter {

        void stop();

        void setActiveKey(int toSet);

        void sustainKey();

    }
}
