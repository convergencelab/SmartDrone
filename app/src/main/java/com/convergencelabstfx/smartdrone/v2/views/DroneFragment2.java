package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;
import com.convergencelabstfx.smartdrone.v2.viewmodels.DroneViewModel;

import org.jetbrains.annotations.NotNull;

public class DroneFragment2 extends Fragment {

    private FragmentDrone2Binding binding;

    private DroneViewModel mViewModel;

    private int lastKeyPressed = - 1;

    public DroneFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_2, container, false
        );
//        addPiano();
        binding.piano.addPianoTouchListener(new PianoTouchListener() {
            @Override
            public void onPianoTouch(PianoView piano, int key) {
//                Log.d("testV", "" + key);
                // Piano key change
                if (key != lastKeyPressed) {
                    if (lastKeyPressed != -1) {
                        piano.showKeyNotPressed(lastKeyPressed);
                    }
                    if (key != -1) {
                        piano.showKeyPressed(key);
                    }
                    lastKeyPressed = key;
                }

            }

            @Override
            public void onPianoClick(PianoView piano, int key) {
//                if (piano.keyIsPressed(key)) {
//                    piano.showKeyNotPressed(key);
//                }
//                else {
//                    piano.showKeyPressed(key);
//                }
            }
        });
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);
        Log.d("testV", mViewModel.testField);
        return binding.getRoot();
    }

    private void pianoClick(int key) {
        Log.d("test", "" + key);
    }
//
//    private void addPiano() {
//        PianoView piano = new PianoView(getContext());
//        for (int i = 0; i < 12; i++) {
//            final int ix = i;
//            piano.getPianoKeyAt(ix).setOnClickListener(view -> {
//                if (!piano.keyIsPressed(ix)) {
//                    piano.showKeyPressed(ix);
//                } else {
//                    piano.showKeyNotPressed(ix);
//                }
//                pianoClick(ix);
//            });
//        }
//        binding.container.addView(piano);
//    }

}