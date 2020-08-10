package com.convergencelabstfx.smartdrone.adapters

import androidx.databinding.BindingAdapter
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.views.VoicingTemplateTouchListener
import com.convergencelabstfx.smartdrone.views.VoicingTemplateView

@BindingAdapter("app:voicingTemplate")
fun setVoicingTemplate(view: VoicingTemplateView, template: VoicingTemplate) {
    view.showTemplate(template)
}

@BindingAdapter("app:voicingTemplateListener")
fun voicingTemplateListener(view: VoicingTemplateView, listener: VoicingTemplateTouchListener) {
    view.touchListener = listener
}