package com.convergencelabstfx.keyfinder.harmony;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class VoicingTemplate {

    private List<Tone> mBassTones = new ArrayList<>();

    private List<Tone> mChordTones = new ArrayList<>();

    public VoicingTemplate() {

    }

    // todo: has to be binary insertion
    public void addBassTone(int degree) {

    }

    public void removeBassTone(int degree) {
        mBassTones.indexOf(degree);
    }

    // todo: has to be binary insertion
    public void addChordTone(int degree) {

    }

    public void removeChordTone(int degree) {

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
        return super.toString();
    }
}
