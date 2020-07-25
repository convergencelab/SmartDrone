package com.convergencelabstfx.keyfinder.harmony;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoicingTemplate {

    private List<Integer> mBassTones = new ArrayList<>();
    private List<Integer> mChordTones = new ArrayList<>();

    public VoicingTemplate() {

    }

    public void addBassTone(int degree) {
        binaryInsertion(mBassTones, degree);
    }

    public void removeBassTone(int degree) {
        binaryRemoval(mBassTones, degree);
    }

    public void addChordTone(int degree) {
        binaryInsertion(mChordTones, degree);
    }

    public void removeChordTone(int degree) {
        binaryRemoval(mChordTones, degree);
    }

    public List<Integer> getBassTones() {
        return mBassTones;
    }

    public void setBassTones(List<Integer> bassTones) {
        mBassTones = bassTones;
    }

    public List<Integer> getChordTones() {
        return mChordTones;
    }

    public void setChordTones(List<Integer> chordTones) {
        mChordTones = chordTones;
    }

    public int size() {
        return mBassTones.size() + mChordTones.size();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Integer tone : mBassTones) {
            sb.append(tone);
            sb.append(" ");
        }
        sb.append("] ");

        sb.append("[ ");
        for (Integer tone : mChordTones) {
            sb.append(tone);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    private void binaryInsertion(List<Integer> list, int tone) {
        final int ix = Collections.binarySearch(list, tone);
        if (ix < 0) {
            final int addIx = -(ix + 1);
            list.add(addIx, tone);
        }
    }

    private void binaryRemoval(List<Integer> list, int tone) {
        final int ix = Collections.binarySearch(list, tone);
        if (ix >= 0) {
            list.remove(ix);
        }
    }

}
