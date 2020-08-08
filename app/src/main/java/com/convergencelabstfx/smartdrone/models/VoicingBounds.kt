package com.convergencelabstfx.smartdrone.models

data class VoicingBounds(
        var bassLower: Int,
        var bassUpper: Int,
        var chordLower: Int,
        var chordUpper: Int
) {

    fun setBounds(
            bassLower: Int,
            bassUpper: Int,
            chordLower: Int,
            chordUpper: Int) {
        this.bassLower = bassLower
        this.bassUpper = bassUpper
        this.chordLower = chordLower
        this.chordUpper = chordUpper
    }

}