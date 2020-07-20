package com.convergencelabstfx.smartdrone.v2.models

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor
import be.tarsos.dsp.util.PitchConverter
import com.convergencelabstfx.smartdrone.models.signalprocessor.PitchProcessorObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

// Wrote this class in Kotlin to take advantage of coroutines
class SignalProcessorKt {

    private var dispatcher: AudioDispatcher? = null

    private val observers: MutableList<PitchProcessorObserver> = ArrayList()

    var isRunning = false
        private set

    fun start() {
        val handler = PitchDetectionHandler { result: PitchDetectionResult, event: AudioEvent? ->
            val pitchInHz = result.pitch
            GlobalScope.launch(Dispatchers.Main) {
                val pitchAsInt = convertPitchToIx(pitchInHz.toDouble())
                for (observer in observers) {
                    observer.handlePitchResult(pitchAsInt)
                }
            }
        }
        val pitchProcessor: AudioProcessor = PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                SAMPLE_RATE.toFloat(),
                AUDIO_BUFFER_SIZE,
                handler)
        dispatcher = AudioDispatcherFactory
                .fromDefaultMicrophone(
                        SAMPLE_RATE,
                        AUDIO_BUFFER_SIZE,
                        BUFFER_OVERLAP)
        dispatcher?.addAudioProcessor(pitchProcessor)
        val audioThread = Thread(dispatcher, "Pitch Processing Thread")
        audioThread.start()
        isRunning = true
    }

    fun stop() {
        if (dispatcher == null) {
            // Todo: throw exception here. ?
            return
        }
        dispatcher!!.stop()
        dispatcher = null
        isRunning = false
    }

    fun addPitchListener(observer: PitchProcessorObserver) {
        observers.add(observer)
    }

    fun removePitchListener(observer: PitchProcessorObserver?) {
        observers.remove(observer)
    }

    private fun convertPitchToIx(pitchInHz: Double): Int {
        return if (pitchInHz == -1.0) { -1 }
        else PitchConverter.hertzToMidiKey(pitchInHz)
    }

    companion object {
        const val SAMPLE_RATE = 22050
        const val AUDIO_BUFFER_SIZE = 1024
        const val BUFFER_OVERLAP = 0
    }

}