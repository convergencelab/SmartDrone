package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate

class DroneRepository(private val voicingTemplateDao: VoicingTemplateDao) {

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

    // todo: delete


    // todo: update

}