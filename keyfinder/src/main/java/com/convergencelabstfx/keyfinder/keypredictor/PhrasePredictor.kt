package com.convergencelabstfx.keyfinder.keypredictor

import android.util.Log
import com.convergencelabstfx.keyfinder.MusicTheory
import kotlinx.coroutines.Job

// Wrote this class in Kotlin to take advantage of coroutines
// todo: deal with threading later on, it's fine for right now
class PhrasePredictor : KeyPredictor() {

    // TODO: deal with default bounds
    var lowerBound = 36
    var upperBound = 59

    private var prevDetectedKey = -1

    private val noteJobs: HashMap<Int, Job> = HashMap()

    private val userPhrase = Phrase()

    // todo: maybe type Phrase? instead of lateinit
    var targetPhrase: Phrase? = null

    override fun noteDetected(note: Int) {
        Log.d("PhrasePredictor", "Detected: $note")
        check(userPhrase.notes.size <= targetPhrase?.notes!!.size) { "User phrase size has exceeded target phrase size." }

        if (userPhrase.notes.size == targetPhrase?.notes!!.size) {
            userPhrase.removeNoteAtIx(0)
        }

        userPhrase.addNote(note)
        if (newKeyDetected()) {
            // TODO: make better
            // For now, the predicted key is always the index of the first note in the phrase.
            // (this works for octave phrase predictions, but won't necessarily work with other
            // phrase implementations).
            notifyListeners(userPhrase.notes[0] % MusicTheory.TOTAL_NOTES)
            prevDetectedKey = userPhrase.notes[0] % MusicTheory.TOTAL_NOTES;
        }
    }

    override fun noteUndetected(note: Int) {
        // todo: implement
        Log.d("PhrasePredictor", "Undetected: $note")
        // todo: Dispatchers.main ??
//        val curJob = GlobalScope.launch() {
//            delay(noteExpirationLength.toLong())
//        }
    }

    private fun newKeyDetected(): Boolean {
        return userPhrase.notes[0] % MusicTheory.TOTAL_NOTES != prevDetectedKey
                && userPhraseMatchesTarget()
                && userPhraseIsWithinBounds()
    }

    private fun userPhraseMatchesTarget(): Boolean {
        if (targetPhrase?.notes?.size != userPhrase.notes.size) {
            return false
        }
        val targetBase = targetPhrase?.notes?.get(0)
        val userBase = userPhrase.notes[0]
        for (i in targetPhrase?.notes!!.indices) {
            if (targetPhrase?.notes!![i] - targetBase!!
                    != userPhrase.notes[i] - userBase) {
                return false
            }
        }
        return true
    }

    private fun userPhraseIsWithinBounds(): Boolean {
        // Both upper and lower bound are INCLUSIVE
        return (userPhrase.notes[0] in lowerBound..upperBound)
    }
}