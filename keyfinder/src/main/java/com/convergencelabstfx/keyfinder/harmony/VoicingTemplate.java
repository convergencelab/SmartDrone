package com.convergencelabstfx.keyfinder.harmony;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoicingTemplate {

    private List<Tone> mBassTones = new ArrayList<>();

    private List<Tone> mChordTones = new ArrayList<>();

    public VoicingTemplate() {

    }

    public void addBassTone(int degree) {
        binaryInsertion(mBassTones, new Tone(degree));
    }

    public void removeBassTone(int degree) {
        binaryRemoval(mBassTones, new Tone(degree));
    }

    public void addChordTone(int degree) {
        binaryInsertion(mChordTones, new Tone(degree));
    }

    public void removeChordTone(int degree) {
        binaryRemoval(mChordTones, new Tone(degree));
    }

    public List<Tone> getBassTones() {
        return mBassTones;
    }

    public void setBassTones(List<Tone> bassTones) {
        mBassTones = bassTones;
    }

    public List<Tone> getChordTones() {
        return mChordTones;
    }

    public void setChordTones(List<Tone> chordTones) {
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
        for (Tone tone : mBassTones) {
            sb.append(tone.getDegree());
            sb.append(" ");
        }
        sb.append("] ");

        sb.append("[ ");
        for (Tone tone : mChordTones) {
            sb.append(tone.getDegree());
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    private void binaryInsertion(List<Tone> list, Tone tone) {
        final int ix = Collections.binarySearch(list, tone);
        if (ix < 0) {
            final int addIx = -(ix + 1);
            list.add(addIx, tone);
        }
    }

    private void binaryRemoval(List<Tone> list, Tone tone) {
        final int ix = Collections.binarySearch(list, tone);
        if (ix >= 0) {
            list.remove(ix);
        }
    }
}
