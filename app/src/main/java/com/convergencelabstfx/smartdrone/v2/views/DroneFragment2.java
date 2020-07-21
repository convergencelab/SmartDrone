package com.convergencelabstfx.smartdrone.v2.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.pianoview.PianoTouchListener;
import com.convergencelabstfx.pianoview.PianoView;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDrone2Binding;
import com.convergencelabstfx.smartdrone.v2.viewmodels.DroneViewModel;

public class DroneFragment2 extends Fragment {

    private int MICROPHONE_PERMISSION_CODE = 1;

    private FragmentDrone2Binding mBinding;

    private DroneViewModel mViewModel;

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

        if (!hasMicrophoneRuntimePermission()) {
            requestMicrophonePermission();
        }

        mViewModel.getSignalProcessor().addPitchListener(pitch -> {
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



        mBinding.piano.addPianoTouchListener(new PianoTouchListener() {
            @Override
            public void onKeyDown(@NonNull PianoView piano, int key) {

            }

            @Override
            public void onKeyUp(@NonNull PianoView piano, int key) {

            }

            @Override
            public void onKeyClick(@NonNull PianoView piano, int key) {
                mBinding.activeKeyButton.setText(Integer.toString(key), 20);
            }
        });

        return mBinding.getRoot();
    }

    private boolean hasMicrophoneRuntimePermission() {
        return ContextCompat.checkSelfPermission(
                mBinding.getRoot().getContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

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