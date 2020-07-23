package com.convergencelabstfx.keyfinder.harmony;

public abstract class Tone {

    // TODO: can this be private ?
    protected final int degree;

    public Tone(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

}
