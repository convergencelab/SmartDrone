package com.example.smartdrone.Models;

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

    private boolean timerIsQueued;

    private int timerToStart;

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
        lastHeard = -1;
        lastAdded = -1;
        noteHasChanged = false;
        timerIsQueued = false;
    }

    /**
     * //todo: fill in method description
     * @param       pitchInHz float; pitch in hertz.
     * @param       keyFinderModel KeyFinderModel; handles key finder.
     */
    public void processPitch(float pitchInHz, KeyFinderModel keyFinderModel) {
        int curHeard = convertPitchToIx((double) pitchInHz);
        if (newNoteDetected(curHeard)) {
            lastHeard = curHeard;
            noteHasChanged = true;
            timeRegistered = System.currentTimeMillis();
            if (timerIsQueued) {
                // Start note timer.
                keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(
                timerToStart).startNoteTimer(keyFinderModel.getKeyFinder(),
                Constants.NOTE_TIMER_LEN);
                timerIsQueued = false;
            }
            if (curHeard == -1) { //todo: encapsulate in method
                lastAdded = -1;
            }
        }
        else {
            if (noteCanBeAdded(curHeard)) {
                keyFinderModel.addNote(curHeard);
                lastAdded = curHeard;
                queueNoteTimer(curHeard, keyFinderModel);
            }
        }

    }

    private void queueNoteTimer(int curHeard, KeyFinderModel keyFinderModel) {
        timerIsQueued = true;
        timerToStart = curHeard;
        keyFinderModel.getKeyFinder().getAllNotes().getNoteAtIndex(curHeard).cancelNoteTimer();
    }

    /**
     * Get index of current monitored note.
     * @return      int; index of current monitored note.
     */
    public int getLastHeard() {
        return lastHeard;
    }

    /**
     * Set index of current monitored note.
     * @param       noteIx int; index of current monitored note.
     */
    public void setLastHeard(int noteIx) {
        lastHeard = noteIx;
    }

    /**
     * Get index of previous added note.
     * @return      int; index of previous added note.
     */
    public int getLastAdded() {
        return lastAdded;
    }

    /**
     * Set index of previous added note.
     * @param       noteIx int; index of previous added note.
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

    public boolean noteFilterLengthMet() {
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
     * Check if current note index is same as last heard note index.
     * @param       curIx int; index of current heard note.
     * @return      boolean; true if same as last heard note index.
     */
    public boolean newNoteDetected(int curIx) {
        return curIx != lastHeard;
    }

    public boolean noteHasChanged() {
        return noteHasChanged;
    }

    public void setNoteHasChanged(boolean bool) {
        noteHasChanged = bool;
    }

    public void setTimerIsQueued(boolean bool) {
        timerIsQueued = bool;
    }

    public boolean isTimerQueued() {
        return timerIsQueued;
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
        return ix != -1 && ix != lastAdded && noteFilterLengthMet();
    }
}
