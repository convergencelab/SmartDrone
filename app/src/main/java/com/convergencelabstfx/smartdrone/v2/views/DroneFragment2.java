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
import com.convergencelabstfx.smartdrone.models.signalprocessor.PitchProcessorObserver;
import com.convergencelabstfx.smartdrone.v2.models.SignalProcessor2;
import com.convergencelabstfx.smartdrone.v2.viewmodels.DroneViewModel;

import org.jetbrains.annotations.NotNull;

public class DroneFragment2 extends Fragment {

    private FragmentDrone2Binding mBinding;

    private DroneViewModel mViewModel;

//    private SignalProcessor2 sp = new SignalProcessor2();
    private SignalProcessor2 sp = new SignalProcessor2();

    private int lastKeyPressed = - 1;

    public DroneFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_2, container, false
        );
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);
        Log.d("testV", mViewModel.testField);


        sp.setActivity(getActivity());
        sp.addPitchListener(new PitchProcessorObserver() {
            @Override
            public void handlePitchResult(int pitch) {
                mBinding.pitchText.setText(Integer.toString(pitch));
            }
        });

        mBinding.randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.start();
            }
        });

        return mBinding.getRoot();
    }

}