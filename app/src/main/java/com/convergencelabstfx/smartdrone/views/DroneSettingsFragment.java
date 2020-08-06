package com.convergencelabstfx.smartdrone.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.convergencelabstfx.keyfinder.ParentScale;
import com.convergencelabstfx.keyfinder.Scale;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.smartdrone.DroneSettingsItem;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.adapters.DroneSettingsAdapter;
import com.convergencelabstfx.smartdrone.database.VoicingTemplateEntity;
import com.convergencelabstfx.smartdrone.databinding.FragmentDroneSettingsBinding;
import com.convergencelabstfx.smartdrone.viewmodels.DroneViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

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

        mAdapter = makeSettingsAdapter();

        mBinding.settingsList.setAdapter(mAdapter);

        mBinding.setLifecycleOwner(getViewLifecycleOwner());


        // todo: remove these if xml databinding will work
        mViewModel.getCurTemplate().observe(getViewLifecycleOwner(), new Observer<VoicingTemplate>() {
            @Override
            public void onChanged(VoicingTemplate voicingTemplate) {
                VoicingTemplateView v = mAdapter.getVoicingTemplateView();
                if (v != null) {
                    v.clear();
                    v.showTemplate(voicingTemplate);
                }
            }
        });

        mViewModel.getCurScale().observe(getViewLifecycleOwner(), new Observer<Scale>() {
            @Override
            public void onChanged(Scale scale) {
                if (scale != null && mAdapter.getScaleText() != null) {
                    mAdapter.getScaleText().setText(scale.getName());
                }
            }
        });


        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private DroneSettingsAdapter makeSettingsAdapter() {
        final ArrayList<DroneSettingsItem> settingsList = new ArrayList<>();

        DroneSettingsItem modePicker = new DroneSettingsItem.ListItem(
                "Mode",
                "Choose the mode",
                getResources().getDrawable(R.drawable.ic_music_note),
                // todo: show the previously chosen index
                view -> showParentScaleDialog(),
                Transformations.map(mViewModel.getCurScale(), new Function<Scale, String>() {
                    @Override
                    public String apply(Scale input) {
                        return input.getName();
                    }
                })
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
                        mViewModel.onBassToneClick(degree);
                    }
                    @Override
                    public void onChordToneClick(@NotNull VoicingTemplateView view, int degree) {
                        mViewModel.onChordToneClick(degree);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTemplateEditorDialog();
                    }
                },
                mViewModel.getCurTemplate()
        );
        settingsList.add(voicingTemplateItem);

        return new DroneSettingsAdapter(getContext(), settingsList);
    }

    private void showParentScaleDialog() {
        final List<ParentScale> parentScales = mViewModel.getParentScales();
        final CharSequence[] scaleNames = new CharSequence[parentScales.size()];
        for (int i = 0; i < parentScales.size(); i++) {
            scaleNames[i] = parentScales.get(i).getName();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose Parent Scale")
                .setNegativeButton("Dismiss", (dialogInterface, i) -> { })
                .setSingleChoiceItems(scaleNames, -1,  (dialogInterface, i) -> {
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
                .setSingleChoiceItems(modeNames, -1,  (dialogInterface, i) -> {
                    mViewModel.saveScaleIxs(ix, i);
                    dialogInterface.dismiss();
                }).show();
    }

    private void showVoicingTemplateDialog(List<VoicingTemplateEntity> templates) {
        CharSequence[] strList = new CharSequence[templates.size()];
        for (int i = 0; i < templates.size(); i++) {
            final StringBuilder sb = new StringBuilder();
            if (templates.get(i).getTemplate().getBassTones().size() != 0) {
                sb.append("Bass: ");
                sb.append(templates.get(i).getTemplate().getBassTones().get(0) + 1);
                for (int j = 1; j < templates.get(i).getTemplate().getBassTones().size(); j++) {
                    sb.append(", ");
                    sb.append(templates.get(i).getTemplate().getBassTones().get(j) + 1);
                }
                sb.append('\n');
            }
            if (templates.get(i).getTemplate().getChordTones().size() != 0) {
                sb.append("Chord: ");
                sb.append(templates.get(i).getTemplate().getChordTones().get(0) + 1);
                for (int j = 1; j < templates.get(i).getTemplate().getChordTones().size(); j++) {
                    sb.append(", ");
                    sb.append(templates.get(i).getTemplate().getChordTones().get(j) + 1);
                }
            }
            strList[i] = sb.toString();
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Title")
                .setPositiveButton("Close", (dialogInterface, i) -> {
                    // nothing on click
                })
                .setSingleChoiceItems(
                        strList,
                        -1,
                        (dialogInterface, i) -> {
                            mViewModel.setVoicingTemplate(templates.get(i).getTemplate(), true);
                        })
                .show();
    }

    private void showTemplateEditorDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Template Editor")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                // todo: implement template editor message
                .setMessage("This is the template editor")
                .show();
    }

    private View makeTemplateRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(getContext());

        return null;
    }

}