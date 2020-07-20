package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.convergencelabstfx.smartdrone.R;

public class DroneSettingsFragment2 extends Fragment {

    public DroneSettingsFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drone_settings, container, false);
    }
}