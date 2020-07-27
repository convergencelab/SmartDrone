package com.convergencelabstfx.smartdrone.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VoicingTemplateDao {

    @Query("SELECT * from voicing_template_table")
    fun getTemplates(): LiveData<List<VoicingTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(template: VoicingTemplateEntity)

    @Delete
    fun deleteTemplates(vararg templates: VoicingTemplateEntity)

    @Update
    fun updateTemplates(vararg templates: VoicingTemplateEntity)

}