package com.convergencelabstfx.keyfinder.harmony;

public class VoicingTemplate {

    private final String name;

    private final BassTone[] bassTones;

    private final ChordTone[] chordTones;

    private final int size;

    public VoicingTemplate(int[] bassTones, int[] chordTones) {
        this(null, bassTones, chordTones);
    }

    public VoicingTemplate(String name, int[] bassTones, int[] chordTones) {
        this.name = name;
        this.bassTones = ixsToBassTones(bassTones);
        this.chordTones = ixsToChordTones(chordTones);
        this.size = bassTones.length + chordTones.length;
    }

    public BassTone[] getBassTones() {
        return bassTones;
    }

    public ChordTone[] getChordTones() {
        return chordTones;
    }

    public int size() {
        return size;
    }

    private BassTone[] ixsToBassTones(int[] bassToneIxs) {
        final BassTone[] toReturn = new BassTone[bassToneIxs.length];
        for (int i = 0; i < bassToneIxs.length; ++i) {
            toReturn[i] = new BassTone(bassToneIxs[i]);
        }
        return toReturn;
    }

    private ChordTone[] ixsToChordTones(int[] chordToneIxs) {
        final ChordTone[] toReturn = new ChordTone[chordToneIxs.length];
        for (int i = 0; i < chordToneIxs.length; ++i) {
            toReturn[i] = new ChordTone(chordToneIxs[i]);
        }
        return toReturn;
    }

}
