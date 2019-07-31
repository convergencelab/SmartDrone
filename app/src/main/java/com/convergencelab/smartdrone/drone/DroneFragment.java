package com.convergencelab.smartdrone.drone;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.convergencelab.smartdrone.Constants;
import com.convergencelab.smartdrone.DroneSettingsActivity;
import com.convergencelab.smartdrone.DroneSoundActivity;
import com.convergencelab.smartdrone.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class DroneFragment extends Fragment implements DroneContract.View {

    /* Used for animating */

    private static final int DURATION_MEDIUM = 200;

    private static final int DURATION_SHORT = 100;

    private static final float POSITION_NORMAL = 0f;

    private static final float POSITION_ROTATED = 90f;

    private static final float INVISIBLE = 0f;

    private static final float VISIBLE = 1f;

    private static final int TEXT_SIZE_SMALL = 20;

    private static final int TEXT_SIZE_MEDIUM = 28;

    private Animation mFadeIn;

    private Animation mFadeOut;

    private ViewGroup mActiveKeyLayout;

    // Todo: Improve architecture:
    //       Permissions will live here for now.
    private int MICROPHONE_PERMISSION_CODE = 1;

    /**
     * Map note name to piano image file name.
     * Key: Note Name.
     * Value: Name of piano image file.
     */
    private HashMap<Integer, String> mPianoImageName;

    /**
     * Root view.
     */
    private View mRoot;

    /**
     * Button for toggling state of drone.
     */
    private FloatingActionButton mDroneToggleButton;

    /**
     * Image of piano on drone main screen.
     */
    private ImageView mPianoImg;

    /**
     * Button that displays active key.
     * Click function will sustain playback of the active drone. //todo make it so this comment isn't a lie
     */
    private Button mActiveKeyButton;

    /**
     * The ring drawable that outlines the active key.
     */
    TransitionDrawable mActiveKeyBackground;

    private DroneContract.Presenter mPresenter;
    private TextView mActiveKeyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.drone_frag, container, false);

        if (ContextCompat.checkSelfPermission(mRoot.getContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicrophonePermission();
        }

        // Setup Piano Image map
        mPianoImageName = new HashMap<>();
        inflatePianoMap();

        setupView();
        setupTextAnimators();

        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    @Override
    public void showDroneActive() {
        mActiveKeyBackground.startTransition(DURATION_MEDIUM);
        animateActiveKeyText("Listening");

        showControlButtonActive();
    }

    @Override
    public void showDroneInactive() {
        mActiveKeyBackground.reverseTransition(DURATION_MEDIUM);
        mPianoImg.setImageResource(R.drawable.piano_null);
        animateActiveKeyText("Start", TEXT_SIZE_MEDIUM);
        showControlButtonInactive();
    }

    @Override
    public void showNoteActive(int toShow) {
        if (toShow == -1) {
            mPianoImg.setImageResource(R.drawable.piano_null);
        }
        else {
            String piano_text = mPianoImageName.get(toShow);
            int resID = getResources().getIdentifier(piano_text,
                    "drawable",
                    mRoot.getContext().getPackageName());
            mPianoImg.setImageResource(resID);
        }
    }

    @Override
    public void showActiveKey(String key, String mode) {
        animateActiveKeyText(key + '\n' + mode, TEXT_SIZE_SMALL);
    }

    @Override
    public void showSoundActivity() {
        mPresenter.stop();
        Intent intent = new Intent(getContext(), DroneSoundActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPreferencesActivity() {
        mPresenter.stop();
        Intent intent = new Intent(getContext(), DroneSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(DroneContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Return instance of fragment.
     * @return new instance.
     */
    public static DroneFragment newInstance() {
        return new DroneFragment();
    }

    /**
     * Sets up view for fragment.
     */
    private void setupView() {
        // Used for animating text
        mActiveKeyLayout = mRoot.findViewById(R.id.active_key_layout);

        // Draw piano null
        mPianoImg = mRoot.findViewById(R.id.piano_img);
        mPianoImg.setImageResource(R.drawable.piano_null);

        // Drone play/stop button
        mDroneToggleButton = mRoot.findViewById(R.id.drone_control_button);
        mDroneToggleButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(mRoot.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestMicrophonePermission();
            }
            else {
                mPresenter.toggleDroneState();
            }
        });

        // Drone sound settings
        mRoot.findViewById(R.id.drone_sound_button).setOnClickListener(v -> {
            mPresenter.stop();
            showSoundActivity();

        });

        // Drone preferences
        mRoot.findViewById(R.id.drone_preferences_button).setOnClickListener(v -> {
            mPresenter.stop();
            showPreferencesActivity();
        });

        // Active key button
        mActiveKeyButton = mRoot.findViewById(R.id.active_key_button);
        mActiveKeyButton.setOnClickListener(v -> mPresenter.handleActiveKeyButtonClick());
        mActiveKeyBackground = (TransitionDrawable) mActiveKeyButton.getBackground();

        mActiveKeyText = mRoot.findViewById(R.id.active_key_text);
    }

    /**
     * Sets up variables for text fading animation.
     */
    private void setupTextAnimators() {
        mFadeIn = new AlphaAnimation(INVISIBLE, VISIBLE);
        mFadeIn.setDuration(DURATION_MEDIUM);

        mFadeOut = new AlphaAnimation(VISIBLE, INVISIBLE);
        mFadeOut.setDuration(DURATION_MEDIUM);
    }

    /**
     * Animates active key text to fade mFadeOut and mFadeIn.
     * @param newText new text to show mFadeIn active key button.
     */
    private void animateActiveKeyText(String newText) {
        animateActiveKeyText(newText, -1);
    }

    /**
     * Animates active key text to fade mFadeOut and mFadeIn.
     * @param newText new text to show mFadeIn active key button.
     */
    private void animateActiveKeyText(String newText, int textSize) {
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { /* Not used. */ }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (textSize != -1) {
                    mActiveKeyText.setTextSize(textSize);
                }
                mActiveKeyText.setText(newText);
                mActiveKeyText.startAnimation(mFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { /* Not used. */ }
        });
        mActiveKeyText.startAnimation(mFadeOut);
    }

    /**
     * Animates control button, ends mFadeIn active state.
     */
    private void showControlButtonActive() {
        ObjectAnimator.ofFloat(mDroneToggleButton, "rotation", POSITION_NORMAL, POSITION_ROTATED).setDuration(DURATION_MEDIUM).start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> mDroneToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_drone)), DURATION_SHORT);
    }

    /**
     * Animates control button, ends mFadeIn inactive state.
     */
    private void showControlButtonInactive() {
        ObjectAnimator.ofFloat(mDroneToggleButton, "rotation", POSITION_ROTATED, POSITION_NORMAL).setDuration(DURATION_MEDIUM).start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> mDroneToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_drone)), DURATION_SHORT);
    }

    // Todo: she ain't pretty
    /**
     * Builds hash map for (note name -> piano image file name).
     */
    private void inflatePianoMap() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
