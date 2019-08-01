package com.convergencelab.smartdrone.soundSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.convergencelab.smartdrone.R;
import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;

public class SoundSettingsFragment extends Fragment implements SoundSettingsContract.View {

    private View mRoot;
    private LinearLayout mParentScalePref;
    private LinearLayout mModePref;
    private LinearLayout mPluginPref;

    private SoundSettingsContract.Presenter mPresenter;
    private LayoutInflater mInflater;

    private CardView[] mTemplates;
    private int mSelectedTemplateIx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.sound_settings_frag, container, false);
        mInflater = inflater;

        setupView();

        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
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

    }

    @Override
    public void showPlaybackUnmuted() {

    }

    @Override
    public void showPlaybackMuted() {

    }

    @Override
    public void showTemplateCreatorActivity() {

    }

    @Override
    public void showDroneMainActivity() {

    }

    @Override
    public void setPresenter(SoundSettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Return instance of fragment.
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
    }

    private void setupTemplates() {
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
                    tonesStr += (tone.getDegree()) + " ";
                }
                tonesStr += ": ";
            }

            // Chord Tones
            if (curTemplate.getChordTones().length != 0) {
                tonesStr += "Chord ";
                for (Tone tone : curTemplate.getChordTones()) {
                    tonesStr += (tone.getDegree()) + " ";
                }
            }
            templateTones.setText(tonesStr);

            ((ViewGroup) mRoot).addView(mTemplates[i]);
        }

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
