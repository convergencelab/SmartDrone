package com.convergencelabstfx.smartdrone.database

import androidx.room.TypeConverter
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import timber.log.Timber

class Converters {

    @TypeConverter
    fun voicingTemplateToString(template: VoicingTemplate): String {
        val sb = StringBuilder()
        sb.append(template.bassTones.joinToString(prefix = "b", separator = ","))
        sb.append(template.chordTones.joinToString(prefix = "c", separator = ","))
        Timber.i(template.toString())
        return sb.toString()
    }

    @TypeConverter
    fun stringToVoicingTemplate(string: String): VoicingTemplate {
        val ixOfC = string.indexOf("c")
        val regex = "\\s*,\\s*".toRegex()
        val bassTones = string.substring(1, ixOfC).split(regex).map { it.toInt() }
        val chordTones = string.substring(ixOfC + 1).split(regex).map { it.toInt() }

        val template = VoicingTemplate()
        template.bassTones = bassTones
        template.chordTones = chordTones
        Timber.i(string)
        return template
    }

}