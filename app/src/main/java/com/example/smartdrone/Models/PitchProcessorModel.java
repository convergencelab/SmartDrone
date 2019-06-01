package com.example.smartdrone.Models;

import android.util.Log;

import com.example.smartdrone.Constants;
import com.example.smartdrone.DroneActivity;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.util.PitchConverter;

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
     * Current key being output.
     */
    public int prevActiveKeyIx;

    /**
     * Previous key that was output.
     */
    public int curActiveKeyIx;

    /**
     * Last note that was added to active note list.
     */
    public int prevAddedNoteIx;

    public int queuedNote;

    public boolean noteIsQueued;

    /**
     * Current note being monitored.
     */
    public int curNoteIx;

    /**
     * Constructor.
      */
    public PitchProcessorModel(DroneModel droneModel) {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);

        prevActiveKeyIx = -1;
        curActiveKeyIx = -1;
        prevAddedNoteIx = -1;
        curNoteIx = -1;
        noteIsQueued = false;
    }

    /**
     * Utilizes other single purpose methods.
     * 1. Converts pitch to ix.
     * 2. Adds note (based on ix) to active note list.
     * 3. Updates the text views on screen.
     * @param       pitchInHz float; current pitch being heard.
     */
    public void processPitch(float pitchInHz, DroneActivity droneActivity, DroneModel droneModel) {
        // Convert pitch to midi key.
        int curKey = convertPitchToIx((double) pitchInHz); // No note will return -1 // todo

        // Note change is detected.
        if (curKey != prevAddedNoteIx) {
            // If previously added note is no longer heard.
            if (prevAddedNoteIx != -1) {
                // Start timer.
                droneModel.getKeyFinderModel().getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).startNoteTimer(droneModel.getKeyFinderModel().getKeyFinder(), droneActivity.noteExpirationLength); // todo
                Log.d(Constants.MESSAGE_LOG_NOTE_TIMER, droneModel.getKeyFinderModel().getKeyFinder().getAllNotes().getNoteAtIndex(
                        prevAddedNoteIx).getName() + ": Started");
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
            else if (noteMeetsConfidence(droneActivity)) { //todo move preferences to proper location
                queuedNote = curKey;
                noteIsQueued = true;
                prevAddedNoteIx = curKey;
                droneModel.getKeyFinderModel().getKeyFinder().getAllNotes().getNoteAtIndex(curKey).cancelNoteTimer();
            }
        }
    }

    public void setPrevActiveKeyIx(int keyIx) {
        prevActiveKeyIx = keyIx;
    }

    public void setPrevAddedNoteIx(int noteIx) {
        prevAddedNoteIx = noteIx;
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

    public boolean noteMeetsConfidence(DroneActivity droneActivity) {
        return (System.currentTimeMillis() - timeRegistered) > droneActivity.noteLengthRequirement;
    }

    public AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    public int getCurActiveKeyIx() {
        return curActiveKeyIx;
    }

    public int getPrevActiveKeyIx() {
        return prevActiveKeyIx;
    }

    public void setNoteIsQueued(boolean bool) {
        noteIsQueued = bool;
    }
}
