package com.convergencelabstfx.smartdrone.models

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class PitchConstructor {

    private val notes: MutableSet<Int> = hashSetOf()

    var listener: PitchConstructorListener? = null

    var silenceThreshold: Long = 3000

    var curJob: Job? = null

    fun start() {
        curJob = GlobalScope.launch {
            Timber.i("thread started")
            delay(silenceThreshold)
            Timber.i("thread executed")
            listener?.onConstructorFinished()
            notes.clear()
        }
    }

    fun noteDetected(note: Int) {
        if (!notes.contains(note)) {
            if (curJob != null) {
                curJob?.cancel()
                Timber.i("thread stopped")
            }
            notes.add(note)
            listener?.onNoteDetected(note)
            curJob = GlobalScope.launch {
                Timber.i("thread started")
                delay(silenceThreshold)
                Timber.i("thread executed")
                listener?.onConstructorFinished()
                notes.clear()
            }
        }
    }

    fun noteUndetected(note: Int) {

    }

}