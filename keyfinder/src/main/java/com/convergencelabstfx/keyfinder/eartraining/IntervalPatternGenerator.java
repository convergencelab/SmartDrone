package com.convergencelabstfx.keyfinder.eartraining;

import java.util.List;

@Deprecated
public class IntervalPatternGenerator {

    private List<IntervalTemplate> intervalTemplates;

    private int lowerBound;

    private int upperBound;

    private boolean ascendingIsEnabled;

    private boolean descendingIsEnabled;

    private boolean randomizedIsEnabled;

    public IntervalPatternGenerator() {
        this(-1, -1);
    }

    public IntervalPatternGenerator(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        ascendingIsEnabled = descendingIsEnabled = randomizedIsEnabled = false;
    }

    public void setLowerBound(int lowerBound) {
        if (lowerBound < this.upperBound) {
            this.lowerBound = lowerBound;
        }
        else {
            // todo: throw exception
        }
    }

    public void setUpperBound(int upperBound) {
        if (upperBound > lowerBound) {
            this.upperBound = upperBound;
        }
        else {
            // todo: throw exception
        }
    }



    public void addPatternTemplate(IntervalTemplate toAdd) {
        // todo: check for duplicates
        intervalTemplates.add(toAdd);
    }

    public void removePatternTemplate(IntervalTemplate toRemove) {
        intervalTemplates.remove(toRemove);
    }

    private boolean hasValidBounds() {
        // todo
        // for each template
            // get range
            // todo: hacky solution: multiply range of degree by 2



        return false;
    }

}
