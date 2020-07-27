package com.convergencelabstfx.smartdrone.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.convergencelabstfx.keyfinder.MusicTheory
import com.convergencelabstfx.keyfinder.ParentScale
import com.convergencelabstfx.keyfinder.Scale
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictor
import com.convergencelabstfx.keyfinder.keypredictor.Phrase
import com.convergencelabstfx.keyfinder.keypredictor.PhrasePredictor
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
class DroneViewModel(application: Application?) : AndroidViewModel(application!!) {
    // todo: remove; just a place holder field
    //    private Scale mCurScale;
    private val mRepository: DroneRepository

    // todo: CHANGE HERE
    private val mAllTemplates: LiveData<List<VoicingTemplateEntity>>
    private val mSignalProcessor = SignalProcessorKt()
    private val mNoteProcessor = NoteProcessor()
    private var mKeyPredictor: KeyPredictor? = null
    private val mChordConstructor = ChordConstructor()
    private val mMidiPlayer = MidiPlayer()
    private val mParentScales: MutableList<ParentScale> = ArrayList()
    private val mVoicingTemplates: MutableList<VoicingTemplate> = ArrayList()
    @JvmField
    var mDroneIsActive = MutableLiveData<Boolean?>()
    @JvmField
    var mDetectedNote = MutableLiveData<Int>()
    var mUndetectedNote = MutableLiveData<Int>()
    @JvmField
    var mDetectedKey = MutableLiveData<Int?>()
    var mCurScale = MutableLiveData<Scale>()
    fun startDrone() {
        mSignalProcessor.start()
        mMidiPlayer.start()
        mDetectedKey.value = -1
        mDroneIsActive.value = true
    }

    fun stopDrone() {
        mSignalProcessor.stop()
        mMidiPlayer.stop()
        mDetectedNote.value = -1
        mDetectedKey.value = -1
        mDroneIsActive.value = false
    }

    fun setKeyChange(key: Int) {
        if (isRunning && mDetectedKey.value != null && key != mDetectedKey.value) {
            mChordConstructor.key = key
            mDetectedKey.value = key
            mMidiPlayer.clear()
            mMidiPlayer.playChord(mChordConstructor.makeVoicing())
        }
    }

    val isRunning: Boolean
        get() = mDroneIsActive.value != null && mDroneIsActive.value!!

    // todo: figure how this should work
    val curModeName: String?
        get() = null

    val parentScales: List<ParentScale>
        get() = mParentScales

    val voicingTemplates: List<VoicingTemplate>
        get() = mVoicingTemplates

    fun setScale(scale: Scale) {
        mCurScale.value = scale
        // todo: update playback
        mChordConstructor.mode = scale.intervals
        if (mMidiPlayer.hasActiveNotes()) {
            mMidiPlayer.clear()
            mMidiPlayer.playChord(mChordConstructor.makeVoicing())
        }
    }

    fun setVoicingTemplate(template: VoicingTemplate?) {
        mChordConstructor.template = template
        if (mMidiPlayer.hasActiveNotes()) {
            mMidiPlayer.clear()
            mMidiPlayer.playChord(mChordConstructor.makeVoicing())
        }
    }

    fun insertVoicingTemplate(template: VoicingTemplate?) = viewModelScope.launch {
        val templateEntity = VoicingTemplateEntity(template!!)
        mRepository.insert(templateEntity)
    }

    // todo: just a method for development purposes; should delete later
    private fun testMethod_setupKeyPredictor() {
        // todo: find a better place for this eventually
        val mOctavePhrase = Phrase()
        mOctavePhrase.addNote(0)
        mOctavePhrase.addNote(12)
        val predictor = PhrasePredictor()
        predictor.targetPhrase = mOctavePhrase
        mKeyPredictor = predictor
    }

    private fun testMethod_setupVoicingTemplates() {
        val template = VoicingTemplate()
        template.addBassTone(0)
        template.addChordTone(0)
        template.addChordTone(4)
        template.addChordTone(9)
        mVoicingTemplates.add(template)
        val template2 = VoicingTemplate()
        template2.addBassTone(0)
        template2.addChordTone(6)
        template2.addChordTone(9)
        template2.addChordTone(11)
        mVoicingTemplates.add(template2)
        val template3 = VoicingTemplate()
        template3.addBassTone(0)
        template3.addChordTone(3)
        template3.addChordTone(6)
        template3.addChordTone(9)
        mVoicingTemplates.add(template3)
    }

    private fun testMethod_setupChordConstructor() {
        val mode: MutableList<Int> = ArrayList()
        mode.add(0)
        mode.add(2)
        mode.add(3)
        mode.add(5)
        mode.add(7)
        mode.add(9)
        mode.add(10)
        val template = VoicingTemplate()
        template.addBassTone(0)
        template.addBassTone(4)
        template.addChordTone(1)
        template.addChordTone(2)
        template.addChordTone(4)
        template.addChordTone(8)
        mChordConstructor.mode = mode
        mChordConstructor.key = 0
        mChordConstructor.template = template
        mChordConstructor.setBounds(36, 60, 48, 72)
    }

    private fun testMethod_setupMidiPlayer() {
        // todo: yeah it's hardcoded for now
        mMidiPlayer.plugin = 48
    }

    private fun testMethod_setupParentScales() {
        val majorScale = ScaleConstructor.makeParentScale(
                "Major Scale",
                Arrays.asList(*MusicTheory.MAJOR_SCALE_SEQUENCE),
                Arrays.asList(*MusicTheory.MAJOR_MODE_NAMES)
        )
        val melodicMinor = ScaleConstructor.makeParentScale(
                "Melodic Minor",
                Arrays.asList(*MusicTheory.MELODIC_MINOR_SCALE_SEQUENCE),
                Arrays.asList(*MusicTheory.MELODIC_MINOR_MODE_NAMES)
        )
        mParentScales.add(majorScale)
        mParentScales.add(melodicMinor)
        mCurScale.value = mParentScales[0].getScaleAt(0)
    }

    // todo: gotta figure out exactly where a note index should turn into a note object
    // todo: make consist naming (observer/listener, notify/handle, onKeyPrediction etc...)
    private fun initPipeline() {
        mSignalProcessor.addPitchListener(SignalProcessorObserver { pitch, probability, isPitched ->
            mDetectedNote.value = pitch
            mNoteProcessor.onPitchDetected(pitch, probability, isPitched)
        })
        mNoteProcessor.addNoteProcessorListener(object : NoteProcessorObserver {
            override fun notifyNoteDetected(note: Int) {
                Timber.i("Detected: $note")
                mKeyPredictor!!.noteDetected(note)
            }

            override fun notifyNoteUndetected(note: Int) {
                mKeyPredictor!!.noteUndetected(note)
                //                mUndetectedNote.setValue(note);
            }
        })
        mKeyPredictor!!.addListener { newKey ->
            Timber.i("key: %s", newKey)
            mChordConstructor.key = newKey
            mDetectedKey.value = newKey
            // todo: implement
            mMidiPlayer.clear()
            mMidiPlayer.playChord(mChordConstructor.makeVoicing())
            Timber.i("newKey: %s", newKey)
        }
    }

    init {
        val templateDao = getDatabase(application!!).voicingTemplateDao()
        mRepository = DroneRepository(templateDao)
        mAllTemplates = mRepository.allTemplates
        testMethod_setupKeyPredictor()
        testMethod_setupChordConstructor()
        testMethod_setupMidiPlayer()
        testMethod_setupParentScales()
        testMethod_setupVoicingTemplates()
        initPipeline()
    }
}