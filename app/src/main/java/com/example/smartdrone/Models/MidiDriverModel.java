package com.example.smartdrone.Models;

import com.example.smartdrone.Constants;
import com.example.smartdrone.Voicing;

import org.billthefarmer.mididriver.MidiDriver;

public class MidiDriverModel {
    /**
     * Default midi driver volume.
     */
    private final int DEFAULT_VOLUME = 65; //todo move to constants

    /**
     * Midi driver.
     */
    private MidiDriver midiDriver;

    // List of all the plugins available.
    // https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java
    //todo user parameter
    private int plugin;

    /**
     * Midi driver volume.
     * MIN - MAX; 0 - 100.
     */
    private int volume;

    /**
     * Constructor
     */
    public MidiDriverModel() {
        midiDriver = new MidiDriver();
        volume = DEFAULT_VOLUME;
        plugin = Constants.PLUGIN_CHOIR;

    }

    /**
     * Get midi driver.
     * @return      MidiDriver; midi driver.
     */
    public MidiDriver getMidiDriver() {
        return midiDriver;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return volume;
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Initial setup data for midi.
     */
    public void sendMidiSetup() {
        byte msg[] = new byte[2];
        msg[0] = (byte) Constants.PROGRAM_CHANGE;    // 0XC0 == PROGRAM CHANGE
        msg[1] = (byte) plugin;
        midiDriver.write(msg);
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Send data that is to be synthesized by midi driver.
     * @param       event int; type of event.
     * @param       midiKey int; index of note (uses octaves).
     * @param       volume int; volume of note.
     */
    public void sendMidi(int event, int midiKey, int volume) {
        byte msg[] = new byte[3];
        msg[0] = (byte) event;
        msg[1] = (byte) midiKey;
        msg[2] = (byte) volume;
        midiDriver.write(msg);
    }

    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    public void sendMidiChord(int event, int[] midiKeys, int volume) {
        for (int key : midiKeys) {
            sendMidi(event, key, volume);
        }
    }

    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    public void sendMidiChord(int event, int[] midiKeys, int volume, int rootIx) {
        int octaveAdjustment = 0;
        if (midiKeys[0] + rootIx > 47) {
            octaveAdjustment = -12;
        }

        for (int key : midiKeys) {
            sendMidi(event, key + rootIx + octaveAdjustment, volume);
        }
    }

    public void switchToVoicing(int[] voiceIxs, int cur, int prev) {
        // Stop chord.
        stopVoicing(voiceIxs, prev);
        // Start chord.
        startVoicing(voiceIxs, cur);
    }

    public void stopVoicing(int[] voiceIxs, int keyIx) {
        sendMidiChord(Constants.STOP_NOTE, voiceIxs, Constants.VOLUME_OFF, keyIx);
    }

    private void startVoicing(int[] voiceIxs, int keyIx) {
        sendMidiChord(Constants.START_NOTE, voiceIxs, getVolume(), keyIx);
    }
}
