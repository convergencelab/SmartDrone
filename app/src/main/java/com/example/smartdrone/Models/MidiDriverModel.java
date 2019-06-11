package com.example.smartdrone.Models;

import com.example.smartdrone.Constants;
import com.example.smartdrone.Voicing;

import org.billthefarmer.mididriver.MidiDriver;

public class MidiDriverModel {
    /**
     * Current voicing being synthesized; transposed.
     */
    private Voicing curVoicing;

    /**
     * Flag is sound is currently being produced by midi driver.
     */
    private boolean isActive;

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
    /**
     * Plugin for midi driver; sound of drone.
     */
    private int plugin;

    /**
     * Midi driver volume.
     * MIN -> MAX; 0 -> 100.
     */
    private int volume;

    /**
     * Constructor
     */
    MidiDriverModel() {
        midiDriver = new MidiDriver();
        volume = DEFAULT_VOLUME;
        plugin = Constants.PLUGIN_CHOIR;
        curVoicing = null;
        isActive = false;
    }

    /**
     * Get midi driver.
     * @return      MidiDriver; midi driver.
     */
    public MidiDriver getMidiDriver() {
        return midiDriver;
    }

    /**
     * Set volume for midi driver.
     * @param       volume int; volume for midi driver.
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * Get volume for midi driver.
     * @return      int; volume of midi driver.
     */
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
    public void sendMidiNote(int event, int midiKey, int volume) {
        byte msg[] = new byte[3];
        msg[0] = (byte) event;
        msg[1] = (byte) midiKey;
        msg[2] = (byte) volume;
        midiDriver.write(msg);
    }

//    //todo this is old method; create new one
//    //todo clean up. refactor, make an improved ui for using this method.
//    /**
//     * Sends multiple messages to be synthesized by midi driver.
//     * Each note is given specifically.
//     * @param       event int; type of event.
//     * @param       midiKeys int[]; indexes of notes (uses octaves).
//     * @param       volume int; volume of notes.
//     */
//    public void sendMidiChord(int event, int[] midiKeys, int volume, int rootIx) {
//        int octaveAdjustment = 0;
//        if (midiKeys[0] + rootIx > 47) {
//            octaveAdjustment = -12;
//        }
//        for (int key : midiKeys) {
//            sendMidiNote(event, key + rootIx + octaveAdjustment, volume);
//        }
//    }

    //todo new method; delete old one
    /**
     * Sends multiple messages to be synthesized by midi driver.
     * Each note is given specifically.
     * @param       event int; type of event.
     * @param       midiKeys int[]; indexes of notes (uses octaves).
     * @param       volume int; volume of notes.
     */
    private void sendMidiChord(int event, int[] midiKeys, int volume) {
        for (int key : midiKeys) {
            sendMidiNote(event, key, volume);
        }
    }

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

//    //todo old methods; create new ones.
//    /**
//     * Stop voicing.
//     * @param voiceIxs
//     * @param keyIx
//     */
//    public void stopVoicing(int[] voiceIxs, int keyIx) {
//        sendMidiChord(Constants.STOP_NOTE, voiceIxs, Constants.VOLUME_OFF, keyIx);
//    }
//
//    private void startVoicing(int[] voiceIxs, int keyIx) {
//        sendMidiChord(Constants.START_NOTE, voiceIxs, volume, keyIx);
//    }

    /**
     * Stops voicing that
     * @param       toStop Voicing; voicing to stop playing.
     */
    public void stopVoicing(Voicing toStop) {
        int[] voiceIxs = toStop.getVoiceIxs();
        sendMidiChord(Constants.STOP_NOTE, voiceIxs, Constants.VOLUME_OFF); //todo new sendmidichord method
    }

    /**
     * Starts playing voicing.
     * Updates current voicing flag.
     * @param       toStart Voicing; voicing to be played.
     */
    public void startVoicing(Voicing toStart) {
        int[] voiceIxs = toStart.getVoiceIxs();
        sendMidiChord(Constants.START_NOTE, voiceIxs, volume); //todo new sendmidichordmethod
        curVoicing = toStart;
    }

//    /**
//     * Get current voicing.
//     * @return      Voicing; current voicing.
//     */
//    public Voicing getCurVoicingTransposed() {
//        return curVoicingTransposed;
//    }

    /**
     * Set current voicing.
     * @param       voicing Voicing; current voicing.
     */
    public void setCurVoicing(Voicing voicing) {
        curVoicing = voicing;
    }

//    /**
//     * Get current voicing template.
//     * @return      Voicing; voicing template.
//     */
//    public Voicing getCurVoicingTemplate() {
//        return curVoicingTemplate;
//    }

//    /**
//     * Set the current voicing template.
//     * @param       voicingTemplate Voicing; voicing template.
//     */
//    public void setCurVoicingTemplate(Voicing voicingTemplate) {
//        curVoicingTemplate = voicingTemplate;
//        if (isActive) {
//            //todo: some code that will properly transpose the current voicing being output
//        }
//    }

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
