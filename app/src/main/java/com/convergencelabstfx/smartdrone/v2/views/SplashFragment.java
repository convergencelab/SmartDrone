package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentSplashBinding;

import org.jetbrains.annotations.NotNull;

public class SplashFragment extends Fragment {

    public SplashFragment() {
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSplashBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_splash, container, false
        );

        binding.splashImg.setImageResource(R.drawable.convergence_lab_1080);

        return binding.getRoot();
    }
}
