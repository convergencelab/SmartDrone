package com.example.smartdrone;

import java.util.ArrayList;

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
    public static final String MESSAGE_LOG_ADD        = "note_add";
    public static final String MESSAGE_LOG_REMOVE     = "note_remove";
    public static final String MESSAGE_LOG_LIST       = "note_list";
    public static final String MESSAGE_LOG_SPEED      = "process_speed";
    public static final String MESSAGE_LOG_NOTE_TIMER = "note_timer";
    public static final String MESSAGE_LOG_VOICING    = "voicing_test";
    public static final String MESSAGE_LOG_PREF       = "drone_pref";
    public static final String MESSAGE_LOG_ACTV       = "drone_lifecycle";
    public static final String DEBUG_TAG              = "drone_debug";

    // Midi driver Constants
    public static final int START_NOTE     = 0X90;
    public static final int STOP_NOTE      = 0X80;
    public static final int PROGRAM_CHANGE = 0XC0;
    public static final int VOLUME_OFF     = 0;

    // Plugin Constants
    private static final int PLUGIN_CHOIR    = 52;
    private static final int PLUGIN_CHOIR_2 = 53;
    private static final int BRASS_SECTION  = 61;
    private static final int STRING_SECTION = 48;
    public static final int[] PLUGIN_INDICES  = {
            PLUGIN_CHOIR,
            PLUGIN_CHOIR_2,
            BRASS_SECTION,
            STRING_SECTION,
    };
    public static final String[] PLUGIN_NAMES = {
            "Choir 1",
            "Choir 2",
            "Brass",
            "Strings"
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

    /**
     * Flattened strings of default voicing templates.
     */
    public static final String[] DEFAULT_TEMPLATES = {
            "Drone,0",
            "Triad (Closed),0,2,4",
            "Triad (Open),0,4,9",
            "Drop II,0,4,6,9"
    };

}
