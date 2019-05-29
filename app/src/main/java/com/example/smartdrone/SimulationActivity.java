/*
 * @SOURCES:
 * Midi Driver         : https://github.com/billthefarmer/mididriver
 * Signal Processing   : https://github.com/JorenSix/TarsosDSP
 * TarsosDSP Example   : https://stackoverflow.com/questions/31231813/tarsosdsp-pitch-analysis-for-dummies
 */

package com.example.smartdrone;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.Locale;

public class SimulationActivity extends AppCompatActivity
    implements MidiDriver.OnMidiStartListener {

    public static final String MESSAGE_LOG =
            "Simulation";

    public static KeyFinder keyFinder = new KeyFinder();

    TextView text;
    int prevActiveKeyIx;
    int curActiveKeyIx;

    public MidiDriver midi;
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        // keyFinder.setNoteTimerLength(100);                 // Future User Parameter
        Log.d(MESSAGE_LOG, "Simulation Started.");
        constructNoteButtons();

        // MidiDriver stuff.
        midi = new MidiDriver();
        if (midi != null) {
            midi.setOnMidiStartListener(this);
        }
        prevActiveKeyIx = -1;
    }

    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // On resume
    @Override
    protected void onResume()
    {
        super.onResume();
        // Start midi
        if (midi != null)
            midi.start();
    }

    // I don't think this fixed anything.
    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();
        // Stop midi
        if (midi != null)
            midi.stop();
    }


    public void addNote(View view) {
        String noteIxStr = view.getTag().toString();
        int noteIx = Integer.parseInt(noteIxStr);
        keyFinder.addNoteToList(keyFinder.getAllNotes().getNoteAtIndex(noteIx));
        printActiveKeyToScreen();
        playActiveKeyNote();
        // Prints list of active notes to log.
        Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + keyFinder.getActiveKey().getName());
    }

    /**
     * Generate buttons for all 12 notes.
     * Each button will add the corresponding note to the KeyFinder.
     */
    public void constructNoteButtons() {
        // Had to add LinearLayout class in front of LayoutParams
        LinearLayout llLeft = (LinearLayout)findViewById(R.id.noteButtonLayoutLeft);
        LinearLayout llRight = (LinearLayout)findViewById(R.id.noteButtonLayoutRight);
        LinearLayout[] llArr = { llLeft, llRight};
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // Construct and add note button.
        for (int i = 0; i < 12; i++) {
            final Button curNoteButton = new Button(this);
            curNoteButton.setText(MainActivity.notes[i]);
            // curNoteButton.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            curNoteButton.setId(i);
            curNoteButton.setTag(i);
            // Set buttons on click to add note
            curNoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNote(curNoteButton);
                }
            });
            llArr[i%2].addView(curNoteButton, lp);
        }
    }

    public void playActiveKeyNote() {
        prevActiveKeyIx = curActiveKeyIx;
        curActiveKeyIx = keyFinder.getActiveKey().getIx() + 36; // 36 == C
        if (prevActiveKeyIx != curActiveKeyIx) {
            sendMidi(0x80, prevActiveKeyIx, 0);
            sendMidi(0x90, curActiveKeyIx, 63);
        }
    }


    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Listener for sending initial midi messages when the Sonivox
    // synthesizer has been started, such as program change.
    @Override
    public void onMidiStart()
    {
        // Program change - harpsichord
        sendMidi();

        // Get the config
        int config[] = midi.config();

        Resources resources = getResources();
    }

    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Send a midi message, 2 bytes
    protected void sendMidi()
    {
        byte msg[] = new byte[2];

        msg[0] = (byte) 0xc0;
                             // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
        msg[1] = (byte) 54;  // 42 == Cello

        midi.write(msg);
    }

    // Method taken from:
    // https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Send a midi message, 3 bytes
    protected void sendMidi(int event, int n, int v)
    {
        byte msg[] = new byte[3];

        msg[0] = (byte) event;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }
}
