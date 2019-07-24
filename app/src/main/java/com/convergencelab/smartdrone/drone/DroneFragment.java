package com.convergencelab.smartdrone.drone;

import androidx.fragment.app.Fragment;

import com.example.keyfinder.KeyFinder;

import java.util.Observable;
import java.util.Observer;

public class DroneFragment extends Fragment implements DroneContract.View {
    @Override
    public void showNoteActive(int toShow) {

    }

    @Override
    public void showNoteInactive(int toShow) {

    }

    @Override
    public void showActiveKey(String key, String mode) {

    }

    @Override
    public void showSoundActivity() {

    }

    @Override
    public void showPreferencesActivity() {

    }

    @Override
    public void setPresenter(DroneContract.Presenter presenter) {

    }

    public static DroneFragment newInstance() {
        return new DroneFragment();
    }
}
