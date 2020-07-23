package com.convergencelabstfx.keyfinder.eartraining;

import com.convergencelabstfx.keyfinder.Mode;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class AbstractTemplate implements Serializable {

    private List<Integer> tones;

    private int lowestDegree;

    private int highestDegree;

    public AbstractTemplate() {
//        this(new ArrayList<>());
    }

    public AbstractTemplate(List<Integer> tones) {
        this.tones = tones;
        lowestDegree = 0;
        highestDegree = 0;
        findRange();
    }

    public List<Integer> getTones() {
        return tones;
    }

    public void setTones(List<Integer> tones) {
        this.tones = tones;
        findRange();
    }

    public int numTones() {
        return tones.size();
    }

    public int getLowestDegree() {
        return lowestDegree;
    }

    public int getHighestDegree() {
        return highestDegree;
    }

    public void addDegree(int degree) {
        tones.add(degree);

        // Edge case, going from 0 to 1 nodes
        if (tones.size() == 1) {
            lowestDegree = highestDegree = tones.get(0);
        }

        else if (degree < lowestDegree) {
            lowestDegree = degree;
        }
        else if (degree > highestDegree) {
            highestDegree = degree;
        }
    }

    private void findRange() {
        if (tones.isEmpty()) {
            lowestDegree = -1;
            highestDegree = -1;
        }
        else {
            lowestDegree = highestDegree = tones.get(0);
            for (int tone : tones) {
                if (tone < lowestDegree) {
                    lowestDegree = tone;
                }
                else if (tone > highestDegree) {
                    highestDegree = tone;
                }
            }
        }
    }

    public int calculateMinSpaceRequired(Mode mode) {
        return mode.getInterval(highestDegree) - mode.getInterval(lowestDegree);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTemplate that = (AbstractTemplate) o;
        return Objects.equals(tones, that.tones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tones);
    }
}
