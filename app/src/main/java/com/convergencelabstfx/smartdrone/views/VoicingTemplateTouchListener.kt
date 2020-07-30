package com.convergencelabstfx.smartdrone.views

interface VoicingTemplateTouchListener {

//    fun onClick(view: VoicingTemplateView, degree: Int, isChordTone: Boolean)

    fun onChordToneClick(view: VoicingTemplateView, degree: Int)

    fun onBassToneClick(view: VoicingTemplateView, degree: Int)

}