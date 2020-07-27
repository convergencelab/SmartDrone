package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface VoicingTemplateDao {

    @Query("SELECT * from voicing_template_table")
    fun getTemplates(): LiveData<List<VoicingTemplateEntity>> // todo: CHANGE HERE

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insert(template: String)

//    @Delete
//    suspend fun deleteTemplates(vararg templates: VoicingTemplateEntity)
//
//    @Update
//    suspend fun updateTemplates(vararg templates: VoicingTemplateEntity)

}