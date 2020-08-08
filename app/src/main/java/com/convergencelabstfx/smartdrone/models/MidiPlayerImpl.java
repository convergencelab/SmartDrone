package com.convergencelabstfx.smartdrone.models;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// todo: look into the SF2 midi lib on github
//       https://github.com/rodydavis/MidiDriver-Android-SF2
public class MidiPlayerImpl implements MidiPlayer {

    private static final int START = 0X90;

    private static final int STOP = 0X80;

    private static final int PROGRAM_CHANGE = 0XC0;

    private static final int VOLUME_OFF = 0;

    private static final int DEFAULT_VOLUME = 65;

    private MidiDriver mDriver = new MidiDriver();

    private Set<Integer> mActiveNotes = new HashSet<>();

    private int mVolume = DEFAULT_VOLUME;

    private int mPlugin = -1;

    public MidiPlayerImpl() {

    }

    @Override
    public void start() {
        mDriver.start();
        sendMidiSetup();

    }

    @Override
    public void stop() {
        clear();
        mDriver.stop();
    }

    // todo:
    @Override
    public boolean isRunning() {
        return false;
    }

    // todo:
    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public void playNote(int note) {
        if (!noteIsActive(note)) {
            noteOn(note);
            mActiveNotes.add(note);
        }
    }

    @Override
    public void stopNote(int note) {
        if (noteIsActive(note)) {
            noteOff(note);
            mActiveNotes.remove(note);
        }
    }

    @Override
    public void playChord(List<Integer> notes) {
        for (Integer note : notes) {
            playNote(note);
        }
    }

    @Override
    public void stopChord(List<Integer> notes) {
        for (Integer note : notes) {
            stopNote(note);
        }
    }

    @Override
    public void clear() {
        for (Integer note : mActiveNotes) {
            noteOff(note);
        }
        mActiveNotes.clear();
    }

    @Override
    public boolean noteIsActive(int note) {
        return mActiveNotes.contains(note);
    }

    @Override
    public boolean hasActiveNotes() {
        return !mActiveNotes.isEmpty();
    }

    @Override
    public int getPlugin() {
        return mPlugin;
    }

    @Override
    public void setPlugin(int plugin) {
        if (plugin != mPlugin) {
            mPlugin = plugin;
            if (mDriver != null) {
                // Need to write plugin to midi driver.
                sendMidiSetup();
                if (hasActiveNotes()) {
                    refreshPlayback();
                }
            }
        }
    }

    @Override
    public int getVolume() {
        return mVolume;
    }

    @Override
    public void setVolume(int volume) {
        if (volume != mVolume) {
            mVolume = volume;
            if (mDriver != null && hasActiveNotes()) {
                refreshPlayback();
            }
        }
    }

    @Override
    public void mute() {
        setVolume(VOLUME_OFF);
    }

    @Override
    public void unMute() {
        setVolume(mVolume);
    }

    /*
     * noteOn and noteOff exist because they don't have the side effect of adding and removing notes
     * to mActiveNotes.
     * Methods refreshPlayback and clear require this to avoid throwing ConcurrentModificationException()
     */
    private void noteOn(int note) {
        sendMessage(START, note, mVolume);
    }

    private void noteOff(int note) {
        sendMessage(STOP, note, mVolume);
    }

    private void sendMidiSetup() {
        byte[] message = new byte[2];
        message[0] = (byte) PROGRAM_CHANGE;
        message[1] = (byte) mPlugin;
        mDriver.write(message);
    }

    private void sendMessage(int event, int toSend, int volume) {
        byte[] message = new byte[3];
        message[0] = (byte) event;
        message[1] = (byte) toSend;
        message[2] = (byte) volume;
        mDriver.write(message);
    }

    private void refreshPlayback() {
        for (Integer note : mActiveNotes) {
            noteOff(note);
        }
        for (Integer note : mActiveNotes) {
            noteOn(note);
        }
    }

}