package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData

class DroneRepository(private val voicingTemplateDao: VoicingTemplateDao) {

    val allTemplates: LiveData<List<VoicingTemplateEntity>> = voicingTemplateDao.getTemplates()

    // todo: implement other methods from DAO
    

    // todo: insert
    suspend fun insert(template: VoicingTemplateEntity) {
        voicingTemplateDao.insertTemplate(template)
    }

    // todo: delete


    // todo: update

}