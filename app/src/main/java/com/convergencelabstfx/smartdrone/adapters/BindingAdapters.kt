package com.convergencelabstfx.smartdrone.adapters

import androidx.databinding.BindingAdapter
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.views.VoicingTemplateView

class BindingAdapters {

}

@BindingAdapter("app:voicingTemplate")
fun setVoicingTemplate(view: VoicingTemplateView, template: VoicingTemplate) {
    view.showTemplate(template)
}