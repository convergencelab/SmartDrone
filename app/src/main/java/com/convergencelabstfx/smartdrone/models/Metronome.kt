package com.convergencelabstfx.smartdrone.models

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

class Metronome {

    var tickerChannel: ReceiveChannel<Unit>? = null

    var soundCallback: MetronomeSoundCallback? = null

    var timeStamp = 0L

    var lastDif = 0L

    var isActive: Boolean = false
        private set

    var bpm = 0

    fun start() {
        if (!isActive) {
            GlobalScope.launch {
                tickerChannel = ticker(200, 0)
                while (true) {
                    tickerChannel!!.receive()
//                    soundCallback?.playSound()
                    val dif = System.currentTimeMillis() - timeStamp
                    Timber.i("${(System.currentTimeMillis() - timeStamp)}, ${abs(dif - lastDif)}ms")
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