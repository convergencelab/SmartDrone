package com.example.smartdrone;

public class Constants {
    // Used for accessing note names.
    public static final String[] NOTES =
            { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

    public static final String MESSAGE_LOG_ADD        = "mainActivityDebugAdd";
    public static final String MESSAGE_LOG_REMOVE     = "mainActivityDebugRemove";
    public static final String MESSAGE_LOG_LIST       = "mainActivityDebugList";
    public static final String MESSAGE_LOG_SPEED      = "mainActivityDebugSpeed";
    public static final String MESSAGE_LOG_NOTE_TIMER = "mainActivityDebugNTimer";

    public static final int START_NOTE = 0X90;
    public static final int STOP_NOTE  = 0X80;
}
