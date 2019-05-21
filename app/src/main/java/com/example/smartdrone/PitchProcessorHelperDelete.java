package com.example.smartdrone;

import android.app.Activity;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.util.PitchConverter;

public class PitchProcessorHelperDelete {
    private AudioDispatcher dispatcher;
    private int prevAddedNoteIx;
    private int curNoteIx;
    private int noteExpirationLength;
    // private KeyFinder keyFinder;
    private Activity activity;
    private long timeRegistered;       //ph
    private int noteLengthFilter; //ph

    public PitchProcessorHelperDelete(Activity activity, KeyFinder keyFinder) {
        this.activity = activity;
        // this.keyFinder = keyFinder;
        this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0); //ph
        this.prevAddedNoteIx = -1;
        this.curNoteIx = -1;
        this.noteLengthFilter = 60;
        noteExpirationLength = keyFinder.getNoteTimerLength();
    }

    /*
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
    */

    /**
     * Utilizes other single purpose methods.
     * 1. Converts pitch to ix.
     * 2. Adds note (based on ix) to active note list.
     * 3. Updates the text views on screen.
     * @param       pitchInHz float; current pitch being heard.
     */
    /*
    public void processPitch(float pitchInHz) {
        // Convert pitch to midi key.
        int curIx = convertPitchToIx((double) pitchInHz); // No note will return -1
        // Note change is detected.
        if (curIx != prevAddedNoteIx) {
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
            else if (curIx != curNoteIx) {
                curNoteIx = curIx;
                timeRegistered = System.currentTimeMillis();
            }
            // Current note is heard.
            else if (noteMeetsConfidence()) {
                addNote(curIx);
                MainActivity.keyFinder.getAllNotes().getNoteAtIndex(curIx).cancelNoteTimer();
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
            // MainActivity.playActiveKeyNote();
        }
    }
    */

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
        return (System.currentTimeMillis() - timeRegistered) > noteLengthFilter;
    }


}
