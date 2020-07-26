package com.convergencelabstfx.smartdrone.views;

import android.os.Bundle;
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

import com.convergencelabstfx.smartdrone.databinding.FragmentDroneSettingsBinding;
import com.convergencelabstfx.smartdrone.viewmodels.DroneViewModel;

public class DroneSettingsFragment extends Fragment {

    private DroneViewModel mViewModel;

    private FragmentDroneSettingsBinding mBinding;

    public DroneSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_settings, container, false
        );
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);

        // todo: remove, just a reference on how to use livedata
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newText) {
                // Update the UI, in this case, a TextView.
                mBinding.textView.setText(newText);
            }
        };
        mViewModel.mTestField.observe(getViewLifecycleOwner(), nameObserver);

        return mBinding.getRoot();
    }
}