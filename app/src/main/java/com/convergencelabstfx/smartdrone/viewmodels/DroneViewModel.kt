package com.convergencelabstfx.smartdrone.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import com.convergencelabstfx.keyfinder.ParentScale
import com.convergencelabstfx.keyfinder.Scale
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictor
import com.convergencelabstfx.smartdrone.R
import com.convergencelabstfx.smartdrone.database.DroneDatabase.Companion.getDatabase
import com.convergencelabstfx.smartdrone.database.DroneRepository
import com.convergencelabstfx.smartdrone.database.VoicingTemplateEntity
import com.convergencelabstfx.smartdrone.models.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * Drone pipeline:
 * 1) Process signal; TarsosDSP signal processing
 * 2) Process note data; analyze note probability, how long note was heard, etc... (filter out noise)
 * 3) Determine key; analyze processed notes (KeyPredictor)
 * 4) Determine chord;
 * 5) Play chord; MidiDriver
 * 6) Notify UI;
 */
// todo: clean up the java-isms; and clean up the MutableLiveData
class DroneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DroneRepository

    var curTemplate: MutableLiveData<VoicingTemplate> = MutableLiveData()
    var curScale = MutableLiveData<Scale>()

    private val signalProcessor = SignalProcessorKt()
    private val noteProcessor = NoteProcessor()
    private val chordConstructor = VoicingConstructor()
    private var keyPredictor: KeyPredictor


    private var midiPlayer: MidiPlayer
    private val mParentScales: List<ParentScale>


    private val mVoicingTemplates: MutableList<VoicingTemplate> = ArrayList()
    @JvmField
    var mDroneIsActive = MutableLiveData<Boolean?>()
    @JvmField
    var mDetectedNote = MutableLiveData<Int>()
    var mUndetectedNote = MutableLiveData<Int>()
    @JvmField
    var mDetectedKey = MutableLiveData<Int?>()

    init {
        val templateDao = getDatabase(application, viewModelScope).voicingTemplateDao()
        val resources = application.resources
        val sharedPreferences = application.getSharedPreferences(resources.getString(R.string.file_key), Context.MODE_PRIVATE)
        repository = DroneRepository(templateDao, sharedPreferences, resources)

        val sfMidiPlayer = MidiPlayer()
        sfMidiPlayer.setPlugin(repository.getMidiPlugin())

        val sf = SF2Soundbank(application.assets.open(repository.getSf2FileName()))
        sfMidiPlayer.sf2 = sf
        sfMidiPlayer.setPlugin(repository.getMidiPlugin())
        midiPlayer = sfMidiPlayer

        mParentScales = repository.getParentScales()
        setScale(mParentScales[repository.getParentScaleIx()].getScaleAt(repository.getModeIx()))
        chordConstructor.bounds = repository.getVoicingBounds()

        keyPredictor = repository.getKeyPredictor()
        setVoicingTemplate(repository.getCurTemplate())

        initPipeline()

    }

    fun startDrone() {
        signalProcessor.start()
        midiPlayer.start()
        mDetectedKey.value = -1
        mDroneIsActive.value = true
    }

    fun stopDrone() {
        signalProcessor.stop()
        midiPlayer.stop()
        mDetectedNote.value = -1
        mDetectedKey.value = -1
        mDroneIsActive.value = false
    }

    fun setKeyChange(key: Int) {
        if (isRunning && mDetectedKey.value != null && key != mDetectedKey.value) {
            chordConstructor.key = key
            mDetectedKey.value = key
            midiPlayer.clear()
            midiPlayer.playChord(chordConstructor.makeVoicing())
        }
    }

    fun toggleBassTone(degree: Int) {
        val template = curTemplate.value
        val note: Int
        if (template?.bassTones!!.contains(degree)) {
            note = chordConstructor.removeBassTone(degree)
            if (midiPlayer.isRunning) {
                midiPlayer.stopNote(note)
            }
        }
        else {
            note = chordConstructor.addBassTone(degree)
            if (midiPlayer.isRunning) {
                midiPlayer.playNote(note)
            }
        }
        setVoicingTemplate(template)
    }

    fun toggleChordTone(degree: Int) {
        val template = curTemplate.value
        val note: Int
        if (template?.chordTones!!.contains(degree)) {
            note = chordConstructor.removeChordTone(degree)
            if (midiPlayer.isRunning) {
                midiPlayer.stopNote(note)
            }
        }
        else {
            note = chordConstructor.addChordTone(degree)
            if (midiPlayer.isRunning) {
                midiPlayer.playNote(note)
            }
        }
        setVoicingTemplate(template)
    }

    fun saveScaleIxs(parentIx: Int, modeIx: Int) {
        setScale(mParentScales[parentIx].getScaleAt(modeIx))
        repository.saveScaleIxs(parentIx, modeIx)
    }

    val isRunning: Boolean
        get() = mDroneIsActive.value != null && mDroneIsActive.value!!

    val parentScales: List<ParentScale>
        get() = mParentScales

    val voicingTemplates: List<VoicingTemplate>
        get() = mVoicingTemplates

    fun setScale(scale: Scale) {
        curScale.value = scale
        // todo: update playback
        chordConstructor.mode = scale.intervals
        if (midiPlayer.hasActiveNotes()) {
            midiPlayer.clear()
            midiPlayer.playChord(chordConstructor.makeVoicing())
        }
    }

    fun setVoicingTemplate(template: VoicingTemplate) {
        chordConstructor.template = template
        curTemplate.value = template
        repository.saveCurTemplate(template)
//        if (isRunning) {
//            chordConstructor.makeVoicing()
////            if (restartPlayback) {
//                midiPlayer.clear()
//                midiPlayer.playChord(chordConstructor.curVoicing)
////            }
//        }
    }

    fun insertVoicingTemplate(template: VoicingTemplate?) = viewModelScope.launch {
        val templateEntity = VoicingTemplateEntity(template!!)
        repository.insert(templateEntity)
    }

    // todo: gotta figure out exactly where a note index should turn into a note object
    // todo: make consist naming (observer/listener, notify/handle, onKeyPrediction etc...)
    private fun initPipeline() {
        signalProcessor.addPitchListener(SignalProcessorObserver { pitch, probability, isPitched ->
            mDetectedNote.value = pitch
            noteProcessor.onPitchDetected(pitch, probability, isPitched)
        })
        noteProcessor.addNoteProcessorListener(object : NoteProcessorObserver {
            override fun notifyNoteDetected(note: Int) {
                Timber.i("Detected: $note")
                keyPredictor.noteDetected(note)
            }

            override fun notifyNoteUndetected(note: Int) {
                keyPredictor.noteUndetected(note)
                //                mUndetectedNote.setValue(note);
            }
        })
        keyPredictor.addListener { newKey ->
            Timber.i("key: %s", newKey)
            chordConstructor.key = newKey
            mDetectedKey.value = newKey
            // todo: implement
            midiPlayer.clear()
            midiPlayer.playChord(chordConstructor.makeVoicing())
            Timber.i("newKey: %s", newKey)
        }
    }

}