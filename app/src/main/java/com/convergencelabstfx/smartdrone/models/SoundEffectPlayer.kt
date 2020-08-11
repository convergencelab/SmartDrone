package com.convergencelabstfx.smartdrone.models

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.SparseIntArray
import com.convergencelabstfx.smartdrone.R


class SoundEffectPlayer(val context: Context) {

    private val PRIORITY = 1

    private val MAX_STREAMS = 4

    private val SOURCE_QUALITY = 0

    private val soundPool = SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, SOURCE_QUALITY)

    private val soundMap = SparseIntArray()

    init {
        loadSoundMap()
    }

    fun playFinishedSound() {
        soundPool.play(
                soundMap.get(R.raw.beep),
                0.99f,
                0.99f,
                0,
                0,
                1f)
    }

    private fun loadSoundMap() {
        soundMap.put(R.raw.beep, this.soundPool.load(context, R.raw.beep, PRIORITY));
    }

}