package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import com.convergencelabstfx.keyfinder.MusicTheory
import com.convergencelabstfx.keyfinder.ParentScale
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.models.ScaleConstructor
import com.convergencelabstfx.smartdrone.models.VoicingBounds

class DroneRepository(private val voicingTemplateDao: VoicingTemplateDao) {

    // todo: implement this later
    val allTemplates: LiveData<List<VoicingTemplateEntity>> = voicingTemplateDao.getTemplates()

    // todo: implement other methods from DAO
    

    // todo: insert
    suspend fun insert(template: VoicingTemplateEntity) {
        voicingTemplateDao.insertTemplate(template)
    }

    fun getCurTemplate(): VoicingTemplate {
        val template = VoicingTemplate()
        template.addBassTone(0)
        template.addChordTone(0)
        template.addChordTone(4)
        template.addChordTone(9)
        return template
    }

    fun saveCurTemplate() {
        // todo: implement
    }

    fun saveScaleIxs(parent: Int, mode: Int) {
        // todo: something here later
    }

    //todo: implement these
    fun getParentScaleIx() : Int {
        return 0;
    }

    fun getModeIx() : Int {
        return 0;
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

    fun getVoicingBounds(): VoicingBounds {
        return VoicingBounds(36, 60, 48, 84)
    }

    // todo: delete


    // todo: update

}