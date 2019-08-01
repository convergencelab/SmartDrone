package com.convergencelabstfx.smartdrone.soundsettings;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.templatecreator.TemplateCreatorActivity;
import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;

public class SoundSettingsFragment extends Fragment implements SoundSettingsContract.View {

    private View mRoot;
    private LinearLayout mParentScalePref;
    private LinearLayout mModePref;
    private LinearLayout mPluginPref;

    DialogInterface.OnClickListener dialogClickListener;

    private SoundSettingsContract.Presenter mPresenter;
    private LayoutInflater mInflater;

    private CardView[] mTemplates;
    private int mSelectedTemplateIx;

    public SoundSettingsFragment() {
        // Requires empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.sound_settings_frag, container, false);
        mInflater = inflater;

        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        mSelectedTemplateIx = -1;
        setupView();
        mPresenter.start();
        setupTemplates();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.finish();
    }

    @Override
    public void showParentScale(String name) {
        setPrefText(mParentScalePref, name);
    }

    @Override
    public void showMode(String name) {
        setPrefText(mModePref, name);
    }

    @Override
    public void showPlugin(String name) {
        setPrefText(mPluginPref, name);
    }

    @Override
    public void showTemplateActive(int templateIx) {
        int activeColor = getResources().getColor(R.color.colorActiveNote);
        int inactiveColor = getResources().getColor(R.color.dark_white);

        int oldTemplate = mSelectedTemplateIx;
        mSelectedTemplateIx = templateIx;

        // Show old template unselected.
        if (oldTemplate != -1) {
            ValueAnimator inactiveAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), activeColor, inactiveColor);
            inactiveAnimator.setDuration(200); // milliseconds
            inactiveAnimator.addUpdateListener
                    (animator -> mTemplates[oldTemplate].setCardBackgroundColor((int) animator.getAnimatedValue()));
            inactiveAnimator.start();
        }

        // Show new template selected.
        ValueAnimator activeAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), inactiveColor, activeColor);
        activeAnimator.setDuration(200); // milliseconds
        activeAnimator.addUpdateListener
                (animator -> mTemplates[templateIx].setCardBackgroundColor((int) animator.getAnimatedValue()));
        activeAnimator.start();
    }

    @Override
    public void showPlaybackUnmuted() {

    }

    @Override
    public void showPlaybackMuted() {

    }

    @Override
    public void showTemplateCreatorActivity() {
        mPresenter.finish();
        Intent intent = new Intent(getContext(), TemplateCreatorActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDroneMainActivity() {

    }

    @Override
    public void refreshTemplates() {
        setupTemplates();
    }

    @Override
    public void setPresenter(SoundSettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Return instance of fragment.
     *
     * @return new instance.
     */
    public static SoundSettingsFragment newInstance() {
        return new SoundSettingsFragment();
    }

    private void setupView() {
        setupPrefs();
    }

    private void setupPrefs() {
        // Parent Scale Pref
        mParentScalePref = (LinearLayout) mInflater
                .inflate(R.layout.sound_pref_item, (ViewGroup) mRoot, false);
        TextView prefTitle = (TextView) mParentScalePref.getChildAt(0);
        prefTitle.setText("Parent Scale");
        mParentScalePref.setOnClickListener(v -> mPresenter.nextParentScale());
        ((ViewGroup) mRoot).addView(mParentScalePref);

        addDivider();

        // Mode pref
        mModePref = (LinearLayout) mInflater
                .inflate(R.layout.sound_pref_item, (ViewGroup) mRoot, false);
        prefTitle = (TextView) mModePref.getChildAt(0);
        prefTitle.setText("Mode");
        mModePref.setOnClickListener(v -> mPresenter.nextMode());
        ((ViewGroup) mRoot).addView(mModePref);

        addDivider();

        // Plugin Pref
        mPluginPref = (LinearLayout) mInflater
                .inflate(R.layout.sound_pref_item, (ViewGroup) mRoot, false);
        prefTitle = (TextView) mPluginPref.getChildAt(0);
        prefTitle.setText("Plugin");
        mPluginPref.setOnClickListener(v -> mPresenter.nextPlugin());
        ((ViewGroup) mRoot).addView(mPluginPref);

        addDivider();

        LinearLayout voicingLabel = (LinearLayout) mInflater.inflate(R.layout.sound_pref_item_nodescription, (ViewGroup) mRoot, false);
        TextView voicingLabelText = (TextView) voicingLabel.getChildAt(0);
        voicingLabelText.setText("Voicing Template:");
        ((ViewGroup) mRoot).addView(voicingLabel);

        addDivider();
    }

    private void setupTemplates() {
        mSelectedTemplateIx = -1;
        if (mRoot.findViewById(R.id.template_scroll_view) != null) {
            ((ViewGroup) mRoot).removeView(mRoot.findViewById(R.id.template_scroll_view));
        }
        ScrollView templateContainer = (ScrollView) mInflater.inflate(R.layout.template_container, (ViewGroup) mRoot, false);
        ArrayList<VoicingTemplate> templates = mPresenter.getAllTemplates();
        mTemplates = new CardView[templates.size()];
        for (int i = 0; i < templates.size(); i++) {
            VoicingTemplate curTemplate = templates.get(i);

            mTemplates[i] =
                    (CardView) mInflater.inflate(R.layout.template_item, (ViewGroup) mRoot, false);
            mTemplates[i].setTag(i);

            TextView templateName = mTemplates[i].findViewById(R.id.template_name);
            templateName.setText(curTemplate.getName());

            TextView templateTones = mTemplates[i].findViewById(R.id.template_tones);
            String tonesStr = "";

            // Bass Tones
            if (curTemplate.getBassTones().length != 0) {
                tonesStr += "Bass ";
                for (Tone tone : curTemplate.getBassTones()) {
                    // + 1 for base 1 indexing
                    tonesStr += (tone.getDegree() + 1) + " ";
                }
                if (curTemplate.getChordTones().length != 0) {
                    tonesStr += ": ";
                }
            }

            // Chord Tones
            if (curTemplate.getChordTones().length != 0) {
                tonesStr += "Chord ";
                for (Tone tone : curTemplate.getChordTones()) {
                    // + 1 for base 1 indexing
                    tonesStr += (tone.getDegree() + 1) + " ";
                }
            }
            templateTones.setText(tonesStr);

            int finalIx = i;
            mTemplates[i]
                    .setOnClickListener(v -> mPresenter.selectTemplate((int) mTemplates[finalIx].getTag()));

            mTemplates[i]
                    .setOnLongClickListener(v -> {
                        dialogClickListener = (dialog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    mSelectedTemplateIx = -1;
                                    if (mTemplates.length > 1) {
                                        mPresenter.deleteTemplate(finalIx);
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    // Do nothing.
                                    break;
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Delete Template?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                        return true;
                    });

            ((LinearLayout) templateContainer.getChildAt(0)).addView(mTemplates[i]);
        }
        ((ViewGroup) mRoot).addView(templateContainer);

        mPresenter.getCurrentTemplate();
    }

    private void setPrefText(LinearLayout layout, String newText) {
        TextView toChange = (TextView) layout.getChildAt(1);
        toChange.setText(newText);
    }

    private void addDivider() {
        ((ViewGroup) mRoot).addView(
                mInflater.inflate(R.layout.layout_divider, (ViewGroup) mRoot, false));
    }


}
