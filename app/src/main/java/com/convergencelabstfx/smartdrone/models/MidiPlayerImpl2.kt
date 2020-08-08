package com.convergencelabstfx.smartdrone.models

import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import jp.kshoji.javax.sound.midi.InvalidMidiDataException
import jp.kshoji.javax.sound.midi.MidiUnavailableException
import jp.kshoji.javax.sound.midi.Receiver
import jp.kshoji.javax.sound.midi.ShortMessage
import java.io.IOException
import java.util.*

class MidiPlayerImpl2 : MidiPlayer {

    private val START = 0X90

    private val STOP = 0X80

    private val PROGRAM_CHANGE = 0XC0

    private val VOLUME_OFF = 0

    private val DEFAULT_VOLUME = 65

    private var synth: SoftSynthesizer? = null

    private var recv: Receiver? = null

    private val activeNotes: Set<Int> = HashSet()

    private var plugin: Int = -1

    private var volume = DEFAULT_VOLUME

    private var sf2: SF2Soundbank? = null

    override fun start() {

    }

    override fun stop() {
        clear()
        synth?.close()
    }

    override fun isRunning(): Boolean {
        TODO("Not yet implemented")
    }

    override fun playNote(note: Int) {
        if (!noteIsActive(note)) {
            try {
                val msg = ShortMessage()
                msg.setMessage(ShortMessage.NOTE_ON, 0, note, 127)
                recv?.send(msg, -1)
            } catch (e: InvalidMidiDataException) {
                e.printStackTrace()
            }
        }
    }

    override fun stopNote(note: Int) {
        if (noteIsActive(note)) {
            try {
                val msg = ShortMessage()
                msg.setMessage(ShortMessage.NOTE_OFF, 0, note, 127)
                recv!!.send(msg, -1)
            } catch (e: InvalidMidiDataException) {
                e.printStackTrace()
            }

        }
    }

    override fun playChord(notes: List<Int>) {
        for (note in notes) {
            playNote(note)
        }
    }

    override fun stopChord(notes: List<Int>) {
        for (note in notes) {
            stopNote(note)
        }
    }

    override fun clear() {
//        for (note in mActiveNotes) {
//            noteOff(note)
//        }
//        mActiveNotes.clear()
    }

    override fun noteIsActive(note: Int): Boolean {
//        return mActiveNotes.contains(note)
        return false
    }

    override fun hasActiveNotes(): Boolean {
//        return !mActiveNotes.isEmpty()
        return false
    }

    override fun getPlugin(): Int {
        return plugin
    }

    override fun setPlugin(plugin: Int) {
        if (this.plugin != plugin) {
            this.plugin = plugin
//            if (mDriver != null) {
//                // Need to write plugin to midi driver.
//                sendMidiSetup()
//                if (hasActiveNotes()) {
//                    refreshPlayback()
//                }
//            }
        }
    }

    override fun getVolume(): Int {
        return volume
    }

    override fun setVolume(volume: Int) {
        if (volume != this.volume) {
            this.volume = volume
            if (synth != null && hasActiveNotes()) {
                refreshPlayback()
            }
        }
    }

    override fun mute() {
        setVolume(VOLUME_OFF)
    }

    override fun unMute() {
        setVolume(volume)
    }

    override fun isMuted(): Boolean {
        TODO("Not yet implemented")
    }

    fun setSf2(sf2: SF2Soundbank) {
        try {
            synth = SoftSynthesizer()
            synth!!.open()
            synth!!.loadAllInstruments(sf2)
            synth!!.channels[0].programChange(0)
            synth!!.channels[1].programChange(1)
            recv = synth!!.receiver
            this.sf2 = sf2
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: MidiUnavailableException) {
            e.printStackTrace()
        }
    }

    /*
     * noteOn and noteOff exist because they don't have the side effect of adding and removing notes
     * to mActiveNotes.
     * Methods refreshPlayback and clear require this to avoid throwing ConcurrentModificationException()
     */
    private fun noteOn(note: Int) {
        sendMessage(START, note, volume)
    }

    private fun noteOff(note: Int) {
        sendMessage(STOP, note, volume)
    }

    private fun sendMidiSetup() {
//        val message = ByteArray(2)
//        message[0] = MidiPlayerImpl.PROGRAM_CHANGE.toByte()
//        message[1] = mPlugin as Byte
//        mDriver.write(message)
    }

    private fun sendMessage(event: Int, toSend: Int, volume: Int) {
//        val message = ByteArray(3)
//        message[0] = event.toByte()
//        message[1] = toSend.toByte()
//        message[2] = volume.toByte()
//        mDriver.write(message)
    }

    private fun refreshPlayback() {
//        for (note in mActiveNotes) {
//            noteOff(note)
//        }
//        for (note in mActiveNotes) {
//            noteOn(note)
//        }
    }

}