package com.convergencelabstfx.smartdrone.models

interface NoteProcessorListener {

    fun notifyNoteDetected(note: Int)

    fun notifyNoteUndetected(note: Int)

}