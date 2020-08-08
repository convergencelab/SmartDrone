package com.convergencelabstfx.smartdrone.models

import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import java.util.*

class MidiPlayer2 {

    private val synth: SoftSynthesizer = SoftSynthesizer()

    private val activeNotes: Set<Int> = HashSet()

    private var plugin: Int = -1

    fun start() {
        mDriver.start()
        sendMidiSetup()
    }

    fun stop() {
        clear()
        mDriver.stop()
    }

    fun playNote(note: Int) {
        if (!noteIsActive(note)) {
            noteOn(note)
            mActiveNotes.add(note)
        }
    }

    fun stopNote(note: Int) {
        if (noteIsActive(note)) {
            noteOff(note)
            mActiveNotes.remove(note)
        }
    }

    fun playChord(notes: List<Int>) {
        for (note in notes) {
            playNote(note)
        }
    }

    fun stopChord(notes: List<Int>) {
        for (note in notes) {
            stopNote(note)
        }
    }

    fun clear() {
        for (note in mActiveNotes) {
            noteOff(note)
        }
        mActiveNotes.clear()
    }

    fun noteIsActive(note: Int): Boolean {
        return mActiveNotes.contains(note)
    }

    fun hasActiveNotes(): Boolean {
        return !mActiveNotes.isEmpty()
    }

    fun getPlugin(): Int {
        return mPlugin
    }

    fun setPlugin(plugin: Int) {
        if (plugin != mPlugin) {
            mPlugin = plugin
            if (mDriver != null) {
                // Need to write plugin to midi driver.
                sendMidiSetup()
                if (hasActiveNotes()) {
                    refreshPlayback()
                }
            }
        }
    }

    fun getVolume(): Int {
        return mVolume
    }

    fun setVolume(volume: Int) {
        if (volume != mVolume) {
            mVolume = volume
            if (mDriver != null && hasActiveNotes()) {
                refreshPlayback()
            }
        }
    }

    fun mute() {
        setVolume(MidiPlayer.VOLUME_OFF)
    }

    fun unMute() {
        setVolume(mVolume)
    }

    /*
     * noteOn and noteOff exist because they don't have the side effect of adding and removing notes
     * to mActiveNotes.
     * Methods refreshPlayback and clear require this to avoid throwing ConcurrentModificationException()
     */
    private fun noteOn(note: Int) {
        sendMessage(MidiPlayer.START, note, mVolume)
    }

    private fun noteOff(note: Int) {
        sendMessage(MidiPlayer.STOP, note, mVolume)
    }

    private fun sendMidiSetup() {
        val message = ByteArray(2)
        message[0] = MidiPlayer.PROGRAM_CHANGE.toByte()
        message[1] = mPlugin as Byte
        mDriver.write(message)
    }

    private fun sendMessage(event: Int, toSend: Int, volume: Int) {
        val message = ByteArray(3)
        message[0] = event.toByte()
        message[1] = toSend.toByte()
        message[2] = volume.toByte()
        mDriver.write(message)
    }

    private fun refreshPlayback() {
        for (note in mActiveNotes) {
            noteOff(note)
        }
        for (note in mActiveNotes) {
            noteOn(note)
        }
    }

}