package com.convergencelabstfx.smartdrone.models;

import com.convergencelabstfx.smartdrone.Constants;
import com.example.keyfinder.Note;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.util.PitchConverter;


public class PitchProcessorModel {
    /**
     * Null note.
     */
    private static final Note NULL_NOTE = null;

    /**
     * Audio dispatcher connected to microphone.
     */
    private AudioDispatcher dispatcher;

    /**
     * Used to stamp time of note heard.
     */
    private long timeFirstHeard;

    /**
     * Last heard note.
     */
    private Note lastHeard;

    /**
     * Last added note.
     */
    private Note lastAdded;

    /**
     * Flag for change in note detection.
     */
    private boolean noteHasChanged;

    /**
     * Flag if a note timer is ready to be started.
     */
    private boolean noteTimerIsQueued;

    /**
     * Note ready to have timer started.
     */
    private Note noteToStart;

    /**
     * Filter for note length.
     * Number of milliseconds.
     */
    public int noteFilterLength;

    /**
     * Constructor.
     */
    PitchProcessorModel() {
        dispatcher = null;
        lastAdded = NULL_NOTE;
        lastAdded = NULL_NOTE;
        noteHasChanged = false;
        noteTimerIsQueued = false;
    }

    /**
     * Process pitch of note.
     * @param       pitchInHz float; pitch in hertz.
     * @param       keyFinderModel KeyFinderModel; handles key finder.
     */
    public Note processPitch(float pitchInHz, KeyFinderModel keyFinderModel) {
        int curHeardIx = convertPitchToIx((double) pitchInHz);
        Note curHeard;
        if (curHeardIx == -1) {
            curHeard = NULL_NOTE;
        }
        else {
            curHeard = keyFinderModel.getKeyFinder().getNote(curHeardIx);
        }
        if (noteChangeDetected(curHeard)) {
            lastHeard = curHeard;
            noteHasChanged = true;
            timeFirstHeard = System.currentTimeMillis();
            lastAdded = NULL_NOTE;
            if (noteTimerIsQueued) {
                keyFinderModel.startNoteTimerRefac(noteToStart);
                noteTimerIsQueued = false;
            }
        }
        else if (noteCanBeAdded(curHeard)) {
            lastAdded = curHeard;
            keyFinderModel.addNote(curHeard);
            queueNoteTimer(curHeard);
            keyFinderModel.cancelNoteTimerRefac(curHeard);
        }
        return curHeard;
    }

    /**
     * Flags timer for note is ready to be started.
     * @param       toQueue Note; note to queue.
     */
    private void queueNoteTimer(Note toQueue) {
        noteTimerIsQueued = true;
        noteToStart = toQueue;
    }

    /**
     * Get audio dispatcher.
     * @return      AudioDispatcher; audio dispatcher.
     */
    AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(AudioDispatcher dis) {
        dispatcher = dis;
    }

    public AudioDispatcher constructDispatcher() {
        AudioDispatcher newDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
                Constants.SAMPLE_RATE, Constants.AUDIO_BUFFER_SIZE, Constants.BUFFER_OVERLAP);
        return newDispatcher;
    }

    /**
     * Check if the note filter length has been reached.
     * @return      boolean; true if note has been heard as long as the filter length.
     */
    private boolean noteFilterLengthMet() {
        return (System.currentTimeMillis() - timeFirstHeard) >= noteFilterLength;
    }

    /**
     * Converts pitch (hertz) to note index.
     * @param       pitchInHz double;
     * @return      int; ix of note.
     */
    public int convertPitchToIx(double pitchInHz) {
        if (pitchInHz == Constants.NULL_NOTE_IX) {
            return Constants.NULL_NOTE_IX;
        }
        return PitchConverter.hertzToMidiKey(pitchInHz) % 12;
    }

    /**
     * Check if current note is not the same as last heard note.
     * @param       curNote Note; current heard note.
     * @return      boolean; true if curNote is different than lastHeardNote.
     */
    private boolean noteChangeDetected(Note curNote) {
        return curNote != lastHeard;
    }

    /**
     * Check if note change has been detected.
     * @return      boolean; true if note change detected.
     */
    boolean noteHasChanged() {
        return noteHasChanged;
    }

    /**
     * Set note change detection flag.
     * @param       bool boolean; true if change detected.
     */
    void setNoteHasChanged(boolean bool) {
        noteHasChanged = bool;
    }

    /**
     * Set last heard note.
     * @param       lastHeard Note; last heard note.
     */
    public void setLastHeard(Note lastHeard) {
        this.lastAdded = lastHeard;
    }

    /**
     * Set last added note.
     * @param       lastAdded Note; last added note.
     */
    public void setLastAdded(Note lastAdded) {
        this.lastAdded = lastAdded;
    }

    /**
     * Three conditions in order to return true.
     * 1) Note must be detected; not NULL_NOTE.
     * 2) Note was not the last note to be added to the list.
     * 3) Note note must be heard for the required amount of time.
     *
     * @param       toCheck Note; note to check.
     * @return      boolean; true if conditions met.
     */
    private boolean noteCanBeAdded(Note toCheck) {
        return toCheck != NULL_NOTE && toCheck != lastAdded && noteFilterLengthMet();
    }

}
