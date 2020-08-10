package com.convergencelabstfx.keyfinder;

import java.util.ArrayList;
import java.util.List;

/**
 * The MusicTheory class contains static variables that help with automating the
 * creation of keys and indexing note values.
 */

public class MusicTheory {
    /**
     * Unicode for sharp sign.
     */
    public final static char SHARP = '\u266F';

    /**
     * Unicode for flat sign.
     */
    public final static char FLAT = '\u266d';

    /**
     * Unicode for natural sign.
     */
    public final static char NATURAL = '\u266e';

    /**
     * Intervals that make up the major scale.
     * Each number can be viewed as the semitone offset from the root.
     */
    public final static Integer[] MAJOR_SCALE_SEQUENCE = { 0, 2, 4, 5, 7, 9, 11 };

    /**
     * Intervals that make up the melodic minor scale.
     * Each number can be viewed as the semitone offset from the root.
     */
    public final static Integer[] MELODIC_MINOR_SCALE_SEQUENCE = { 0, 2, 3, 5, 7, 9, 11 };

    /**
     * Intervals pertaining to harmonic minor scale.
     * Indices represent semitones away from root.
     */
    public final static Integer[] HARMONIC_MINOR_SCALE_SEQUENCE = { 0, 2, 3, 5, 7, 8, 11 };

    public final static Integer[] HARMONIC_MAJOR_SCALE_SEQUENCE = { 0, 2, 4, 5, 7, 8, 11 };


    /**
     * Intervals that make up the Phrygian scale.
     */
    public final static int[] PHRYGIAN_SCALE_SEQUENCE = { 0, 1, 3, 5, 7, 8, 10 };

    /**
     * Intervals that make up the Dorian Flat2 scale.
     */
    public final static int[] DORIAN_FLAT2_SEQUENCE   = { 0, 1, 3, 5, 7, 9, 10 };

    /**
     * Intervals that make up a major triad.
     */
    public final static int[] MAJOR_TRAID_SEQUENCE    = { 0, 4, 7 };

    final static int SHARP_SPELLING_CODE = 1;
    final static int FLAT_SPELLING_CODE = 0;

    /**
     * Values correspond to whether or not a key should have sharp spelling.
     * true  = sharp
     * false = flat
     */
    final static int[] SPELLING_CODE = {
            SHARP_SPELLING_CODE, // C
            FLAT_SPELLING_CODE,  // Db
            SHARP_SPELLING_CODE, // D
            FLAT_SPELLING_CODE,  // Eb
            SHARP_SPELLING_CODE, // E
            FLAT_SPELLING_CODE,  // F
            FLAT_SPELLING_CODE,  // Gb
            SHARP_SPELLING_CODE, // G
            FLAT_SPELLING_CODE,  // Ab
            SHARP_SPELLING_CODE, // A
            FLAT_SPELLING_CODE,  // Bb
            SHARP_SPELLING_CODE, // B
    };

    /**
     * Names of all 12 tones used in western music.
     */
    public final static String[] CHROMATIC_SCALE_SHARP = {
            "C",
            "C" + SHARP,
            "D",
            "D" + SHARP,
            "E",
            "F",
            "F" + SHARP,
            "G",
            "G" + SHARP,
            "A",
            "A" + SHARP,
            "B"
    };

    public final static String[] CHROMATIC_SCALE_FLAT = {
            "C",
            "D" + FLAT,
            "D",
            "E" + FLAT,
            "E",
            "F",
            "G" + FLAT,
            "G",
            "A" + FLAT,
            "A",
            "B" + FLAT,
            "B"
    };

    /**
     * Names of modes in major scale.
     */
    public final static String[] MAJOR_MODE_NAMES = {
            "Ionian",
            "Dorian",
            "Phrygian",
            "Lydian",
            "Mixolydian",
            "Aeolian",
            "Locrian"
    };

    /**
     * Names of modes in melodic minor scale.
     */
    public final static String[] MELODIC_MINOR_MODE_NAMES = {
            "Melodic Minor",
            "Phrygian " + SHARP + '6',
            "Lydian Augmented",
            "Lydian " + FLAT + '7',
            "Mixolydian " + FLAT + '6',
            "Locrian " + SHARP + '2',
            "Altered"
    };

    /**
     * Names of harmonic minor modes.
     */
    public final static String[] HARMONIC_MINOR_MODE_NAMES = {
            "Harmonic Minor",
            "Locrian " + NATURAL + '6',
            "Ionian " + SHARP + '5',
            "Dorian " + SHARP + '4',
            "Phrygian Dominant",
            "Lydian " + SHARP + '9',
            "Altered Diminished"
    };

    public final static String[] HARMONIC_MAJOR_MODE_NAMES = {
            "Harmonic Major",
            "Dorian " + FLAT + '5',
            "Phrygian " + FLAT + '4',
            "Lydian " + FLAT + '3',
            "Mixolydian " + FLAT + '2',
            "Lydian Augmented " + SHARP + '2',
            "Locrian" + FLAT + FLAT + '7'
    };

    /**
     * Total number of unique tones in western music.
     */
    public final static int TOTAL_NOTES = 12;

    /**
     * Number of notes in a diatonic scale.
     */
    public final static int DIATONIC_SCALE_SIZE = 7;

    /**
     * Names of Parent scales.
     */
    public final static String[] PARENT_SCALE_NAMES = {
            "Major",
            "Melodic Minor"
    };

    // TODO: figure out what this does
    public static int getLowestIx(int rootIx, int min) {
        int lowest = ((min / MusicTheory.TOTAL_NOTES) * MusicTheory.TOTAL_NOTES) + (rootIx % MusicTheory.TOTAL_NOTES);
        if ((min % MusicTheory.TOTAL_NOTES) > (rootIx % MusicTheory.TOTAL_NOTES)) {
            lowest += MusicTheory.TOTAL_NOTES;
        }
        return lowest;
    }

    /**
     * Transposes intervals to the mode given by the ix.
     */
    public static List<Integer> getModeIntervals(List<Integer> intervals, int modeIx) {
        if (intervals.get(0) != 0) {
            throw new IllegalArgumentException(
                    "Cannot use scale when first interval is not zero.\n" +
                            "Intervals = " + intervals.toString());
        }
        // No need to change anything
        if (modeIx == 0) {
            return intervals;
        }
        final List<Integer> toReturn = new ArrayList<>(intervals.size());
        final int offset = intervals.get(modeIx);
        for (int i = modeIx; i < intervals.size(); i++) {
            toReturn.add(intervals.get(i) - offset);
        }
        for (int i = 0; i < modeIx; i++) {
            toReturn.add(intervals.get(i) + TOTAL_NOTES - offset);
        }
        return toReturn;
    }

}
