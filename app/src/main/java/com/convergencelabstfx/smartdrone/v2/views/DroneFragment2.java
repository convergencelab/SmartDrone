package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;
import com.convergencelabstfx.smartdrone.v2.models.SignalProcessorKt;
import com.convergencelabstfx.smartdrone.v2.viewmodels.DroneViewModel;

public class DroneFragment2 extends Fragment {

    private FragmentDrone2Binding mBinding;

    private DroneViewModel mViewModel;

    private SignalProcessorKt sp = new SignalProcessorKt();

    private int mLastKey = -1;

    public DroneFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_2, container, false
        );
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);

        // todo: remove, just a reference on how to use livedata
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newText) {
                // Update the UI, in this case, a TextView.
                mBinding.pitchText.setText(newText);
            }
        };
        mViewModel.mTestField.observe(getViewLifecycleOwner(), nameObserver);


        sp.addPitchListener(pitch -> {
                    Log.d("testV", Integer.toString(pitch));
                    if (pitch != mLastKey) {
                        if (pitch == -1) {
                            mBinding.piano.showKeyNotPressed(mLastKey % 12);
                        }
                        else {
                            mBinding.piano.showKeyPressed(pitch % 12);
                        }
                        mLastKey = pitch;
                    }
                }
        );

        mBinding.randomButton.setOnClickListener(view -> {
            if (sp.isRunning()) {
                sp.stop();
            }
            else {
                sp.start();
            }
        });

        return mBinding.getRoot();
    }

}