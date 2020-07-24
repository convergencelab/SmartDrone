package com.convergencelabstfx.smartdrone.models;


import com.convergencelabstfx.keyfinder.Scale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo: might move this class and ChordConstructor to be a part of the KeyFinder package
// todo: this class could probably even be static, but oh well
class ScaleConstructor {

    private int mCurId = 0;

    private Map<Integer, List<Scale>> mScaleMap = new HashMap<>();

    public ScaleConstructor() {

    }

    public int addParentScale(List<Integer> parentScaleIntervals, List<String> modeNames) {
        if (modeNames != null && modeNames.size() != parentScaleIntervals.size()) {
            throw new IllegalArgumentException(
                    "If mode names is not null then the size of " +
                    "modeNames must be equal to the size of parentScaleIntervals."
            );
        }

        final List<Scale> mModes = new ArrayList<>();
        final int scaleId = mCurId;
        mCurId++;


        // todo: make scale object for each mode in scale


        mScaleMap.put(scaleId, mModes);
        return scaleId;
    }

    public void removeParentScale(int parentScaleId) {
        mScaleMap.remove(parentScaleId);
    }

    public Scale getMode(int parentScaleId, int modeIx) {
        return mScaleMap.get(parentScaleId).get(modeIx);
    }

}
