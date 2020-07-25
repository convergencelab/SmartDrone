package com.convergencelabstfx.keyfinder.keypredictor;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO: documentation
 */
public class Phrase {

    private List<Integer> notes = new LinkedList<>();

    public Phrase() {
    }

    public List<Integer> getNotes() {
        return notes;
    }

    public void setNotes(List<Integer> notes) {
        this.notes = notes;
    }

    public void addNote(Integer toAdd) {
        notes.add(toAdd);
    }

    public Integer removeNoteAtIx(int ix) {
        return notes.remove(ix);
    }

}
