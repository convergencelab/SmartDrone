package com.convergencelabstfx.smartdrone.database

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.LiveData
import com.convergencelabstfx.keyfinder.MusicTheory
import com.convergencelabstfx.keyfinder.ParentScale
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.keyfinder.keypredictor.KeyPredictor
import com.convergencelabstfx.keyfinder.keypredictor.Phrase
import com.convergencelabstfx.keyfinder.keypredictor.PhrasePredictor
import com.convergencelabstfx.smartdrone.R
import com.convergencelabstfx.smartdrone.models.ChordConstructor
import com.convergencelabstfx.smartdrone.models.ScaleConstructor
import com.convergencelabstfx.smartdrone.models.VoicingBounds
import com.convergencelabstfx.smartdrone.stringToVoicingTemplate
import com.convergencelabstfx.smartdrone.voicingTemplateToString

class DroneRepository(
        private val voicingTemplateDao: VoicingTemplateDao,
        private val sharedPreferences: SharedPreferences,
        private val resources: Resources) {

    // todo: implement this later
    val allTemplates: LiveData<List<VoicingTemplateEntity>> = voicingTemplateDao.getTemplates()

    // todo: implement other methods from DAO

    // todo: insert
    suspend fun insert(template: VoicingTemplateEntity) {
        voicingTemplateDao.insertTemplate(template)
    }

    fun saveCurTemplate(template: VoicingTemplate) {
        with (sharedPreferences.edit()) {
            putString(resources.getString(R.string.voicing_template_key), voicingTemplateToString(template))
            commit()
        }
    }

    fun getCurTemplate(): VoicingTemplate {
        val templateStr = sharedPreferences.getString(
                resources.getString(R.string.voicing_template_key),
                resources.getString(R.string.default_voicing_template)
        )
        return stringToVoicingTemplate(templateStr!!)
    }

    fun saveScaleIxs(parent: Int, mode: Int) {
        with (sharedPreferences.edit()) {
            putInt(resources.getString(R.string.parent_ix_key), parent)
            putInt(resources.getString(R.string.mode_ix_key), mode)
            commit()
        }
    }

    fun getParentScaleIx() : Int {
        return sharedPreferences.getInt(resources.getString(R.string.parent_ix_key), 0)
    }

    fun getModeIx() : Int {
        return sharedPreferences.getInt(resources.getString(R.string.mode_ix_key), 0)
    }

    // todo: consider putting this on another thread
    fun getParentScales() : List<ParentScale> {
        val list = ArrayList<ParentScale>()
        val majorScale = ScaleConstructor.makeParentScale(
                "Major Scale",
                listOf(*MusicTheory.MAJOR_SCALE_SEQUENCE),
                listOf(*MusicTheory.MAJOR_MODE_NAMES)
        )
        val melodicMinor = ScaleConstructor.makeParentScale(
                "Melodic Minor",
                listOf(*MusicTheory.MELODIC_MINOR_SCALE_SEQUENCE),
                listOf(*MusicTheory.MELODIC_MINOR_MODE_NAMES)
        )
        val harmonicMinor = ScaleConstructor.makeParentScale(
                "Harmonic Minor",
                listOf(*MusicTheory.HARMONIC_MINOR_SCALE_SEQUENCE),
                listOf(*MusicTheory.HARMONIC_MINOR_MODE_NAMES)
        )
        val harmonicMajor = ScaleConstructor.makeParentScale(
                "Harmonic Major",
                listOf(*MusicTheory.HARMONIC_MAJOR_SCALE_SEQUENCE),
                listOf(*MusicTheory.HARMONIC_MAJOR_MODE_NAMES)
        )
        list.add(majorScale)
        list.add(melodicMinor)
        list.add(harmonicMinor)
        list.add(harmonicMajor)
        return list
    }

    fun saveVoicingBounds(bounds: VoicingBounds) {
        // todo:
    }

    fun getVoicingBounds(): VoicingBounds {
        return VoicingBounds(36, 60, 48, 84)
    }

    fun saveMidiPlugin(ix: Int) {
        // todo:
    }

    fun getMidiPlugin() : Int {
        return sharedPreferences.getInt(resources.getString(R.string.midi_plugin_key), 48)
    }

    fun saveKeyPredictor(predictor: KeyPredictor) {
        // todo
    }

    // todo
    fun getKeyPredictor() : KeyPredictor {
        val mOctavePhrase = Phrase()
        mOctavePhrase.addNote(0)
        mOctavePhrase.addNote(12)
        val predictor = PhrasePredictor()
        predictor.targetPhrase = mOctavePhrase
        return predictor
    }

    fun saveChordConstructorType(type: ChordConstructor) {
        with (sharedPreferences.edit()) {
            putInt(resources.getString(R.string.parent_ix_key), type.ordinal)
            commit()
        }
    }

    fun getChordConstructorType() : ChordConstructor {
        val ordinal = sharedPreferences.getInt(resources.getString(R.string.key_predictor_key), 0)
        return ChordConstructor.values()[ordinal]
    }

    fun getSf2FileName() : String {
        return "string_section.sf2"
    }

}
