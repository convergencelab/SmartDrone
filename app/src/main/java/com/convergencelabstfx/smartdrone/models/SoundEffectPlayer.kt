package com.convergencelabstfx.smartdrone.models

import android.content.Context
import android.media.MediaPlayer
import com.convergencelabstfx.smartdrone.R
import timber.log.Timber


class SoundEffectPlayer(val context: Context) {

    private val metronomeClack: MediaPlayer = MediaPlayer.create(context, R.raw.metronome_clack)


    fun playFinishedSound() {
        val mp = MediaPlayer.create(context, R.raw.beep)
        mp.start()
        mp.setOnCompletionListener { mediaPlayer -> mediaPlayer.release() }
    }

    fun playMetronomeClack() {
        if (metronomeClack.isPlaying) {
            Timber.i("seek called")
            metronomeClack.seekTo(0)
        }
        else {
            Timber.i("start called")
            metronomeClack.start()
        }

    }

}