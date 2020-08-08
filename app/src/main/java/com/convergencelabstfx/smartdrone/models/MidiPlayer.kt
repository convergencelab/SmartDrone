package com.convergencelabstfx.smartdrone.models

interface MidiPlayer {

    fun start()

    fun stop()

    fun isRunning() : Boolean

    fun playNote(note: Int)

    fun stopNote(note: Int)

    fun playChord(chord: List<Int>)

    fun stopChord(chord: List<Int>)

    fun clear()

    fun noteIsActive(note: Int) : Boolean

    fun hasActiveNotes() : Boolean

    fun setPlugin(plugin: Int)

    fun getPlugin() : Int

    fun setVolume(volume: Int)

    fun getVolume() : Int

    fun mute()

    fun unMute()

    fun isMuted() : Boolean

}