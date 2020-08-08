package com.convergencelabstfx.smartdrone.models;


import com.convergencelabstfx.keyfinder.MusicTheory;
import com.convergencelabstfx.keyfinder.ParentScale;
import com.convergencelabstfx.keyfinder.Scale;

import java.util.ArrayList;
import java.util.List;

// todo: might move this class and ChordConstructor to be a part of the KeyFinder package
// todo: this class could probably even be static, but oh well
public class ScaleConstructor {

    public static ParentScale makeParentScale(
            String parentName,
            List<Integer> parentIntervals,
            List<String> modeNames) {
        if (modeNames.contains(null) || modeNames.size() != parentIntervals.size()) {
            throw new IllegalArgumentException(
                    "modeNames cannot contain 'null' and must match the size of parent scale intervals."
            );
        }
        for (Integer interval : parentIntervals) {
            if (interval < 0 || interval > 11) {
                throw new IllegalArgumentException(
                        "All intervals in parent scale must be between " +
                                "0 (inclusive) and 11 (inclusive)."
                );
            }
        }

        final List<Scale> mModes = new ArrayList<>();
        for (int i = 0; i < parentIntervals.size(); i++) {
            final List<Integer> curDegrees = MusicTheory.getModeIntervals(parentIntervals, i);
            final Scale scale = new Scale(modeNames.get(i), curDegrees);
            mModes.add(scale);
        }
        return new ParentScale(parentName, mModes);
    }

}
