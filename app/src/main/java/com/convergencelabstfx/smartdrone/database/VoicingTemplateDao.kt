package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VoicingTemplateDao {

    @Query("SELECT * FROM voicing_template_table")
    fun getTemplates(): LiveData<List<VoicingTemplateEntity>> // todo: CHANGE HERE

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTemplate(template: VoicingTemplateEntity)

    @Delete
    fun deleteTemplate(template: VoicingTemplateEntity)

    @Update
    fun updateTemplate(template: VoicingTemplateEntity)

}