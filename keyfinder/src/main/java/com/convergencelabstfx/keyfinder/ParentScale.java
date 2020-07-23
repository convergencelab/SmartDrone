package com.convergencelabstfx.keyfinder;

public class ParentScale {

    private final String name;

    private final int[] intervals;

    private final String[] modeNames;

    public ParentScale(String name, int[] intervals, String[] modeNames) {
        this.name = name;
        this.intervals = intervals;
        this.modeNames = modeNames;
    }

    public String getName() {
        return name;
    }

    public int[] getIntervals() {
        return intervals;
    }

    public String[] getModeNameAt(int ix) {
        return modeNames;
    }

}
