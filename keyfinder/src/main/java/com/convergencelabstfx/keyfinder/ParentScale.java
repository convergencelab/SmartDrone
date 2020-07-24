package com.convergencelabstfx.keyfinder;

import java.util.Collections;
import java.util.List;

@Deprecated
public class ParentScale {

    private final String mName;

    private final List<Integer> mIntervals;

    private final List<String> mModeNames;

    public ParentScale(String name, List<Integer> intervals, List<String> modeNames) {
        this.mName = name;
        this.mIntervals = intervals;
        this.mModeNames = modeNames;
    }

    public String getName() {
        return mName;
    }

    public List<Integer> getParentIntervals() {
        return Collections.unmodifiableList(mIntervals);
    }

//    public List<Integer> getIntervalsForMode() {
//
//    }
//
//    public String[] getModeNameAt(int ix) {
////        return mModeNames;
//    }

}
