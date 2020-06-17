package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;

import org.jetbrains.annotations.NotNull;

public class DroneFragment2 extends Fragment {

    private FragmentDrone2Binding binding;

    public DroneFragment2() {
        // Required empty public constructor
    }
    
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_2, container, false
        );
        PianoView piano = new PianoView(getContext());
        for (int i = 0; i < 12; i++) {
            final int ix = i;
            piano.getPianoKeyAt(ix).setOnClickListener(view -> {
                if (!piano.keyIsPressed(ix)) {
                    piano.showKeyPressed(ix);
                }
                else {
                    piano.showKeyNotPressed(ix);
                }
                pianoClick(ix);
            });
        }
        binding.container.addView(piano);
        return binding.getRoot();
    }

    private void pianoClick(int key) {
        Log.d("test", "" + key);
    }

}