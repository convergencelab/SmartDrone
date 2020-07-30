package com.convergencelabstfx.smartdrone.models;


import com.convergencelabstfx.keyfinder.MusicTheory;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChordConstructor {

    public static int MIN_BOUND = 0;

    public static int MAX_BOUND = 128;

    private List<Integer> mCurVoicing = null;

    private List<Integer> mMode = null;

    private int mKey = -1;

    private int mBassLowerBound = -1;

    private int mBassUpperBound = -1;

    private int mChordLowerBound = -1;

    private int mChordUpperBound = -1;

    private VoicingTemplate mTemplate = null;

    public ChordConstructor() {

    }

    public List<Integer> makeVoicing() {
        // todo: implement voicing making logic
         if (mMode.size() == 0) {
            throw new IllegalStateException("Attempted to make voicing with empty mode.");
        }

        final List<Integer> voicing = new ArrayList<>(mTemplate.size());
        voicing.addAll(constructNotes(mTemplate.getBassTones(), mBassLowerBound, mBassUpperBound));
        voicing.addAll(constructNotes(mTemplate.getChordTones(), mChordLowerBound, mChordUpperBound));

        mCurVoicing = voicing;

        return voicing;
    }

    public List<Integer> getCurVoicing() {
        return mCurVoicing;
    }

    public List<Integer> getMode() {
        return mMode;
    }

    public void setMode(List<Integer> mode) {
        mMode = mode;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        if (key < 0 || key > 11) {
            throw new IllegalArgumentException("Parameter 'key' must be between 0 (inclusive) and 11 (inclusive).");
        }
        mKey = key;
    }

    public VoicingTemplate getTemplate() {
        return mTemplate;
    }

    public void setTemplate(VoicingTemplate template) {
        mTemplate = template;
    }

    public void setBounds(
            int bassLowerBound,
            int bassUpperBound,
            int chordLowerBound,
            int chordUpperBound) {
        mBassLowerBound = bassLowerBound;
        mBassUpperBound = bassUpperBound;
        mChordLowerBound = chordLowerBound;
        mChordUpperBound = chordUpperBound;
    }

    public int getBassLowerBound() {
        return mBassLowerBound;
    }

    public void setBassLowerBound(int bassLowerBound) {
        mBassLowerBound = bassLowerBound;
    }

    public int getBassUpperBound() {
        return mBassUpperBound;
    }

    public void setBassUpperBound(int bassUpperBound) {
        mBassUpperBound = bassUpperBound;
    }

    public int getChordLowerBound() {
        return mChordLowerBound;
    }

    public void setChordLowerBound(int chordLowerBound) {
        mChordLowerBound = chordLowerBound;
    }

    public int getChordUpperBound() {
        return mChordUpperBound;
    }

    public void setChordUpperBound(int chordUpperBound) {
        mChordUpperBound = chordUpperBound;
    }

    private List<Integer> constructNotes(List<Integer> degrees, int lowerBound, int upperBound) {
        if (degrees.isEmpty()) {
            return new ArrayList<>();
        }
        final List<Integer> notes = new ArrayList<>(degrees.size());

        /*
        int octaveOffset =
                (lowerBound + ((mMode.get(degrees.get(0) % mMode.size())
                        + (degrees.get(0) / mMode.size()) * MusicTheory.TOTAL_NOTES) + mKey))
                        / MusicTheory.TOTAL_NOTES;

//        if (((lowerBound + ((mMode.get(degrees.get(0) % mMode.size())
//                + (degrees.get(0) / mMode.size()) * MusicTheory.TOTAL_NOTES) + mKey))
//                / MusicTheory.TOTAL_NOTES) != 0) {
//            octaveOffset += 1;
//        }

         */

        for (int i = 0; i < degrees.size(); i++) {
            int note = mMode.get(degrees.get(i) % mMode.size());
            note += (degrees.get(i) / mMode.size()) * MusicTheory.TOTAL_NOTES;
            note += mKey;
            notes.add(note);
        }

        // todo: this is done iteratively because I haven't figured out the correct math
        //       and speed isn't really a problem here
        boolean offsetFound = false;
        while (!offsetFound) {
            for (int i = 0; i < notes.size(); i++) {
                notes.set(i, notes.get(i) + MusicTheory.TOTAL_NOTES);
            }
            // todo: implement the other half of this condition later
            if (notes.get(0) >= lowerBound /* && notes.get(notes.size() - 1) <=  upperBound */) {
                offsetFound = true;
            }
        }

        return notes;
    }

}
