package com.convergencelabstfx.smartdrone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate

@Entity(tableName = "voicing_template_table")
class VoicingTemplateEntity(
        // todo: add an id later
        //       i guess if autoGenerate = 0, setting this field as '0' will generate id
        @PrimaryKey
        @ColumnInfo(name = "template") val template: VoicingTemplate
)