package com.convergencelabstfx.keyfinder.keypredictor;

import com.convergencelabstfx.keyfinder.MusicTheory;

import java.util.ArrayList;
import java.util.List;

// TODO: better documentation / variable naming
public class ActiveNoteList {

    // TODO: could combine these lists into one list; if keyweight > 0 then note is active
    // One index for each note, true if active
    private boolean[] noteActiveList = new boolean[MusicTheory.TOTAL_NOTES];
    // One index for each note, stores note count/weight
    private int[] noteWeightList = new int[MusicTheory.TOTAL_NOTES];

    // Weight + 1 if note is active
    private int[] keyStrengths = new int[MusicTheory.TOTAL_NOTES];
    // Sum of all note weights
    private int[] keyWeights = new int[MusicTheory.TOTAL_NOTES];

    // Sequence to iterate over key weights (based on scale)
    private int[] iterateSequence;

    private int maxKeyStrength = 0;
    private int maxKeyWeight = 0;

    // Todo: note sure why this is here
    private int numActiveNotes = 0;

    public ActiveNoteList() {
    }

    public boolean[] getNoteActiveList() {
        return noteActiveList;
    }

    public int[] getNoteWeightList() {
        return noteWeightList;
    }

    public int[] getKeyStrengths() {
        return keyStrengths;
    }

    public int[] getKeyWeights() {
        return keyWeights;
    }

    public void setIterateSequence(int[] iterateSequence) {
        this.iterateSequence = iterateSequence;
    }

    public void addNote(int toAdd) {
        if (!noteActiveList[toAdd]) {
            incrementKeyStrengths(toAdd);
            noteActiveList[toAdd] = true;
        }
        incrementKeyWeights(toAdd);
        noteWeightList[toAdd]++;
        numActiveNotes++;
    }

    public void removeNote(int toRemove) {
        if (!noteActiveList[toRemove]) {
            // TODO: improve exception (display note and list)
            throw new IllegalStateException("Tried to remove note that isn't active.");
        }
        decrementKeyStrengths(toRemove);
        decrementKeyWeights(toRemove, noteWeightList[toRemove]);
        noteActiveList[toRemove] = false;
        noteWeightList[toRemove] = 0;
        numActiveNotes--;
    }

    public boolean isNoteActive(int toCheck) {
        return noteActiveList[toCheck];
    }

    public int getNoteWeight(int toCheck) {
        return noteWeightList[toCheck];
    }

    public int getKeyStrength(int toCheck) {
        return keyStrengths[toCheck];
    }

    public int getMaxKeyStrength() {
        return maxKeyStrength;
    }

    public int getMaxKeyWeight() {
        return maxKeyWeight;
    }

    public List<Integer> getKeysWithMaxStrength() {
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (int i = 0; i < MusicTheory.TOTAL_NOTES; i++) {
            if (keyStrengths[i] == maxKeyStrength) {
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    public List<Integer> getKeysWihtMaxWeight() {
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (int i = 0; i < MusicTheory.TOTAL_NOTES; i++) {
            if (keyWeights[i] == maxKeyWeight) {
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    public int numActiveNotes() {
        return numActiveNotes;
    }

    public void clear() {
        numActiveNotes = 0;
        // Reset note counts & key strengths
        // Arrays are same numActiveNotes; 12
        for (int ix = 0; ix < noteActiveList.length; ix++) {
            noteActiveList[ix] = false;
            noteWeightList[ix] = 0;
            keyStrengths[ix] = 0;
            keyWeights[ix] = 0;
            maxKeyWeight = 0;
            maxKeyStrength = 0;
        }
    }

    public int findMaxKeyStrength() {
        int max = -1;
        for (int strength : keyStrengths) {
            if (strength > max) {
                max = strength;
            }
        }
        return max;
    }

    private int findMaxKeyWeight() {
        int max = -1;
        for (int weight : keyWeights) {
            if (weight > max) {
                max = weight;
            }
        }
        return max;
    }

    private void incrementKeyStrengths(int toAdd) {
        for (int interval : iterateSequence) {
            keyStrengths[(interval + toAdd) % MusicTheory.TOTAL_NOTES]++;
        }
    }

    private void decrementKeyStrengths(int toRemove) {
        for (int interval : iterateSequence) {
            keyStrengths[(interval + toRemove) % MusicTheory.TOTAL_NOTES]--;
        }
    }

    private void incrementKeyWeights(int toAdd) {
        for (int interval : iterateSequence) {
            keyWeights[(interval + toAdd) % MusicTheory.TOTAL_NOTES]++;
        }
    }

    private void decrementKeyWeights(int toRemove, int amount) {
        for (int interval : iterateSequence) {
            keyWeights[(interval + toRemove) % MusicTheory.TOTAL_NOTES] -= amount;
        }
    }

}
