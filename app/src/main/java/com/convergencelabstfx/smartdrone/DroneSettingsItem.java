package com.convergencelabstfx.smartdrone;

public interface DroneSettingsItem {

    class ListItem {

        private String mTitle;
        private String mSummary;
        private int mIcon;

        public ListItem(String title, String summary, int icon) {
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

        public int getIcon() {
            return mIcon;
        }

    }

    class CheckBoxItem {

        public CheckBoxItem() {

        }

    }

}
