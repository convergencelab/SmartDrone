package com.convergencelabstfx.smartdrone.drone;

import com.convergencelabstfx.smartdrone.BasePresenter;
import com.convergencelabstfx.smartdrone.BaseView;

public interface DroneContract {

    interface View extends BaseView<Presenter> {

        void showDroneActive();

        void showDroneInactive();

        void showNoteActive(int toShow);

        void showActiveKey(String key, String mode);

        void showSoundActivity();

        void showPreferencesActivity();

        void showDroneLocked();

        void showDroneUnlocked();

    }

    /* Not much here since user interaction mostly comes from microphone input */
    interface Presenter extends BasePresenter {

        void toggleDroneState();

        void handleActiveKeyButtonClick();

        void stop();

        void setActiveKey(int toSet);

    }
}
