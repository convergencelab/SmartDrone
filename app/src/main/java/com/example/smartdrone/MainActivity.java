package com.example.smartdrone;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;


public class MainActivity extends AppCompatActivity
    implements MidiDriver.OnMidiStartListener {

    // TarsosDSP
    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

    public static final String[] notes =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    public static KeyFinder keyFinder = new KeyFinder();
    int prevKey;
    int curKey;

    int lastAddedNote = -1;

    public MidiDriver midi;
    public MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();


        // MidiDriver stuff.
        midi = new MidiDriver();
        if (midi != null) {
            midi.setOnMidiStartListener(this);
        }
        prevKey = -1;
        curKey = -1;
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

    public void addNote(int noteIx) {
        keyFinder.addNoteToList(keyFinder.getAllNotes().getNoteAtIndex(noteIx));
        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

    public void processPitch(float pitchInHz) {
        TextView pitchText = (TextView) findViewById(R.id.pitchText);

        TextView noteText = (TextView) findViewById(R.id.noteText);

        double p = (double) pitchInHz;

        int midiKey = PitchConverter.hertzToMidiKey(p) % 12;
        if (midiKey != lastAddedNote && midiKey != -1) {
            addNote(midiKey);
        }


        pitchText.setText("" + (int) pitchInHz);
        if (p != -1) {
            noteText.setText(notes[midiKey]);
        } else {
            noteText.setText("null");
        }
    }

    public void secondActivity(View view) {
        Intent intent = new Intent(this, SimulationActivity.class);
        startActivity(intent);
    }


    public void playActiveKeyNote() {
        prevKey = curKey;
        curKey = keyFinder.getActiveKey().getIx() + 36; // 36 == C
        if (prevKey != curKey) {
            printActiveKeyToScreen();
            sendMidi(0x80, prevKey, 0);
            sendMidi(0x90, curKey, 63);
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
    protected void sendMidi(int m, int n, int v)
    {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }

    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + keyFinder.getActiveKey().getName());
    }
}
