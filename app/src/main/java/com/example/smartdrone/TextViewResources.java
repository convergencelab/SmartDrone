package com.example.smartdrone;

import android.app.Activity;
import android.view.View;
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

    public void incrementVolume() {
        MidiDriverHelper.incrementVolume();
        MidiDriverHelper.sendMidiChord(
                Constants.STOP_NOTE, VoicingsHelper.getCurVoicing(),
                0, KeyFinderHelper.getCurActiveKeyIx());
        MidiDriverHelper.sendMidiChord(
                Constants.START_NOTE, VoicingsHelper.getCurVoicing(),
                MidiDriverHelper.getVolume(), KeyFinderHelper.getCurActiveKeyIx());
        volumeButton.setText("" + MidiDriverHelper.getVolume());
    }
}
