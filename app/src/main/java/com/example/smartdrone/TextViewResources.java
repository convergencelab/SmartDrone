package com.example.smartdrone;

import android.app.Activity;
import android.widget.Button;

public class TextViewResources {
    public Button noteTimerButton;
    public Button keyTimerButton;
    public Button noteLengthFilterButton;
    public Button userModeButton;
    public Button volumeButton;

    private Activity activity;

    public TextViewResources(Activity activity) {
        this.activity = activity;
        noteTimerButton = activity.findViewById(R.id.noteTimerButton);
        keyTimerButton = activity.findViewById(R.id.keyTimerButton);
        noteLengthFilterButton = activity.findViewById(R.id.noteFilterButton);
        userModeButton = activity.findViewById(R.id.userModeButton);
        volumeButton = activity.findViewById(R.id.volumeButton);
    }
}
