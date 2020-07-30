package com.convergencelabstfx.smartdrone;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.convergencelabstfx.keyfinder.Scale;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.smartdrone.views.VoicingTemplateTouchListener;

public interface DroneSettingsItem {

    class ListItem implements DroneSettingsItem {

        private String mTitle;
        private String mSummary;
        private Drawable mIcon;
        private View.OnClickListener mListener;
        private LiveData<Scale> mScale;

        public ListItem(String title, String summary, Drawable icon, View.OnClickListener listener, LiveData<Scale> scale) {
            mTitle = title;
            mSummary = summary;
            mIcon = icon;
            mListener = listener;
            mScale = scale;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getSummary() {
            return mSummary;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public View.OnClickListener getListener() {
            return mListener;
        }

        public LiveData<Scale> getScale() {
            return mScale;
        }
    }

    class CheckBoxItem implements DroneSettingsItem {

        public CheckBoxItem() {

        }

    }

    class VoicingTemplateItem implements DroneSettingsItem {

        VoicingTemplateTouchListener mListener;
        View.OnClickListener mHelpListener;
        LiveData<VoicingTemplate> mTemplate;

        public VoicingTemplateItem(
                VoicingTemplateTouchListener listener,
                View.OnClickListener helpListener,
                LiveData<VoicingTemplate> template) {
            mListener = listener;
            mHelpListener = helpListener;
            mTemplate = template;
        }

        public VoicingTemplateTouchListener getListener() {
            return mListener;
        }

        public View.OnClickListener getHelpListener() {
            return mHelpListener;
        }

        public LiveData<VoicingTemplate> getTemplate() {
            return mTemplate;
        }

    }

}
