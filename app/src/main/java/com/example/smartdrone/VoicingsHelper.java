package com.example.smartdrone;

public class VoicingsHelper {
    private static int userVoicingIx = 0;

    private static int[] drone             = { 0                     };
    private static int[] majorTriad        = { 0,  7, 16             };
    private static int[] maj7Voicing       = { 0,  7, 16, 23, 26     };
    private static int[] gabeVoicing       = { 2, 12, 17, 23         };
    private static int[] lydianVoicing     = { 5, 12, 19, 26, 33, 40 };
    private static int[] mixolydianVoicing = { 7, 17, 21, 24, 28, 33 };
    private static int[] phrygianVoicing   = { 4, 17, 21, 23, 28     };

    private static int[][] voicings = {
            drone,
            majorTriad,
            maj7Voicing,
            gabeVoicing,
            lydianVoicing,
            mixolydianVoicing,
            phrygianVoicing};

    private static String[] userVoicingName = {
            "Drone",
            "Major Triad",
            "Major9",
            "Gabe",
            "Lydian",
            "Mixolydian",
            "Phrygian", };

    public static int getUserVoicingIx() {
        return userVoicingIx;
    }

    public static void setUserVoicingIx(int newIx) {
        userVoicingIx = newIx;
    }

    public static int[] getVoicingAtIx(int ix) {
        return voicings[ix];
    }

    public static int[] getCurVoicing() {
        return voicings[userVoicingIx];
    }

    public static String getNameAtIx(int ix) {
        return userVoicingName[ix];
    }

    public static String getCurVoicingName() {
        return userVoicingName[userVoicingIx];
    }

    public static void incrementIx() {
        userVoicingIx = (userVoicingIx + 1) % voicings.length;
    }
}
