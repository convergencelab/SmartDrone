package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;

import org.jetbrains.annotations.NotNull;

public class DroneFragment2 extends Fragment {

    public DroneFragment2() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDrone2Binding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_2, container, false
        );
        PianoView piano = new PianoView(getContext());
        binding.container.addView(piano);
        return binding.getRoot();
    }
}