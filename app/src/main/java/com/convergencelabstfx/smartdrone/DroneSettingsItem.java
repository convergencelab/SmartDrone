package com.convergencelabstfx.smartdrone;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.smartdrone.views.VoicingTemplateTouchListener;

public interface DroneSettingsItem {

    class ListItem implements DroneSettingsItem {

        private String mTitle;
        private LiveData<String> mSummary;
        private Drawable mIcon;
        private View.OnClickListener mListener;

        public ListItem(String title, LiveData<String> summary, Drawable icon, View.OnClickListener listener) {
            mTitle = title;
            mSummary = summary;
            mIcon = icon;
            mListener = listener;
        }

        public String getTitle() {
            return mTitle;
        }

        public LiveData<String> getSummary() {
            return mSummary;
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public View.OnClickListener getListener() {
            return mListener;
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
