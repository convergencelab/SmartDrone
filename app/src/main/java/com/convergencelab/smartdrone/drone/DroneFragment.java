package com.convergencelab.smartdrone.drone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.convergencelab.smartdrone.Constants;
import com.convergencelab.smartdrone.DroneActivity;
import com.convergencelab.smartdrone.DroneSettingsActivity;
import com.convergencelab.smartdrone.DroneSoundActivity;
import com.convergencelab.smartdrone.R;

import java.util.HashMap;

public class DroneFragment extends Fragment implements DroneContract.View {

    // Todo: Improve architecture:
    //       Permissions will live here for now.
    private int MICROPHONE_PERMISSION_CODE = 1;

    /**
     * Map note name to piano image file name.
     * Key: Note Name.
     * Value: Name of piano image file.
     */
    private HashMap<Integer, String> mPianoImageName;

    private View mRoot;

    /**
     * Button for toggling state of drone.
     */
    private ImageButton mDroneToggleButton;

    // Todo: just the way it is right now
    private int mCurDroneState = 0;

    /**
     * Image of piano on drone main screen.
     */
    private ImageView mPianoImg;

    /**
     * Button that displays active key.
     * Click function will sustain playback of the active drone. //todo make it so this comment isn't a lie
     */
    private Button mActiveKeyButton;


    private DroneContract.Presenter mPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.template_creator_frag, container, false);

        if (ContextCompat.checkSelfPermission(mRoot.getContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicrophonePermission();
        }

        // Draw piano null
        mPianoImg = mRoot.findViewById(R.id.piano_img);
        mPianoImg.setImageResource(R.drawable.piano_null);

        // Drone state button
        mDroneToggleButton = mRoot.findViewById(R.id.drone_control_button);
        mDroneToggleButton.setOnClickListener(v -> {
            mPresenter.toggleDroneState();
            if (ContextCompat.checkSelfPermission(mRoot.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestMicrophonePermission();
            }
            else {
                // Todo: Better solution? It is what it is for now.
                switch (mCurDroneState) {
                    case 0:
                        mCurDroneState = 1;
                        mDroneToggleButton.setImageResource(R.drawable.ic_stop_drone);
                        break;
                    case 1:
                        mCurDroneState = 0;
                        mDroneToggleButton.setImageResource(R.drawable.ic_play_drone);
                        break;
                }
            }
        });

        // Drone sound settings
        mRoot.findViewById(R.id.drone_control_button).setOnClickListener(v -> showSoundActivity());

        // Drone preferences button
        mRoot.findViewById(R.id.drone_preferences_button).setOnClickListener(v -> showPreferencesActivity());

        return mRoot;
    }

    @Override
    public void showNoteActive(int toShow) {
        if (toShow == -1) {
            mPianoImg.setImageResource(R.drawable.piano_null);
        }
        else {
            int resID = getResources().getIdentifier(
                    mPianoImageName.get(toShow), "drawable", mRoot.getContext().getPackageName());
            mPianoImg.setImageResource(resID);
        }
    }

    @Override
    public void showNullPitch() {
        mPianoImg.setImageResource(R.drawable.piano_null);
    }

    @Override
    public void showActiveKey(String key, String mode) {
        mActiveKeyButton.setText(key + '\n' + mode);
    }

    @Override
    public void showSoundActivity() {
        mPresenter.handleActivityChange();
        Intent intent = new Intent(getContext(), DroneSoundActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPreferencesActivity() {
        mPresenter.handleActivityChange();
        Intent intent = new Intent(getContext(), DroneSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(DroneContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static DroneFragment newInstance() {
        return new DroneFragment();
    }

    // Todo: she ain't pretty
    /**
     * Builds hash map for (note name -> piano image file name).
     */
    public void inflatePianoMap() {
        String str;
        for (int i = 0; i < 12; i++) {
            str = "piano_";
            str += Character.toLowerCase(Constants.NOTES_SHARP[i].charAt(0));
            if (Constants.NOTES_SHARP[i].length() == 2) {
                str += "_sharp";
            }
            mPianoImageName.put(i, str);
        }
    }

    public void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed for drone.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
