package com.example.smartdrone;

public class Constants {
    /**
     * Requested sample rate.
     */
    public static final int SAMPLE_RATE = 22050;

    /**
     * Size of the audio buffer (in samples).
     */
    public static final int AUDIO_BUFFER_SIZE = 1024;

    /**
     * Size of the overlap (in samples).
     */
    public static final int BUFFER_OVERLAP = 0;

    /**
     * Names of all 12 notes using sharps.
     */
    public static final String[] notes =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    /**
     * Names of all 12 notes using flats.
     */
    public static final String[] notesFlat =
            { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
}
