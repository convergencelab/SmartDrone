package com.convergencelab.smartdrone.models.droneplayer;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashSet;
import java.util.Set;

public class DronePlayerImpl implements DronePlayer {
    /**
     * Start note playback.
     */
    private static final int START = 0X90;

    /**
     * Stop note playback.
     */
    private static final int STOP = 0X80;

    /**
     * Program change.
     */
    private static final int PROGRAM_CHANGE = 0XC0;

    /**
     * No volume.
     */
    private static final int VOLUME_OFF = 0;

    /**
     * Default volume for playback.
     * TODO: currently the only volume, user cannot modify this.
     */
    private static final int DEFAULT_VOLUME = 65;

    /**
     * Object that performs midi synthesis.
     */
    private MidiDriver mDriver;

    /**
     * Current notes being synthesized by the midi mDriver.
     */
    private Set<Integer> mActiveNotes;

    /**
     * Current volume of midi mDriver.
     */
    private int mVolume;

    /**
     * Current plugin for midi mDriver.
     */
    private int mPlugin;

    /**
     * Constructor.
     */
    public DronePlayerImpl(int plugin) {
        mDriver = new MidiDriver();
        mActiveNotes = new HashSet<>();
        mVolume = DEFAULT_VOLUME;
        mPlugin = plugin;
    }

    /**
     * Initializes midi driver for playback.
     */
    @Override
    public void start() {
        mDriver.start();
        sendMidiSetup();
    }

    /**
     * Ends midi playback.
     */
    @Override
    public void stop() {
        // Todo: I can't remember if this interrupts playback? Guess I'll find out.
        endPlayback();
        mDriver.stop();
    }

    /**
     * Stops current playback then plays all notes given.
     * @param toPlay array of notes to be played.
     */
    @Override
    public void play(int[] toPlay) {
        endPlayback();

        for (int note : toPlay) {
            startNotePlayback(note);
            mActiveNotes.add(note);
        }
    }

    /**
     * Stops current playback then plays note given.
     * @param toPlay note to be played.
     */
    @Override
    public void play(int toPlay) {
        endPlayback();

        startNotePlayback(toPlay);
        mActiveNotes.add(toPlay);
    }

    /**
     * Adds notes to current playback.
     * @param toAdd notes to add.
     */
    @Override
    public void add(int[] toAdd) {
        for (Integer note : toAdd) {
            if (!mActiveNotes.contains(note)) {
                startNotePlayback(note);
                mActiveNotes.add(note);
            }
        }
    }

    /**
     * Adds note to current playback.
     * @param toAdd note to add.
     */
    @Override
    public void add(int toAdd) {
        if (!mActiveNotes.contains(toAdd)) {
            startNotePlayback(toAdd);
            mActiveNotes.add(toAdd);
        }
    }

    /**
     * Removes notes from playback.
     * @param toRemove notes to remove.
     */
    @Override
    public void remove(int[] toRemove) {
        for (Integer note : toRemove) {
            if (mActiveNotes.contains(note)) {
                stopNotePlayback(note);
                mActiveNotes.remove(note);
            }
        }
    }

    /**
     * Removes note from playback.
     * @param toRemove note to remove.
     */
    @Override
    public void remove(int toRemove) {
        if (mActiveNotes.contains(toRemove)) {
            stopNotePlayback(toRemove);
            mActiveNotes.remove(toRemove);
        }
    }

    // Todo: Not sure if this needs to be a public method? I'll keep it here for now though.

    /**
     * Checks if target note is currently being synthesized.
     * @param target note to check.
     * @return true if note is being synthesized.
     */
    @Override
    public boolean noteIsActive(int target) {
        return mActiveNotes.contains(target);
    }

    /**
     * Gets current plugin.
     * @return current plugin.
     */
    @Override
    public int getPlugin() {
        return mPlugin;
    }

    /**
     * Sets current plugin.
     * If driver is active playback will update to the new plugin.
     * @param plugin new plugin.
     */
    @Override
    public void setPlugin(int plugin) {
        mPlugin = plugin;
        if (driverIsActive()) {
            // Needed to write plugin to midi driver.
            sendMidiSetup();
            refreshPlayback();
        }
    }

    /**
     * Gets current volume.
     * @return current volume.
     */
    @Override
    public int getVolume() {
        return mVolume;
    }

    /**
     * Sets current volume for midi driver.
     * If driver is active it will change update playback to the new volume.
     * @param volume
     */
    @Override
    public void setVolume(int volume) {
        mVolume = volume;
        if (driverIsActive()) {
            refreshPlayback();
        }
    }

    /**
     * Sets up midi. Also used when changing plugin.
     */
    private void sendMidiSetup() {
        byte[] message = new byte[2];
        message[0] = (byte) PROGRAM_CHANGE;
        message[1] = (byte) mPlugin;
        mDriver.write(message);
    }

    /**
     * Starts playback for note.
     * @param toStart note to start.
     */
    private void startNotePlayback(int toStart) {
        sendMessage(START, toStart, mVolume);
    }

    /**
     * Stops playback for notes.
     * @param toStop note to handleActivityChange.
     */
    private void stopNotePlayback(int toStop) {
        sendMessage(STOP, toStop, VOLUME_OFF);
    }

    /**
     * Method for playing or stopping notes.
     * Writes message to midi driver.
     * @param event start or handleActivityChange.
     * @param toSend note to send.
     * @param volume volume of message (is 0 for stopping notes).
     */
    private void sendMessage(int event, int toSend, int volume) {
        byte[] message = new byte[3];
        message[0] = (byte) event;
        message[1] = (byte) toSend;
        message[2] = (byte) volume;
        mDriver.write(message);
    }

    /**
     * Checks if driver is active.
     * @return boolean if driver not equal to null.
     */
    private boolean driverIsActive() {
        return mDriver != null;
    }

    /**
     * Stops and plays current notes.
     * If any attributes have changed this method will use any new changes.
     */
    private void refreshPlayback() {
        // Stop old notes.
        for (Integer note : mActiveNotes) {
            stopNotePlayback(note);
        }
        // Start new notes.
        for (Integer note : mActiveNotes) {
            startNotePlayback(note);
        }
    }

    /**
     * Stops current midi driver playback.
     * Stops and removes every note in set.
     */
    private void endPlayback() {
        for (Integer note : mActiveNotes) {
            stopNotePlayback(note);
            mActiveNotes.remove(note);
        }
    }
}
