package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.util.PitchConverter;


//todo make current/prev notes/keys reset on active/deactivate toggle
public class PitchProcessorModel {
    /**
     * Audio dispatcher connected to microphone.
     */
    private AudioDispatcher dispatcher;

    /**
     * Used to stamp time of note heard.
     */
    private long timeRegistered;

    /**
     * Index of current active key.
     */
    private int curActiveKeyIx;

    /**
     * Index previous active key.
     */
    private int prevActiveKeyIx;

    /**
     * Index of current monitored note.
     */
    private int curNoteIx;

    /**
     * Index of previous note.
     * Last note added to key finder.
     */
    private int prevAddedNoteIx;

    /**
     * Filter for note length.
     * Number of milliseconds.
     */
    public int noteFilterLengthRequirement;

    /**
     * Constructor.
      */
    public PitchProcessorModel() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        // Set all note/key indices to -1.
        this.resetAllIxs();
    }

    /**
     * //todo: fill in method description
     * @param pitchInHz
     * @param keyFinderModel
     */
    public void processPitch(float pitchInHz, KeyFinderModel keyFinderModel) {
        // Convert pitch to midi key.
        int curIx = convertPitchToIx((double) pitchInHz); // No note will return -1

        // Note change is detected.
        if (curIx != prevAddedNoteIx) {
            // If previously added note is no longer heard.
            if (prevAddedNoteIx != -1) {
                // Start timer.
                keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(keyFinderModel.getKeyFinder(),
                        keyFinderModel.getKeyFinder().getNoteTimerLength());
                Log.d(Constants.MESSAGE_LOG_NOTE_TIMER, keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).getName() + ": Started");
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
            else if (noteMeetsConfidence()) { //todo move preferences to proper location
                keyFinderModel.addNote(curIx);
                prevAddedNoteIx = curIx;
                keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(curIx).cancelNoteTimer();
            }
        }
    }

    /**
     * Get index of current active key.
     * @return      int; index of current active key.
     */
    public int getCurActiveKeyIx() {
        return curActiveKeyIx;
    }

    /**
     * Set index of current active key.
     * @param       keyIx int; index of current active key.
     */
    public void setCurActiveKeyIx(int keyIx) {
        curActiveKeyIx = keyIx;
    }

    /**
     * Get index of previous active key.
     * @return      int; index of previous active key.
     */
    public int getPrevActiveKeyIx() {
        return prevActiveKeyIx;
    }

    /**
     * Set index of previous active key.
     * @param       keyIx int; index of previous active key.
     */
    public void setPrevActiveKeyIx(int keyIx) {
        prevActiveKeyIx = keyIx;
    }

    /**
     * Get index of current monitored note.
     * @return      int; index of current monitored note.
     */
    public int getCurNoteIx() {
        return curNoteIx;
    }

    /**
     * Set index of current monitored note.
     * @param       noteIx int; index of current monitored note.
     */
    public void setCurNoteIx(int noteIx) {
        curNoteIx = noteIx;
    }

    /**
     * Get index of previous added note.
     * @return      int; index of previous added note.
     */
    public int getPrevAddedNoteIx() {
        return prevAddedNoteIx;
    }

    /**
     * Set index of previous added note.
     * @param       noteIx int; index of previous added note.
     */
    public void setPrevAddedNoteIx(int noteIx) {
        prevAddedNoteIx = noteIx;
    }

    /**
     * Get audio dispatcher.
     * @return      AudioDispatcher; audio dispatcher.
     */
    public AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    public boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > noteFilterLengthRequirement;
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

    /**
     * Check if parameter index is same as
     * @param curIx
     * @return
     */
    public boolean newNoteDetected(int curIx) {
        return curIx != prevAddedNoteIx;
    }

    /**
     * Set all note/key indices to -1.
     */
    public void resetAllIxs() {
        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        prevAddedNoteIx = -1;
        curNoteIx = -1;
    }
}
