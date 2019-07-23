package com.convergencelab.smartdrone.models;

import com.convergencelab.smartdrone.Constants;
import com.example.keyfinder.Note;
import com.example.keyfinder.Voicing;

import org.billthefarmer.mididriver.MidiDriver;

public class MidiDriverModel {
    /**
     * Start note playback.
     */
    private static final int START_NOTE = 0X90;

    /**
     * Stop note playback.
     */
    private static final int STOP_NOTE = 0X80;

    /**
     * Program change.
     */
    private static final int PROGRAM_CHANGE = 0XC0;

    /**
     * No volume.
     */
    private static final int VOLUME_OFF = 0;

    /**
     * Current voicing being synthesized; transposed.
     */
    private Voicing curVoicing;

    /**
     * Flag is sound is currently being produced by midi driver.
     */
    private boolean isActive;

    /**
     * Default midi driver mCurVolume.
     */
    public static final int DEFAULT_VOLUME = 65;

    /**
     * Midi driver.
     */
    private MidiDriver midiDriver;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    /**
     * Plugin for midi driver; sound of drone.
     */
    private int plugin;

    /**
     * Playback volume.
     * MIN -> MAX; 0 -> 100.
     */
    private int mCurVolume;

    /**
     * Constructor
     */
    public MidiDriverModel() {
        midiDriver = new MidiDriver();
        mCurVolume = DEFAULT_VOLUME;
        curVoicing = null;
        isActive = false;
    }

    public MidiDriverModel(int mCurVolume, int pluginIx) {
        midiDriver = new MidiDriver();
        this.mCurVolume = mCurVolume;
        curVoicing = null;
        isActive = false;
        this.plugin = pluginIx;
    }

    /**
     * Get midi driver.
     * @return      MidiDriver; midi driver.
     */
    public MidiDriver getMidiDriver() {
        return midiDriver;
    }

    /**
     * Set mCurVolume for midi driver.
     * @param       mCurVolume int; mCurVolume for midi driver.
     */
    public void setVolume(int mCurVolume) {
        this.mCurVolume = mCurVolume;
    }

    /**
     * Get mCurVolume for midi driver.
     * @return      int; mCurVolume of midi driver.
     */
    public int getVolume() {
        return mCurVolume;
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Initial setup data for midi.
     */
    public void sendMidiSetup() {
        byte msg[] = new byte[2];
        msg[0] = (byte) Constants.PROGRAM_CHANGE;
        msg[1] = (byte) plugin;
        midiDriver.write(msg);
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Send data that is to be synthesized by midi driver.
     * @param       event int; type of event.
     * @param       midiKey int; index of note (uses octaves).
     * @param       mCurVolume int; mCurVolume of note.
     */
    public void sendMidiNote(int event, int midiKey, int mCurVolume) {
        byte msg[] = new byte[3];
        msg[0] = (byte) event;
        msg[1] = (byte) midiKey;
        msg[2] = (byte) mCurVolume;
        midiDriver.write(msg);
    }

    /**
     * Adds note to playback. Used in TemplateCreator.
     * @param       toAdd int; index of note to add.
     */
    public void addNoteToPlayback(Note toAdd) {
        sendMidiNote(Constants.START_NOTE, toAdd.getRawIx(), mCurVolume);
    }

    /**
     * Removes note from playback. Used in TemplateCreator.
     * @param       toStop int; index of note to remove.
     */
    public void removeNoteFromPlayback(Note toStop) {
        sendMidiNote(Constants.STOP_NOTE, toStop.getRawIx(), mCurVolume);
    }


    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       mCurVolume int; mCurVolume of notes.
     */
    public void sendMidiChord(int event, int[] midiKeys, int mCurVolume) {
        for (int key : midiKeys) {
            sendMidiNote(event, key, mCurVolume);
        }
    }

    //todo better name; something like switchVoicing() ?
    /**
     * Stops playing previous voicing and starts playing new voicing.
     * @param       toPlay Voicing; voicing to play.
     */
    public void playVoicing(Voicing toPlay) {
        if (curVoicing != null) {
            stopVoicing(curVoicing);
        }
        startVoicing(toPlay);
        isActive = true;
    }

    /**
     * Stops voicing that
     * @param       toStop Voicing; voicing to stop playing.
     */
    public void stopVoicing(Voicing toStop) {
        int[] voiceIxs = new int[toStop.numVoices()];
        for (int i = 0; i < toStop.numVoices(); i++) {
            voiceIxs[i] = toStop.getVoice(i).getRawIx();
        }
        sendMidiChord(Constants.STOP_NOTE, voiceIxs, Constants.VOLUME_OFF); //todo new sendmidichord method
    }

    /**
     * Starts playing voicing.
     * Updates current voicing flag.
     * @param       toStart Voicing; voicing to be played.
     */
    public void startVoicing(Voicing toStart) {
        int[] voiceIxs = new int[toStart.numVoices()];
        for (int i = 0; i < toStart.numVoices(); i++) {
            voiceIxs[i] = toStart.getVoice(i).getRawIx(); // todo: Debug: changed from getIx to rawIx
        }
        sendMidiChord(Constants.START_NOTE, voiceIxs, mCurVolume); //todo new sendmidichordmethod
        curVoicing = toStart;
    }

    //todo get rid of this method
    /**
     * Set current voicing.
     * @param       voicing Voicing; current voicing.
     */
    public void setCurVoicing(Voicing voicing) {
        curVoicing = voicing;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    /**
     * Set plugin for midi.
     * @param       pluginIx int; plugin index.
     */
    public void setPlugin(int pluginIx) {
        plugin = pluginIx;
    }
}