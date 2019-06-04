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
    private long timeOfChange;

    /**
     * Index of current monitored note.
     */
    private int lastHeard;

    /**
     * Index of previous note.
     * Last note added to key finder.
     */
    private int lastAdded;

    /**
     * Flag for change in note detection.
     */
    private boolean noteHasChanged;

    private boolean noteTimerIsQueued;

    private int noteToStart;

    /**
     * Filter for note length.
     * Number of milliseconds.
     */
    public int noteFilterLength;

    /**
     * Constructor.
      */
    public PitchProcessorModel() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        lastHeard = -1;
        lastAdded = -1;
        noteHasChanged = false;
        noteTimerIsQueued = false;
    }

    /**
     * Process pitch of note.
     * @param       pitchInHz float; pitch in hertz.
     * @param       keyFinderModel KeyFinderModel; handles key finder.
     */
    public void processPitch(float pitchInHz, KeyFinderModel keyFinderModel) {
        int curHeard = convertPitchToIx((double) pitchInHz);
        if (noteChangeDetected(curHeard)) {
            lastHeard = curHeard;
            noteHasChanged = true;
            timeOfChange = System.currentTimeMillis();
            lastAdded = Constants.NULL_NOTE_IX;
            if (noteTimerIsQueued) {
                keyFinderModel.startNoteTimer(noteToStart);
                Log.d("timer", noteToStart + " started");
                noteTimerIsQueued = false;
            }
        }
        else if (noteCanBeAdded(curHeard)) {
            lastAdded = curHeard;
            keyFinderModel.addNote(curHeard);
            queueNoteTimer(curHeard);
            keyFinderModel.cancelNoteTimer(curHeard);
            Log.d("timer", curHeard + " cancelled");
        }
    }

    /**
     * Flags timer for note is ready to be started.
     * @param       noteIx int; index of note.
     */
    private void queueNoteTimer(int noteIx) {
        noteTimerIsQueued = true;
        noteToStart = noteIx;
    }

    /**
     * Set index of last heard note.
     * @param       noteIx int; index of note.
     */
    public void setLastHeard(int noteIx) {
        lastHeard = noteIx;
    }

    /**
     * Set index of last added note.
     * @param       noteIx int; index of note.
     */
    public void setLastAdded(int noteIx) {
        lastAdded = noteIx;
    }

    /**
     * Get audio dispatcher.
     * @return      AudioDispatcher; audio dispatcher.
     */
    public AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Check if the current
     * @return
     */
    private boolean noteFilterLengthMet() {
        return (System.currentTimeMillis() - timeOfChange) > noteFilterLength;
    }

    /**
     * Converts pitch (hertz) to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    public int convertPitchToIx(double pitchInHz) {
        // No note is heard.
        if (pitchInHz == Constants.NULL_NOTE_IX) {
            return Constants.NULL_NOTE_IX;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz) % 12;
    }

    /**
     * Check if current note index is same as last heard note index.
     * @param       curIx int; index of current heard note.
     * @return      boolean; true if same as last heard note index.
     */
    public boolean noteChangeDetected(int curIx) {
        return curIx != lastHeard;
    }

    /**
     * Check if note change has been detected.
     * @return      boolean; true if note change detected.
     */
    public boolean noteHasChanged() {
        return noteHasChanged;
    }

    /**
     * Set note change detection flag.
     * @param       bool boolean; true if change detected.
     */
    public void setNoteHasChanged(boolean bool) {
        noteHasChanged = bool;
    }

    /**
     * Three conditions in order to return true.
     * 1) Note must be detected; not -1.
     * 2) Ix was is not the last note added to list.
     * 3) The note must be heard for the required amount of time.
     *
     * @param       ix int; index of note.
     * @return      boolean; true if conditions met.
     */
    private boolean noteCanBeAdded(int ix) {
        return ix != Constants.NULL_NOTE_IX && ix != lastAdded && noteFilterLengthMet();
    }

    /**
     * Check whether or not a note is detected.
     * @param       noteIx int; index of note.
     * @return      boolean; true if note heard.
     */
    private boolean isNullNote(int noteIx) {
        return noteIx == Constants.NULL_NOTE_IX;
    }
}
