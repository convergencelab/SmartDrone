package com.example.smartdrone.Utility;

import android.util.Log;

public class DroneLog {
    private static final String NOTE_LIST = "note_list";
    private static final String NOTE_ADD = "note_add";
    private static final String NOTE_REMOVE = "note_remove";
    private static final String NOTE_THREADS = "note_threads";

    public static void noteList(String noteString) {
        Log.d(NOTE_LIST, noteString);
    }

    public static void noteAdded(String noteName) {
        Log.d(NOTE_ADD, noteName);
    }

    public static void noteRemoved(String noteName) {
        Log.d(NOTE_REMOVE, noteName);
    }

    public static void noteThreads(int threadCount) {
        Log.d(NOTE_THREADS, Integer.toString(threadCount));
    }
}
