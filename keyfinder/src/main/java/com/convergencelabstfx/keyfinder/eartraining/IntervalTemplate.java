package com.convergencelabstfx.keyfinder.eartraining;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class holds onto harmonic data.
 * Orders it's tones.
 */
public class IntervalTemplate implements Serializable {

    private List<Integer> indices;

    private int highestIx;

    private int lowestIx;

    public IntervalTemplate() {
        indices = new ArrayList<>();
        highestIx = -1;
        lowestIx = -1;
    }

    public IntervalTemplate(Integer[] indices) {
        this(Arrays.asList(indices));
    }

    public IntervalTemplate(List<Integer> indices) {
        this.indices = indices;
        findRange();
    }

    private void findRange() {
        if (size() == 0) {
            highestIx = lowestIx = -1;
        }
        else {
            highestIx = lowestIx = indices.get(0);
            for (int ix : indices) {
                if (ix < lowestIx) {
                    lowestIx = ix;
                }
                else if (ix > highestIx) {
                    highestIx = ix;
                }
            }
        }
    }

    public int getSpaceRequired() {
        return highestIx - lowestIx;
    }

//    public Pattern generatePattern(int key) {
//        Pattern pattern = new Pattern();
//        for (int ix : indices) {
//            pattern.getNotes().add(new Note(ix + key));
//        }
//        return pattern;
//    }

    public int size() {
        return indices.size();
    }

    public List<Integer> getIndices() {
        return indices;
    }

}
