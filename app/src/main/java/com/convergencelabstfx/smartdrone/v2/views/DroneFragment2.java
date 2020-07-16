package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.pianoview.PianoTouchListener;
import com.convergencelabstfx.pianoview.PianoView;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;
import com.convergencelabstfx.smartdrone.v2.viewmodels.DroneViewModel;

import org.jetbrains.annotations.NotNull;

public class DroneFragment2 extends Fragment {

    private FragmentDrone2Binding mBinding;

    private DroneViewModel mViewModel;

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
        return mBinding.getRoot();
    }

}