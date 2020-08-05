package com.convergencelabstfx.smartdrone

import androidx.room.TypeConverter
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate

fun voicingTemplateToString(template: VoicingTemplate): String {
    val sb = StringBuilder()
    sb.append(template.bassTones.joinToString(prefix = "b", separator = ","))
    sb.append(template.chordTones.joinToString(prefix = "c", separator = ","))
    return sb.toString()
}

fun stringToVoicingTemplate(string: String): VoicingTemplate {
    val ixOfC = string.indexOf("c")
    val regex = "\\s*,\\s*".toRegex()
    val bassTones = string.substring(1, ixOfC).split(regex).map { it.toInt() }
    val chordTones = string.substring(ixOfC + 1).split(regex).map { it.toInt() }

    val template = VoicingTemplate()
    template.bassTones = bassTones
    template.chordTones = chordTones
    return template
}