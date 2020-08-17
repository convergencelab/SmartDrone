package com.convergencelabstfx.smartdrone

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.LiveData
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.views.VoicingTemplateTouchListener

interface DroneSettingsItem {

    data class ListItem(
            val title: String,
            val summary: LiveData<String>,
            val icon: Drawable?,
            val listener: View.OnClickListener
    ) : DroneSettingsItem

    data class CheckBoxItem(
            val title: String
    ) : DroneSettingsItem

    data class VoicingTemplateItem(
            var listener: VoicingTemplateTouchListener,
            var helpListener: View.OnClickListener,
            var template: LiveData<VoicingTemplate>
    ) : DroneSettingsItem

    class SliderItem(
            val title: String,
            val summary: LiveData<String>,
            val curVal: LiveData<Int>,
            val minVal: Int,
            val maxVal: Int
    ) : DroneSettingsItem {
    }

}