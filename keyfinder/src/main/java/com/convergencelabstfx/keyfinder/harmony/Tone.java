package com.convergencelabstfx.keyfinder.harmony;

import java.util.Objects;

public class Tone implements Comparable<Tone> {

    private final int degree;

    public Tone(int degree) {
        if (degree < 0) {
            throw new IllegalArgumentException("Parameter 'degree' must be greater than 0. degree: " + degree);
        }
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tone tone = (Tone) o;
        return degree == tone.degree;
    }

    @Override
    public int hashCode() {
        return Objects.hash(degree);
    }

    @Override
    public int compareTo(Tone tone) {
        return Integer.compare(this.getDegree(), tone.getDegree());
    }
}
