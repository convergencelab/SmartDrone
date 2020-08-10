package com.convergencelabstfx.smartdrone.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.convergencelabstfx.keyfinder.ParentScale;
import com.convergencelabstfx.keyfinder.Scale;
import com.convergencelabstfx.smartdrone.DroneSettingsItem;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.adapters.DroneSettingsAdapter;
import com.convergencelabstfx.smartdrone.databinding.FragmentDroneSettingsBinding;
import com.convergencelabstfx.smartdrone.models.ChordConstructorType;
import com.convergencelabstfx.smartdrone.viewmodels.DroneViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Settings:
 * .) KeyDetector (activeNoteList, phraseDetector)
 * - List
 * .) if (activeNoteList) -> Mode
 * - List (parent scale) -> List (mode)
 * .) Change Keys on Piano Touch
 * - checkbox
 * .) Template List
 * - List; last option takes to template creator fragment
 */
public class DroneSettingsFragment extends Fragment {

    private DroneViewModel mViewModel;

    private FragmentDroneSettingsBinding mBinding;

    private DroneSettingsAdapter mAdapter;

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

        setupSettingsList();

        mBinding.setLifecycleOwner(this);
        return mBinding.getRoot();
    }

    private DroneSettingsAdapter makeSettingsAdapter() {
        final ArrayList<DroneSettingsItem> settingsList = new ArrayList<>();

        DroneSettingsItem modePicker = new DroneSettingsItem.ListItem(
                "Mode",
//                mViewModel.getCurScale().getValue().getName(),
                null,
                getResources().getDrawable(R.drawable.ic_music_note),
                // todo: show the previously chosen index
                view -> showParentScaleDialog()
        );
        settingsList.add(modePicker);

//        DroneSettingsItem chordConstructorPicker = new DroneSettingsItem.ListItem(
//                "Chord Constructor",
//                "Choose the chord constructor",
//                null,
//                view -> showChordConstructorDialog(),
//
//        );
//        settingsList.add(chordConstructorPicker);

        DroneSettingsItem voicingTemplateItem = new DroneSettingsItem.VoicingTemplateItem(
                new VoicingTemplateTouchListener() {
                    @Override
                    public void onBassToneClick(@NotNull VoicingTemplateView view, int degree) {
                        mViewModel.toggleBassTone(degree);
                    }

                    @Override
                    public void onChordToneClick(@NotNull VoicingTemplateView view, int degree) {
                        mViewModel.toggleChordTone(degree);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTemplateEditorHelpDialog();
                    }
                },
                mViewModel.getCurTemplate()
        );
        settingsList.add(voicingTemplateItem);

        return new DroneSettingsAdapter(getContext(), settingsList);
    }


    private void setupSettingsList() {
        /* Setup Chord Constructor Picker */
        DroneSettingsItem.ListItem chordConstructorPicker = new DroneSettingsItem.ListItem(
                "Chord Constructor",
                Transformations.map(mViewModel.getCurChordConstructorType(), ChordConstructorType::getStr),
                null,
                view -> showChordConstructorDialog()
        );
        mBinding.chordConstructorPicker.setItem(chordConstructorPicker);

        /* Setup Mode Picker */
        DroneSettingsItem.ListItem modePicker = new DroneSettingsItem.ListItem(
                "Mode",
                Transformations.map(mViewModel.getCurScale(), Scale::getName),
                getResources().getDrawable(R.drawable.ic_music_note),
                // todo: show the previously chosen index
                view -> showParentScaleDialog()
        );
        mBinding.modePicker.setItem(modePicker);

        /* Setup Voicing Template Editor */
        DroneSettingsItem.VoicingTemplateItem voicingTemplateItem = new DroneSettingsItem.VoicingTemplateItem(
                new VoicingTemplateTouchListener() {
                    @Override
                    public void onBassToneClick(VoicingTemplateView view, int degree) {
                        mViewModel.toggleBassTone(degree);
                    }

                    @Override
                    public void onChordToneClick(VoicingTemplateView view, int degree) {
                        mViewModel.toggleChordTone(degree);
                    }
                },
                view -> showTemplateEditorHelpDialog(),
                mViewModel.getCurTemplate()
        );
        mBinding.templateEditor.setItem(voicingTemplateItem);
    }

    // todo: extract all of these hardcoded strings
    private void showParentScaleDialog() {
        final List<ParentScale> parentScales = mViewModel.getParentScales();
        final CharSequence[] scaleNames = new CharSequence[parentScales.size()];
        for (int i = 0; i < parentScales.size(); i++) {
            scaleNames[i] = parentScales.get(i).getName();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Parent Scale")
                .setNegativeButton("Dismiss", (dialogInterface, i) -> {
                })
                .setSingleChoiceItems(scaleNames, -1, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    showModeDialog(parentScales.get(i), i);
                }).show();
    }

    private void showModeDialog(ParentScale parentScale, int ix) {
        CharSequence[] modeNames = new CharSequence[parentScale.numModes()];
        for (int i = 0; i < parentScale.numModes(); i++) {
            modeNames[i] = parentScale.getScaleAt(i).getName();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Mode")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setSingleChoiceItems(modeNames, -1, (dialogInterface, i) -> {
                    mViewModel.saveScaleIxs(ix, i);
                    dialogInterface.dismiss();
                }).show();
    }

    private void showTemplateEditorHelpDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                // todo: extract hardcoded string
                .setTitle("Template Editor Help")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                // todo: implement template editor message
                .setMessage("This is the template editor")
                .show();
    }

    private void showChordConstructorDialog() {
        CharSequence[] names = new CharSequence[ChordConstructorType.values().length];
        for (int i = 0; i < names.length; i++) {
            names[i] = ChordConstructorType.values()[i].getStr();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Chord Constructor Type")
                .setNegativeButton("Dismiss", (dialogInterface, i) -> {

                })
                .setSingleChoiceItems(names, -1, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
    }

}