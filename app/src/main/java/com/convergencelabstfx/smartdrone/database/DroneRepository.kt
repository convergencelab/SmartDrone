package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData

class DroneRepository(private val voicingTemplateDao: VoicingTemplateDao) {

    // todo: CHANGE HERE
    val allTemplates: LiveData<List<VoicingTemplateEntity>> = voicingTemplateDao.getTemplates()

    // todo: implement other methods from DAO
    

//    // todo: insert
//    suspend fun insert(template: String) {
//        voicingTemplateDao.insert(template)
//    }

    // todo: delete


    // todo: update

}