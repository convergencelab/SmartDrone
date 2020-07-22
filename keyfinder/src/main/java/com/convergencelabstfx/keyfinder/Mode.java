package com.convergencelabstfx.keyfinder;

import java.util.Arrays;
import java.util.Objects;

public class Mode {

//    // Implementation will setup intervals and name
//    protected abstract void setup();

    protected final int ix;

    private int[] intervals;

    private final String name;

    // TODO: come back to this function
    public Mode(int[] parentScale, int ix) {
        this.ix = ix;
        name = MusicTheory.MELODIC_MINOR_MODE_NAMES[this.ix % MusicTheory.TOTAL_NOTES];

        // Build intervals for mode
        intervals = new int[MusicTheory.DIATONIC_SCALE_SIZE];
        final int offset = MusicTheory.MELODIC_MINOR_SCALE_SEQUENCE[ix % MusicTheory.TOTAL_NOTES];
        for (int i = 0; i < MusicTheory.DIATONIC_SCALE_SIZE; i++) {
            int curInterval = MusicTheory.MELODIC_MINOR_SCALE_SEQUENCE[(i + ix) % MusicTheory.DIATONIC_SCALE_SIZE] - offset;
            if (curInterval < 0) {
                curInterval += MusicTheory.TOTAL_NOTES;
            }
            intervals[i] = curInterval;
        }
//        setup();
    }

    public int getIx() {
        return ix;
    }

    public int[] getIntervals() {
        return intervals;
    }

    public String getName() {
        return name;
    }

    // Method will accommodate for degrees that are greater than a 7th
    public int getInterval(int degree) {
        return (intervals[degree % MusicTheory.DIATONIC_SCALE_SIZE] % MusicTheory.TOTAL_NOTES)
                + ((degree / MusicTheory.DIATONIC_SCALE_SIZE) * MusicTheory.TOTAL_NOTES);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mode mode = (Mode) o;
        return ix == mode.ix &&
                Arrays.equals(intervals, mode.intervals);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ix);
        result = 31 * result + Arrays.hashCode(intervals);
        return result;
    }
}
