package com.convergencelabstfx.smartdrone.views

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Some weird stuff goes on with the bass tones here because currently there are only two options:
 * the root (1) and the fifth (5).
 */
class VoicingTemplateView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val NUM_CHORD_TONES = 14
        const val NUM_BASS_TONES = 2
    }

    private val chordDegrees = BooleanArray(NUM_CHORD_TONES)

    private val bassDegrees = BooleanArray(NUM_BASS_TONES)

    fun activateChordDegree(degree: Int) {
        if (!chordDegrees[degree]) {
            chordDegrees[degree] = true
        }
    }

    fun deactivateChordDegree(degree: Int) {
        if (chordDegrees[degree]) {
            chordDegrees[degree] = false
        }
    }

    fun activateBassDegree(degree: Int) {
        val realDegree = if (degree == 4) {
            1
        }
        else {
            degree
        }
        if (!bassDegrees[realDegree]) {
            bassDegrees[realDegree] = true
        }
    }

    fun deactivateBassDegree(degree: Int) {
        val realDegree = if (degree == 4) {
            1
        }
        else {
            degree
        }
        if (bassDegrees[realDegree]) {
            bassDegrees[realDegree] = false
        }
    }

    fun chordDegreeIsActive(degree: Int): Boolean {
        return chordDegrees[degree]
    }

    fun bassDegreeIsActive(degree: Int): Boolean {
        val realDegree = if (degree == 4) {
            1
        }
        else {
            degree
        }
        return bassDegrees[realDegree]
    }

    // todo:
    //   - touchListener
    //   - draw
    //   - onSizeChanged
    //   - active color / inactive color
    //   - square size
    //   - construct layout

}