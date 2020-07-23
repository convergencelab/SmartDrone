package com.convergencelabstfx.smartdrone.models;

import com.convergencelabstfx.keyfinder.Key;
import com.convergencelabstfx.keyfinder.Mode;
import com.convergencelabstfx.keyfinder.harmony.Voicing;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;

public class ChordConstructor {

    private Voicing mCurVoicing = null;

    private Mode mMode = null;

    private Key mKey = null;

    private VoicingTemplate mTemplate = null;

    public ChordConstructor() {

    }

    public Voicing makeVoicing() {
        // todo: implement voicing making logic
        return null;
    }

    public Voicing getCurVoicing() {
        return mCurVoicing;
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }

    public Key getKey() {
        return mKey;
    }

    public void setKey(Key key) {
        mKey = key;
    }

    public VoicingTemplate getTemplate() {
        return mTemplate;
    }

    public void setTemplate(VoicingTemplate template) {
        mTemplate = template;
    }
}
