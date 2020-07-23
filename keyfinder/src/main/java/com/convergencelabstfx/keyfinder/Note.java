package com.convergencelabstfx.keyfinder;

import java.util.Objects;

public class Note {

    private final int ix;

    @Deprecated
    private String name;

    public Note(int ix) {
        this.ix = ix;
    }

    public int getIx() {
        return ix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return ix == note.ix;
    }

//    @Override
//    public String toString() {
//        return name + ix;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(ix);
    }

}
