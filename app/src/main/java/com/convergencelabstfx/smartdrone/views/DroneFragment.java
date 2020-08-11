package com.convergencelabstfx.smartdrone.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.keyfinder.MusicTheory;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.pianoview.PianoTouchListener;
import com.convergencelabstfx.pianoview.PianoView;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDroneBinding;
import com.convergencelabstfx.smartdrone.viewmodels.DroneViewModel;

import java.util.ArrayList;
import java.util.List;

public class DroneFragment extends Fragment {

    private List<VoicingTemplate> mTempList = new ArrayList<>();

    private int ACTIVE_KEY_TEXT_SMALL;

    private int ACTIVE_KEY_TEXT_LARGE;

    private int MICROPHONE_PERMISSION_CODE = 1;

    private FragmentDroneBinding mBinding;

    private DroneViewModel mViewModel;

    // todo: add a clean() method to PianoView
    private Integer mLastPressedKey = -1;

    private int mLastKey = -1;

    public DroneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone, container, false
        );
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);
//        mBinding.activeKeyButton.setText("Start", ACTIVE_KEY_TEXT_LARGE);

        ACTIVE_KEY_TEXT_SMALL = (int) getResources().getDimension(R.dimen.activeKeyText_small);
        ACTIVE_KEY_TEXT_LARGE = (int) getResources().getDimension(R.dimen.activeKeyText_large);

        if (!hasMicrophoneRuntimePermission()) {
            requestMicrophonePermission();
        }

        mBinding.activeKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mViewModel.isRunning()) {
                    mViewModel.startDrone();
                }
                else {
                    mViewModel.stopDrone();
                }
            }
        });

        mBinding.playButton.setOnClickListener(view -> {
            if (!mViewModel.isRunning()) {
                mViewModel.startDrone();
            }
            else {
                mViewModel.stopDrone();
            }
        });

        // todo: do something eventually
        mBinding.lockButton.setOnClickListener(view -> { } );

        mBinding.piano.addPianoTouchListener(new PianoTouchListener() {
            @Override
            public void onKeyDown(@NonNull PianoView piano, int key) { }

            @Override
            public void onKeyUp(@NonNull PianoView piano, int key) { }

            @Override
            public void onKeyClick(@NonNull PianoView piano, int key) {
                if (mViewModel.isRunning()) {
                    mViewModel.setKeyChange(key);
                }
            }
        });

        mViewModel.mDroneIsActive.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mBinding.activeKeyButton.setText("Listening", ACTIVE_KEY_TEXT_SMALL);
                    mBinding.playButton.setIcon(getResources().getDrawable(R.drawable.ic_stop_drone));
                }
                else {
                    mBinding.activeKeyButton.setText("Start", ACTIVE_KEY_TEXT_SMALL);
                    mBinding.playButton.setIcon(getResources().getDrawable(R.drawable.ic_play_drone));
                }
            }
        });

        // todo: see how to set the value of mDetectedNote back to null or something
        mViewModel.mDetectedNote.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                final Integer cappedInteger = integer % 12;
                if (!cappedInteger.equals(mLastPressedKey)) {
                    if (cappedInteger == -1) {
                        mBinding.piano.showKeyNotPressed(mLastPressedKey);
                    }
                    else {
                        mBinding.piano.showKeyPressed(cappedInteger % mBinding.piano.getNumberOfKeys());
                    }
                    mLastPressedKey = cappedInteger;
                }
            }
        });

        mViewModel.mDetectedKey.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.equals(-1)) {
                    mBinding.activeKeyButton.setText("Start", ACTIVE_KEY_TEXT_SMALL);
                }
                else {
                    mBinding.activeKeyButton.setText(MusicTheory.CHROMATIC_SCALE_FLAT[integer], ACTIVE_KEY_TEXT_LARGE);
                }
            }
        });

        return mBinding.getRoot();
    }

    private boolean hasMicrophoneRuntimePermission() {
        return ContextCompat.checkSelfPermission(
                mBinding.getRoot().getContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    // todo: maybe find a way to clean this up; extract hardcoded strings
    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to process pitch.")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

}