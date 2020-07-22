package com.convergencelabstfx.keyfinder.keypredictor;

import com.convergencelabstfx.keyfinder.Note;

import java.util.LinkedList;
import java.util.List;

/**
 * TODO: documentation
 */
public class Phrase {

    private List<Note> notes = new LinkedList<>();

    public Phrase() {
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void addNote(Note toAdd) {
        notes.add(toAdd);
    }

    public Note removeNoteAtIx(int ix) {
        return notes.remove(ix);
    }

}
