package com.convergencelabstfx.smartdrone.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.keyfinder.ParentScale;
import com.convergencelabstfx.smartdrone.DroneSettingsAdapter;
import com.convergencelabstfx.smartdrone.DroneSettingsItem;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDroneSettingsBinding;
import com.convergencelabstfx.smartdrone.viewmodels.DroneViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings:
 *     .) KeyDetector (activeNoteList, phraseDetector)
 *         - List
 *     .) if (activeNoteList) -> Mode
 *         - List (parent scale) -> List (mode)
 *     .) Change Keys on Piano Touch
 *         - checkbox
 *     .) Template List
 *         - List; last option takes to template creator fragment
 *
 */
public class DroneSettingsFragment extends Fragment {

    private DroneViewModel mViewModel;

    private FragmentDroneSettingsBinding mBinding;

    public DroneSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_settings, container, false
        );
        mViewModel = new ViewModelProvider(requireActivity()).get(DroneViewModel.class);

        final ListAdapter settingsAdapter = makeSettingsAdapter();

        mBinding.settingsList.setAdapter(settingsAdapter);

        return mBinding.getRoot();
    }

    private ListAdapter makeSettingsAdapter() {
        final ArrayList<DroneSettingsItem> settingsList = new ArrayList<>();




        DroneSettingsItem modeSetting = new DroneSettingsItem.ListItem(
                "Mode",
                "Choose the mode",
                getResources().getDrawable(R.drawable.ic_music_note),
                view -> {
                    showParentScaleDialog(mViewModel.getParentScales());
                });
        settingsList.add(modeSetting);

        return new DroneSettingsAdapter(getContext(), settingsList);
    }

    private void showParentScaleDialog(List<ParentScale> parentScales) {
        final List<String> tempList = new ArrayList<>();
        tempList.add("One");
        tempList.add("Two");
        tempList.add("Three");
        tempList.add("Four");
        CharSequence[] cs = tempList.toArray(new CharSequence[tempList.size()]);
        final CharSequence[] charSeq = new CharSequence[parentScales.size()];
        for (int i = 0; i <parentScales.size(); i++) {
            charSeq[i] = parentScales.get(i).getName();
        }

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Choose Parent Scale")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setSingleChoiceItems(charSeq, -1,  (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    showModeDialog(parentScales.get(i));
                }).show();
    }

    private void showModeDialog(ParentScale parentScale) {
        final List<String> tempList = new ArrayList<>();
        tempList.add("Travis");
        tempList.add("Jenna");
        tempList.add("Seb");
        tempList.add("Kyle");
        CharSequence[] charSeq = new CharSequence[parentScale.numModes()];
        for (int i = 0; i < parentScale.numModes(); i++) {
            charSeq[i] = parentScale.getScaleAt(i).getName();
        }
        CharSequence[] cs = tempList.toArray(new CharSequence[tempList.size()]);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Choose Mode")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setSingleChoiceItems(charSeq, -1,  (dialogInterface, i) -> {
                    mViewModel.setScale(parentScale.getScaleAt(i));
                    dialogInterface.dismiss();
                }).show();
    }

}