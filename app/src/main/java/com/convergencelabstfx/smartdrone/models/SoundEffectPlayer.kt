package com.convergencelabstfx.smartdrone.models

import android.content.Context
import android.media.MediaPlayer
import com.convergencelabstfx.smartdrone.R


class SoundEffectPlayer(val context: Context) {

    fun playFinishedSound() {
        MediaPlayer.create(context, R.raw.beep).start()
    }

    fun playMetronomeClack() {
        MediaPlayer.create(context, R.raw.metronome_clack).start()
    }

}