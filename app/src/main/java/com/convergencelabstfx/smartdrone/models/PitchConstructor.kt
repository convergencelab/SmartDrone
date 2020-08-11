package com.convergencelabstfx.smartdrone.models

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PitchConstructor {

    private val notes: MutableSet<Int> = hashSetOf()

    val chord: List<Int>
        get() = notes.toList()

    var listener: PitchConstructorListener? = null

    var silenceThreshold: Long = 3000

    var curJob: Job? = null

    private val threadIsActive: Boolean
        get() = curJob != null

    fun start() {
        startThread()
    }

    fun noteDetected(note: Int) {
        if (!notes.contains(note)) {
            if (threadIsActive) {
                stopThread()
            }
            notes.add(note)
            listener?.onNoteDetected(note)
            startThread()
        }
    }

    fun noteUndetected(note: Int) {

    }

    fun clear() {
        notes.clear()
    }

    private fun startThread() {
        if (curJob == null) {
            curJob = GlobalScope.launch {
                delay(silenceThreshold)
                listener?.onConstructorFinished()
            }
        }
    }

    private fun stopThread() {
        curJob?.cancel()
        curJob = null
    }

}