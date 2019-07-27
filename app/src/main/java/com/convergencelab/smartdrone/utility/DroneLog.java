package com.convergencelab.smartdrone.utility;

import android.util.Log;

public class DroneLog {
    /* KeyFinder log keys */
    private static final String NOTE_LIST = "note_list";
    private static final String NOTE_ADD = "note_add";
    private static final String NOTE_REMOVE = "note_remove";
    private static final String NOTE_THREADS = "note_threads";
    private static final String GENERAL_DEBUG = "general_debug";

    /* Activity and Lifecycle log keys */
    private static final String ACTIVITY_LIFECYCLE = "activity_lifecycle";

    /**
     * Log the list of active notes in the KeyFinder class.
     * @param       noteString String; string of notes.
     */
    public static void noteList(String noteString) {
        try {
            Log.d(NOTE_LIST, noteString);
        }
        catch(java.util.ConcurrentModificationException exception) {
            System.out.println(exception.toString());
        }
    }

    /**
     * Log note that was added to active note list.
     * @param       noteName String; name of note added.
     */
    public static void noteAdded(String noteName) {
        Log.d(NOTE_ADD, noteName);
    }

    /**
     * Log note that was removed from active note list.
     * @param       noteName String; name of note removed.
     */
    public static void noteRemoved(String noteName) {
        Log.d(NOTE_REMOVE, noteName);
    }

    /**
     * Log number of note threads active.
     * @param       threadCount int; number of active note threads.
     */
    public static void noteThreads(int threadCount) {
        Log.d(NOTE_THREADS, Integer.toString(threadCount));
    }

    /**
     * Log the activity and the lifecycle method being called.
     * @param       activity String; name of activity the method is called from.
     * @param       lifecycle String; name of lifecycle method being called.
     */
    public static void activityLifecycle(String activity, String lifecycle) {
        Log.d(ACTIVITY_LIFECYCLE, activity + ": " + lifecycle);
    }

    public static void debugLog(String toShow) {
        Log.d(GENERAL_DEBUG, toShow);
    }
}
