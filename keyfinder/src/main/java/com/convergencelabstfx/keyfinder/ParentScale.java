package com.convergencelabstfx.keyfinder;

import java.util.List;

public class ParentScale {

    private final String mName;

    private final List<Scale> mScales;

    public ParentScale(String name, List<Scale> scales) {
        mName = name;
        mScales = scales;
    }

    public String getName() {
        return mName;
    }

    public Scale getScaleAt(int ix) {
        return mScales.get(ix);
    }

    public int numModes() {
        return mScales.size();
    }

}
