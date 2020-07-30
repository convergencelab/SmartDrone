package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import com.convergencelabstfx.keyfinder.MusicTheory
import com.convergencelabstfx.keyfinder.ParentScale
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.models.ScaleConstructor

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
        list.add(majorScale)
        list.add(melodicMinor)
        return list
    }


    // todo: delete


    // todo: update

}