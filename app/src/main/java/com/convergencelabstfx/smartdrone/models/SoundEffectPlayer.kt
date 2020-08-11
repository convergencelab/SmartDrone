package com.convergencelabstfx.smartdrone.models

import android.content.Context
import android.media.MediaPlayer
import com.convergencelabstfx.smartdrone.R


class SoundEffectPlayer(val context: Context) {

    val metronomeClack = MediaPlayer.create(context, R.raw.metronome_clack)

    fun playFinishedSound() {
        val mp = MediaPlayer.create(context, R.raw.beep)
        mp.start()
        mp.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
    }

    fun playMetronomeClack() {
        /*
        val mp = MediaPlayer.create(context, R.raw.metronome_clack)
        mp.start()
        mp.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }

         */
        if (metronomeClack.isPlaying) {
            metronomeClack.seekTo(0)
        }
        else {
            metronomeClack.start()
        }
//        metronomeClack.start()

    }

}