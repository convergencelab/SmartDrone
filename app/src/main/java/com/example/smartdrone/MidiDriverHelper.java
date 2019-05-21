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
}
