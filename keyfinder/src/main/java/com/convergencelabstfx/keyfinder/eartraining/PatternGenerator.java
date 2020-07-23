package com.convergencelabstfx.keyfinder.eartraining;

import com.convergencelabstfx.keyfinder.Mode;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PatternGenerator {

    private List<AbstractTemplate> abstractTemplates;

    private List<Mode> modes;

    public PatternGenerator() {
        abstractTemplates = new ArrayList<>();
        modes = new ArrayList<>();
    }



    public void addPhraseTemplate(AbstractTemplate toAdd) {
        abstractTemplates.add(toAdd);
    }

    public void removePhraseTemplate(AbstractTemplate toRemove) {
        abstractTemplates.remove(toRemove);
    }

    public void addMode(Mode mode) {
        modes.add(mode);
    }

    public void removeMovde(Mode mode) {
        modes.remove(mode);
    }

}
