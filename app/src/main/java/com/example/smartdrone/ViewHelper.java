package com.example.smartdrone;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class ViewHelper {

    public Button noteTimerButton;
    public Button keyTimerButton;
    public Button noteLengthFilterButton;
    public Button userModeButton;
    public Button volumeButton;

    private Activity activity;

    public ViewHelper(Activity activity) {
        this.activity = activity;
        noteTimerButton = activity.findViewById(R.id.noteTimerButton);
        keyTimerButton = activity.findViewById(R.id.keyTimerButton);
        noteLengthFilterButton = activity.findViewById(R.id.noteFilterButton);
        userModeButton = activity.findViewById(R.id.userModeButton);
        volumeButton = activity.findViewById(R.id.volumeButton);
    }

    public void incrementNoteExpiration(View view) {
        KeyFinderHelper.setNoteTimerLength((KeyFinderHelper.getNoteTimerLength() % 5) + 1);
        KeyFinderHelper.getKeyFinder().setNoteTimerLength(KeyFinderHelper.getNoteTimerLength());
        noteTimerButton.setText("" + KeyFinderHelper.getNoteTimerLength());
    }
}
