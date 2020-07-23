package com.convergencelabstfx.keyfinder.eartraining;

import com.convergencelabstfx.keyfinder.Mode;
import com.convergencelabstfx.keyfinder.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pattern {

    private List<Note> notes;

    private int offset;

    private Mode mode;

    private boolean canInterrupt;

    // AbstractTemplate, Mode, Key
    public Pattern(AbstractTemplate template, int key, Mode mode) {
        notes = new ArrayList<>();
        offset = key - mode.getInterval(template.getLowestDegree());
        this.mode = mode;

        for (int degree : template.getTones()) {
            notes.add(new Note(mode.getInterval(degree) + offset));
        }
    }

    // Degree, Mode, Key
    public Pattern(int degree, int key, Mode mode) {
        notes = new ArrayList<>();
        offset = key - mode.getInterval(degree);
        this.mode = mode;

        notes.add(new Note(mode.getInterval(degree) + offset));
    }

    // IntervalTemplate, Key
    public Pattern(IntervalTemplate template, int key) {
        // This one is pretty simple
        notes = new ArrayList<>();
        for (int ix : template.getIndices()) {
            notes.add(new Note(ix + key));
        }
    }

    @Deprecated
    public Pattern() {
//        this(new ArrayList<>());
    }

    @Deprecated
    public Pattern(List<Note> notes) {
        this.notes = notes;
        canInterrupt = false;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(int degree) {
        getNotes().add(new Note(mode.getInterval(degree) + offset));
    }

    public int size() {
        return notes.size();
    }

    public void setCanInterrupt(boolean canInterrupt) {
        this.canInterrupt = true;
    }

    public boolean canInterrupt() {
        return canInterrupt;
    }

    @Deprecated
    static public Pattern generatePattern(AbstractTemplate template, Mode mode, int rootIx) {
        Pattern toReturn = new Pattern();
//        toReturn.offset = rootIx - mode.getInterval(template.getTones().get(0));
        toReturn.offset = rootIx - mode.getInterval(template.getLowestDegree());
        toReturn.mode = mode;

        for (int degree : template.getTones()) {
            toReturn.getNotes().add(new Note(mode.getInterval(degree) + toReturn.offset));
        }

        return toReturn;
    }

    @Deprecated
    static public Pattern generatePattern(int degree, Mode mode, int rootIx) {
        Pattern toReturn = new Pattern();
        toReturn.offset = rootIx - mode.getInterval(degree);
        toReturn.mode = mode;

        toReturn.getNotes().add(new Note(mode.getInterval(degree) + toReturn.offset));

        return toReturn;
    }

    @Deprecated
    static public int calculateMinSpaceRequired(AbstractTemplate template, Mode mode) {
        return mode.getInterval(template.getHighestDegree()) - mode.getInterval(template.getLowestDegree());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return Objects.equals(notes, pattern.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notes);
    }

//    @Override
//    public String toString() {
//        StringBuilder string = new StringBuilder();
//        for (Note note : notes) {
//            string.append(note.getName());
//            string.append(note.getIx());
//            string.append(' ');
//        }
//        return string.toString();
//    }
}
