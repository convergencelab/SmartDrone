package com.convergencelabstfx.smartdrone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate

@Entity(tableName = "voicing_template_table")
class VoicingTemplateEntity(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "template") val template: VoicingTemplate
)