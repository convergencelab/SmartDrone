package com.convergencelabstfx.smartdrone;

import android.graphics.drawable.Drawable;

public interface DroneSettingsItem {

    class ListItem implements DroneSettingsItem {

        private String mTitle;
        private String mSummary;
        private Drawable mIcon;

        public ListItem(String title, String summary, Drawable icon) {
            mTitle = title;
            mSummary = summary;
            mIcon = icon;
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

    }

    class CheckBoxItem {

        public CheckBoxItem() {

        }

    }

}
