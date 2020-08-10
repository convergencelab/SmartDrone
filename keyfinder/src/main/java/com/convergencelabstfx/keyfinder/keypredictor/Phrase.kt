package com.convergencelabstfx.keyfinder.keypredictor

/**
 * TODO: documentation
 */
class Phrase {

    var notes: MutableList<Int> = arrayListOf()

    fun addNote(toAdd: Int) {
        notes.add(toAdd)
    }

    fun removeNoteAtIx(ix: Int): Int {
        return notes.removeAt(ix)
    }

}