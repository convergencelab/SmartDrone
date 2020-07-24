package com.convergencelabstfx.smartdrone.models;


import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;

import java.util.List;

public class ChordConstructor {

    private List<Integer> mCurVoicing = null;

    private List<Integer> mMode = null;

    private int mKey = -1;

    private VoicingTemplate mTemplate = null;

    public ChordConstructor() {

    }

    public List<Integer> makeVoicing() {
        // todo: implement voicing making logic
        if (mTemplate.size() == 0) {
            throw new IllegalStateException("Attempted to make voicing with empty voicing template.");
        }
        else if (mMode.size() == 0) {
            throw new IllegalStateException("Attempted to make voicing with empty mode.");
        }
        return null;
    }

    public List<Integer> getCurVoicing() {
        return mCurVoicing;
    }

    public List<Integer> getMode() {
        return mMode;
    }

    public void setMode(List<Integer> mode) {
        mMode = mode;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        if (key < 0 || key > 11) {
            throw new IllegalArgumentException("Parameter 'key' must be between 0 (inclusive) and 11 (inclusive).");
        }
        mKey = key;
    }

    public VoicingTemplate getTemplate() {
        return mTemplate;
    }

    public void setTemplate(VoicingTemplate template) {
        mTemplate = template;
    }
}
