package com.convergencelabstfx.smartdrone.models;


import com.convergencelabstfx.keyfinder.MusicTheory;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class VoicingConstructor {

    public static int MIN_BOUND = 0;

    public static int MAX_BOUND = 128;

    private List<Integer> mCurVoicing = null;

    private List<Integer> mMode = null;

    private int mKey = -1;

    private VoicingBounds mBounds = null;

    private VoicingTemplate mTemplate = null;

    public VoicingConstructor() {

    }

    public List<Integer> makeVoicing() {
        // todo: implement voicing making logic
        if (mMode.size() == 0) {
            throw new IllegalStateException("Attempted to make voicing with empty mode.");
        }

        final List<Integer> voicing = new ArrayList<>(mTemplate.size());
        voicing.addAll(constructNotes(mTemplate.getBassTones(), mBounds.getBassLower(), mBounds.getBassUpper()));
        voicing.addAll(constructNotes(mTemplate.getChordTones(), mBounds.getChordLower(), mBounds.getChordUpper()));

        mCurVoicing = voicing;
        Timber.i("chord:" + mCurVoicing.toString());
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

    public void setBounds(VoicingBounds bounds) {
        mBounds = bounds;
    }

    public VoicingBounds getBounds() {
        return mBounds;
    }

    private List<Integer> constructNotes(List<Integer> degrees, int lowerBound, int upperBound) {
        if (degrees.isEmpty()) {
            return new ArrayList<>();
        }
        final List<Integer> notes = new ArrayList<>(degrees.size());
        int octaveOffset = (int) Math.ceil( ((double) (lowerBound - mKey)) / ((double) MusicTheory.TOTAL_NOTES) );

        for (int i = 0; i < degrees.size(); i++) {
            int note = mMode.get(degrees.get(i) % mMode.size());
            note += (degrees.get(i) / mMode.size()) * MusicTheory.TOTAL_NOTES;
            note += mKey;
            note += octaveOffset * MusicTheory.TOTAL_NOTES;
            notes.add(note);
        }
        return notes;
    }

}
