package com.convergencelabstfx.smartdrone;

import com.example.smartdrone.Key;
import com.example.smartdrone.MusicTheory;
import com.example.smartdrone.Note;

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
    public static final String[] NOTES_SHARP = MusicTheory.CHROMATIC_SCALE_SHARP;

    /**
     * Names of all 12 notes using flats.
     */
    public static final String[] NOTES_FLAT  = MusicTheory.CHROMATIC_SCALE_FLAT;

    /**
     * Names of all 12 notes using flats.
     */
    public static final String[] notesFlat =
            { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };

    // Message Log Constants
    public static final String MESSAGE_LOG_ACTV       = "drone_lifecycle";

    // Midi driver Constants
    public static final int START_NOTE     = 0X90;
    public static final int STOP_NOTE      = 0X80;
    public static final int PROGRAM_CHANGE = 0XC0;
    public static final int VOLUME_OFF     = 0;

    // Plugin Constants
    private static final int STRING_SECTION = 48;
    private static final int PLUGIN_CHOIR   = 52;
    private static final int BRASS_SECTION  = 61;
    public static final int[] PLUGIN_INDICES  = {
            STRING_SECTION,
            PLUGIN_CHOIR,
            BRASS_SECTION,
    };
    public static final String[] PLUGIN_NAMES = {
            "Strings",
            "Choir",
            "Brass"
    };

    // Default Constants
    public static final int NOTE_FILTER_LENGTH_DEFAULT = 60;
    public static final int KEY_SENS_DEFAULT           = 3;
    public static final int NOTE_TIMER_LEN             = 2;

    /**
     * Note index for null.
     * Used when microphone has not detected a note.
     */
    public static final int NULL_NOTE_IX = -1;

    /**
     * Key index for null.
     */
    public static final int NULL_KEY_IX = -1;

    public static final Note NULL_NOTE = null;
    public static final Key NULL_KEY = null;

    /**
     * Flattened strings of default voicing templates.
     */
    public static final String DEFAULT_TEMPLATE_LIST =
            "Drone,0,4|Triad (Closed),0,2,4|Triad (Open),0,4,9|Drop II,0,4,6,9|Holdsworth,2,7,8,11";

    public static final String DEFAULT_TEMPLATE =
            "Drone,0,4";

}
