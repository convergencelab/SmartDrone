package com.convergencelabstfx.smartdrone.models;


import androidx.annotation.NonNull;

import com.convergencelabstfx.keyfinder.MusicTheory;
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

    public int addParentScale(@NonNull List<Integer> parentScaleIntervals, @NonNull List<String> modeNames) {
        if (modeNames.contains(null) || modeNames.size() != parentScaleIntervals.size()) {
            throw new IllegalArgumentException(
                    "modeNames cannot contain 'null' and must match the size of parent scale intervals."
            );
        }
        for (Integer interval : parentScaleIntervals) {
            if (interval < 0 || interval > 11) {
                throw new IllegalArgumentException(
                    "All intervals in parent scale must be between " +
                    "0 (inclusive) and 11 (inclusive)."
                );
            }
        }

        final List<Scale> mModes = new ArrayList<>();

        for (int i = 0; i < parentScaleIntervals.size(); i++) {
            final List<Integer> curDegrees = MusicTheory.getModeIntervals(parentScaleIntervals, i);
            final Scale scale = new Scale(modeNames.get(i), curDegrees);
            mModes.add(scale);
        }

        final int scaleId = getNewId();
        mScaleMap.put(scaleId, mModes);
        return scaleId;
    }

    public void removeParentScale(int parentScaleId) {
        mScaleMap.remove(parentScaleId);
    }

    public Scale getMode(int parentScaleId, int modeIx) {
        return mScaleMap.get(parentScaleId).get(modeIx);
    }

    private int getNewId() {
        final int newId = mCurId;
        mCurId++;
        return newId;
    }

}
