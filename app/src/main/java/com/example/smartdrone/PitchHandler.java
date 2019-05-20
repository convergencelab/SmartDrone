package com.example.smartdrone;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

public class PitchHandler {
    private AudioDispatcher dispatcher;
    private int prevAddedNoteIx;
    private int curNoteIx;
    private int noteExpirationLength;
    // private KeyFinder keyFinder;
    private Activity activity;
    private long timeRegistered;       //ph
    private int noteLengthRequirement; //ph

    public PitchHandler(Activity activity, KeyFinder keyFinder) {
        this.activity = activity;
        // this.keyFinder = keyFinder;
        this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0); //ph
        this.prevAddedNoteIx = -1;
        this.curNoteIx = -1;
        this.noteLengthRequirement = 60;
        noteExpirationLength = keyFinder.getNoteTimerLength();
    }

    public void start() {
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                activity.runOnUiThread(new Runnable() {
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
    }

    /**
     * Utilizes other single purpose methods.
     * 1. Converts pitch to ix.
     * 2. Adds note (based on ix) to active note list.
     * 3. Updates the text views on screen.
     * @param       pitchInHz float; current pitch being heard.
     */
    public void processPitch(float pitchInHz) {
        // Convert pitch to midi key.
        int curKey = convertPitchToIx((double) pitchInHz); // No note will return -1
        // Note change is detected.
        if (curKey != prevAddedNoteIx) {
            // If previously added note is no longer heard.
            if (prevAddedNoteIx != -1) {
                // Start timer.
                MainActivity.keyFinder.getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(MainActivity.keyFinder, noteExpirationLength);
                // Log.d(MESSAGE_LOG_NOTE_TIMER, keyFinder.getAllNotes().getNoteAtIndex(
                //        prevAddedNoteIx).getName() + ": Started");
            }
            // No note is heard.
            if (pitchInHz == -1) {
                curNoteIx = -1;
                prevAddedNoteIx = -1;
            }
            // Different note is heard.
            else if (curKey != curNoteIx) {
                curNoteIx = curKey;
                timeRegistered = System.currentTimeMillis();
            }
            // Current note is heard.
            else if (noteMeetsConfidence()) {
                addNote(curKey);
                MainActivity.keyFinder.getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
                // Log.d(MESSAGE_LOG_NOTE_TIMER, MainActivity.keyFinder.getAllNotes().getNoteAtIndex(
                //        prevAddedNoteIx).getName() + ": Cancelled");
            }
        }
        // Note removal detected.
        if (MainActivity.keyFinder.getNoteHasBeenRemoved()) {
            MainActivity.keyFinder.setNoteHasBeenRemoved(false);
            // Log.d(MESSAGE_LOG_REMOVE, keyFinder.getRemovedNote().getName());
            // Log.d(MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        }
        // If active key has changed.
        if (MainActivity.keyFinder.getActiveKeyHasChanged()) {
            MainActivity.playActiveKeyNote();
        }
    }

    /**
     * Converts pitch (hertz) to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    public int convertPitchToIx(double pitchInHz) {
        // No note is heard.
        if (pitchInHz == -1) {
            return -1;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz) % 12;
    }

    public boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > noteLengthRequirement;
    }

    /**
     * Add note to Active Note list based on the given ix.
     * @param       noteIx int; index of note.
     */
    public void addNote(int noteIx) {
        Note curNote = MainActivity.keyFinder.getAllNotes().getNoteAtIndex(noteIx);
        MainActivity.keyFinder.addNoteToList(curNote);
        // Log.d(MESSAGE_LOG_ADD, curNote.getName());
        // Log.d(MESSAGE_LOG_LIST, keyFinder.getActiveNotes().toString());
        prevAddedNoteIx = noteIx;

        // printActiveKeyToScreen();
        playActiveKeyNote();
        // Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

}
