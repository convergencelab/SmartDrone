package com.convergencelabstfx.keyfinder.harmony;


import com.convergencelabstfx.keyfinder.Key;
import com.convergencelabstfx.keyfinder.Mode;

public class Voicing {

//    private final Note[] voices;

    public Voicing(VoicingTemplate template, Key key, Mode mode, int minBassIx, int minChordIx) {
//        voices = makeVoices(template, key, minBassIx, minChordIx);
    }

//    private Note[] makeVoices(VoicingTemplate template, Key key, int minBassIx, int minChordIx) {
//        Note[] toReturn = new Note[template.size()];
//        int ix = 0;
//
//        // Make Bass Notes
//        int bassMin = MusicTheory.getLowestIx(key.getIx(), minBassIx);
//        for (BassTone tone : template.getBassTones()) {
//            toReturn[ix] = new Note(
//                    bassMin + (MusicTheory.TOTAL_NOTES * (tone.getDegree() / MusicTheory.DIATONIC_SCALE_SIZE))
//                            + key.getMode().intervals[tone.getDegree() % MusicTheory.DIATONIC_SCALE_SIZE]);
//            ix++;
//        }
//
//        // Make Chord Notes
//        int chordMin = MusicTheory.getLowestIx(key.getIx(), minChordIx);
//        for (ChordTone tone : template.getChordTones()) {
//            toReturn[ix] = new Note(
//                    chordMin + (MusicTheory.TOTAL_NOTES * (tone.getDegree() / MusicTheory.DIATONIC_SCALE_SIZE))
//                            + key.getMode().intervals[tone.getDegree() % MusicTheory.DIATONIC_SCALE_SIZE]);
//            ix++;
//        }
//        return toReturn;
//    }

//    public Note[] getVoices() {
//        return voices;
//    }

}
