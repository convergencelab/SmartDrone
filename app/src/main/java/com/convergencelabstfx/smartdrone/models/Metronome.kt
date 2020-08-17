package com.convergencelabstfx.smartdrone.models

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import timber.log.Timber

class Metronome {

    companion object {
        const val MIN_BPM = 30
        const val MAX_BPM = 240
    }

    private val DEFAULT_BPM = 60

    var tickerChannel: ReceiveChannel<Unit>? = null

    var soundCallback: MetronomeSoundCallback? = null

    // todo: remove these; debug variables
    var timeStamp = 0L

    var lastDif = 0L

    var isActive: Boolean = false
        private set

    var bpm = DEFAULT_BPM
        set(value) {
            field = value
            if (isActive) {
                stop()
                start()
            }
        }

    fun start() {
        if (!isActive) {
            GlobalScope.launch {
                tickerChannel = ticker(700, 0)
                while (true) {
                    tickerChannel!!.receive()

                    val soundTimeStamp = System.currentTimeMillis()
                    soundCallback?.playSound()

                    // This line prints the length of the playSound call
                    Timber.i("soundLen: ${System.currentTimeMillis() - soundTimeStamp}")

                    val dif = System.currentTimeMillis() - timeStamp

                    // This line prints the difference between sound finished
                    Timber.i("${(System.currentTimeMillis() - timeStamp)}, ${dif - lastDif}ms")
                    timeStamp = System.currentTimeMillis()
                    lastDif = dif

                }
            }
            isActive = true
        }
    }

    fun stop() {
        if (isActive) {
            tickerChannel?.cancel()
            isActive = false
        }
    }

}