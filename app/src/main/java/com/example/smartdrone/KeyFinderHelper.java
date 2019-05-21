package com.example.smartdrone;

public class KeyFinderHelper {
    private static KeyFinder keyFinder = new KeyFinder();

    private static int prevActiveKeyIx = -1;
    private static int curActiveKeyIx = -1;
    private static int prevAddedNoteIx = -1;
    private static int curNoteIx = -1;

    private static int noteTimerLength;
    private static int keyTimerLength;

    public static KeyFinder getKeyFinder() {
        return keyFinder;
    }

    public static int getPrevActiveKeyIx() {
        return prevActiveKeyIx;
    }

    public static void setPrevActiveKeyIx(int newIx) {
        prevActiveKeyIx = newIx;
    }

    public static int getCurActiveKeyIx() {
        return curActiveKeyIx;
    }

    public static void setCurActiveKeyIx(int newIx) {
        curActiveKeyIx = newIx;
    }

    public static int getPrevAddedNoteIx() {
        return prevAddedNoteIx;
    }

    public static void setPrevAddedNoteIx(int newIx) {
        prevAddedNoteIx = newIx;
    }

    public static int getCurNoteIx() {
        return curNoteIx;
    }

    public static void setCurNoteIx(int newIx) {
        curNoteIx = newIx;
    }

    public static int getNoteTimerLength() {
        return noteTimerLength;
    }

    public static void setNoteTimerLength(int seconds) {
        noteTimerLength = seconds;
    }

    public static int getKeyTimerLength() {
        return keyTimerLength;
    }

    public static void setKeyTimerLength(int seconds) {
        keyTimerLength = seconds;
    }
}
