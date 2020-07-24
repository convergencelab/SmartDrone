package com.convergencelabstfx.keyfinder;

import java.util.List;

public class Scale {

    private String mName;

    private List<Integer> mIntervals;

    public Scale(String name, List<Integer> intervals) {
        mName = name;
        mIntervals = intervals;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Integer> getIntervals() {
        return mIntervals;
    }

    public void setIntervals(List<Integer> intervals) {
        mIntervals = intervals;
    }

    public int size() {
        return mIntervals.size();
    }

}
