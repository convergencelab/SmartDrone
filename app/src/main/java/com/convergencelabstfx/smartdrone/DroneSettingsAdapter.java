package com.convergencelabstfx.smartdrone;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DroneSettingsAdapter extends ArrayAdapter<DroneSettingsItem> {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_CHECKBOX = 1;

    public DroneSettingsAdapter(Context context, ArrayList<DroneSettingsItem> settingsItems) {
        super(context, 0, settingsItems);
    }

    @Override
    public int getItemViewType(int position) {
        final DroneSettingsItem item = getItem(position);

        if (item instanceof DroneSettingsItem.ListItem) {
            return TYPE_LIST;
        }
        else if (item instanceof DroneSettingsItem.CheckBoxItem) {
            return TYPE_CHECKBOX;
        }
        else {
            throw new IllegalStateException("Unrecognized type given as DroneSettingsItem.");
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // todo: instantiate view
        return null;
    }


}
