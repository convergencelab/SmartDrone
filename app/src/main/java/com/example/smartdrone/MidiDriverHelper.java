package com.example.smartdrone;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.List;

public class MidiDriverHelper
    // implements MidiDriver.OnMidiStartListener
{
    private static MidiDriver midiDriver = new MidiDriver();

    /* List of all the plugins available */
    /* https://github.com/billthefarmer/mididriver/blob/master/library/src/main/java/org/billthefarmer/mididriver/GeneralMidiConstants.java */
    //TODO: Add user parameter.
    private static int plugin = 52;

    private static int volume = 65;

    public static MidiDriver getMidiDriver() {
        return midiDriver;
    }

    public static int getPlugin() {
        return plugin;
    }

    public static int getVolume() {
        return volume;
    }

    public static void incrementVolume() {
        volume = (volume + 5) % 105;
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Send data that is to be synthesized by midi driver.
     * @param       event int; type of event.
     * @param       midiKey int; index of note (uses octaves).
     * @param       volume int; volume of note.
     */
    public static void sendMidi(int event, int midiKey, int volume) {
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
    public static void sendMidiChord(int event, int[] midiKeys, int volume, int rootIx) {
        int octaveAdjustment = 0;
        if (midiKeys[0] + rootIx > 47) {
            octaveAdjustment = -12;
        }

        for (int key : midiKeys) {
            sendMidi(event, key + rootIx + octaveAdjustment, volume);
        }
    }

    /**
     * https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
     *
     * Initial setup data for midi.
     */
    public static void sendMidiSetup() {
        byte msg[] = new byte[2];
        msg[0] = (byte) 0XC0;    // 0XC0 == PROGRAM CHANGE
        msg[1] = (byte) MidiDriverHelper.getPlugin();
        MidiDriverHelper.getMidiDriver().write(msg);
    }
}
