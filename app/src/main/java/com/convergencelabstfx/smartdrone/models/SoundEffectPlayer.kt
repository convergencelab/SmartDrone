package com.convergencelabstfx.smartdrone.models

import android.content.Context
import android.media.MediaPlayer
import com.convergencelabstfx.smartdrone.R


class SoundEffectPlayer(val context: Context) {

    fun playFinishedSound() {
        val mp: MediaPlayer = MediaPlayer.create(context, R.raw.beep)
        mp.start()
    }

}