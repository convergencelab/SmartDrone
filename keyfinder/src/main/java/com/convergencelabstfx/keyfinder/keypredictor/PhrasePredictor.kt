package com.convergencelabstfx.keyfinder.keypredictor

import com.convergencelabstfx.keyfinder.MusicTheory
import com.convergencelabstfx.keyfinder.Note

// Wrote this class in Kotlin to take advantage of coroutines
class PhrasePredictor : KeyPredictor() {

    // TODO: deal with default bounds
    var lowerBound = 48
    var upperBound = 59

    private val userPhrase = Phrase()

    var targetPhrase: Phrase? = null

    override fun noteDetected(note: Int) {
        check(userPhrase.notes.size <= targetPhrase!!.notes.size) { "User phrase size has exceeded target phrase size." }

        if (userPhrase.notes.size == targetPhrase!!.notes.size) {
            userPhrase.removeNoteAtIx(0)
        }

        userPhrase.addNote(Note(note))
        if (userPhraseMatchesTarget() && userPhraseIsWithinBounds()) {
            // TODO: make better
            // For now, the predicted key is always the index of the first note in the phrase.
            // (this works for octave phrase predictions, but won't necessarily work with other
            // phrase implementations).
            notifyListeners(userPhrase.notes[0].ix % MusicTheory.TOTAL_NOTES)
        }
    }

    override fun noteUndetected(note: Int) {
        // todo: implement
    }

    private fun userPhraseMatchesTarget(): Boolean {
        checkNotNull(targetPhrase) { "Target Phrase not set." }
        if (targetPhrase!!.notes.size != userPhrase.notes.size) {
            return false
        }
        val targetBase = targetPhrase!!.notes[0].ix
        val userBase = userPhrase.notes[0].ix
        for (i in targetPhrase!!.notes.indices) {
            if (targetPhrase!!.notes[i].ix - targetBase
                    != userPhrase.notes[i].ix - userBase) {
                return false
            }
        }
        return true
    }

    private fun userPhraseIsWithinBounds(): Boolean {
        // Both upper and lower bound are INCLUSIVE
        return (userPhrase.notes[0].ix in lowerBound..upperBound)
    }
}