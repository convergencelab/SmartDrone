package com.convergencelabstfx.smartdrone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.convergencelabstfx.smartdrone.DroneSettingsItem;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.SettingsItemListBinding;
import com.convergencelabstfx.smartdrone.databinding.SettingsItemTemplateBinding;
import com.convergencelabstfx.smartdrone.views.VoicingTemplateView;

import java.util.ArrayList;

public class DroneSettingsAdapter extends ArrayAdapter<DroneSettingsItem> {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_CHECKBOX = 1;
    private static final int TYPE_TEMPLATE = 2;

    private VoicingTemplateView mTemplateView;
    private TextView mScaleText;

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
        else if (item instanceof DroneSettingsItem.VoicingTemplateItem) {
            return TYPE_TEMPLATE;
        }
        else {
            throw new IllegalStateException("Unrecognized type given as DroneSettingsItem.");
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup viewGroup) {
        // todo: instantiate view
        switch (getItemViewType(i)) {
            case TYPE_LIST:
                return makeListItem((DroneSettingsItem.ListItem) getItem(i), viewGroup);
            case TYPE_CHECKBOX:
                return makeCheckBoxItem((DroneSettingsItem.CheckBoxItem) getItem(i));
            case TYPE_TEMPLATE:
                return makeTemplateItem((DroneSettingsItem.VoicingTemplateItem) getItem(i), viewGroup);
            default:
                throw new IllegalStateException("Unrecognized type in DroneSettingsAdapter.");
        }
    }

    private View makeListItem(DroneSettingsItem.ListItem listItem, ViewGroup container) {
        final SettingsItemListBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(getContext()),
                        R.layout.settings_item_list,
                        container,
                        false);
        binding.setItem(listItem);
        // todo: for some reason, couldn't get the databinding expression to work,
        //       also; why the F was this drawable turning white ????????????
        //       WHERE DID THE DRAWABLES GO
        binding.icon.setImageDrawable(listItem.getIcon());
        binding.icon.getDrawable().setTint(Color.BLACK);
        mScaleText = binding.summary;
        return binding.getRoot();
    }

    private View makeCheckBoxItem(DroneSettingsItem.CheckBoxItem checkBoxItem) {
        return new TextView(getContext());
    }

    // todo: implement
    private View makeTemplateItem(DroneSettingsItem.VoicingTemplateItem item, ViewGroup container) {
//        final SettingsItemTemplateBinding binding =
//                DataBindingUtil.inflate(
//                        LayoutInflater.from(getContext()),
//                        R.layout.settings_item_template,
//                        container,
//                        false);
//        binding.templateEditor.addListener(item.getListener());
//        binding.questionMark.setOnClickListener(item.getHelpListener());
//        mTemplateView = binding.templateEditor;
//        binding.setItem(item);
//        return binding.getRoot();
        return null;
    }

    public VoicingTemplateView getVoicingTemplateView() {
        return mTemplateView;
    }

    public TextView getScaleText() {
        return mScaleText;
    }

}
