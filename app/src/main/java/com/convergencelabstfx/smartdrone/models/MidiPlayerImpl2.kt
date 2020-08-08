package com.convergencelabstfx.smartdrone.models

import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import jp.kshoji.javax.sound.midi.MidiUnavailableException
import jp.kshoji.javax.sound.midi.Receiver
import java.io.IOException
import java.util.*

class MidiPlayerImpl2 : MidiPlayer {

    private var synth: SoftSynthesizer? = null

    private var recv: Receiver? = null

    private val activeNotes: Set<Int> = HashSet()

    private var plugin: Int = -1

    fun start() {
        try {
            val sf = SF2Soundbank(getAssets.open("SmallTimGM6mb.sf2"))
            synth = SoftSynthesizer()
            synth.open()
            synth.loadAllInstruments(sf)
            synth.channels[0].programChange(0)
            synth.channels[1].programChange(1)
            recv = synth.receiver
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: MidiUnavailableException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        clear()
        mDriver.stop()
    }

    override fun isRunning(): Boolean {
        TODO("Not yet implemented")
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
        setVolume(MidiPlayerImpl.VOLUME_OFF)
    }

    fun unMute() {
        setVolume(mVolume)
    }

    override fun isMuted(): Boolean {
        TODO("Not yet implemented")
    }

    /*
     * noteOn and noteOff exist because they don't have the side effect of adding and removing notes
     * to mActiveNotes.
     * Methods refreshPlayback and clear require this to avoid throwing ConcurrentModificationException()
     */
    private fun noteOn(note: Int) {
        sendMessage(MidiPlayerImpl.START, note, mVolume)
    }

    private fun noteOff(note: Int) {
        sendMessage(MidiPlayerImpl.STOP, note, mVolume)
    }

    private fun sendMidiSetup() {
        val message = ByteArray(2)
        message[0] = MidiPlayerImpl.PROGRAM_CHANGE.toByte()
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