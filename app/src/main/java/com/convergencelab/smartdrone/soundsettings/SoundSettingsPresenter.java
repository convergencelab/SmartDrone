package com.convergencelab.smartdrone.soundsettings;

import com.convergencelab.smartdrone.VoicingHelper;
import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.data.Plugin;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelab.smartdrone.models.notehandler.NoteHandler;
import com.convergencelab.smartdrone.utility.Utility;
import com.example.keyfinder.Voicing;
import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;

public class SoundSettingsPresenter implements SoundSettingsContract.Presenter {
    private int mCurTemplateIx;

    /**
     * PlaybackState of midi playback.
     */
    enum PlaybackState {
        MUTED, UNMUTED;
    }

    /* Models */

    private SoundSettingsContract.View mView;

    private DroneDataSource mDataSource;

    private DronePlayer mPlayer;

    private Chords mChords;

    private PlaybackState mPaybackState;

    private final NoteHandler mNoteHandler;

    /* Variables */

    private int mPluginIx;

    private int mParentScaleIx;

    private int mModeIx;

    private VoicingTemplate mCurTemplate;

    private Plugin[] mPlugins;

    private String[] mParentScaleNames;

    private String[] mModeNames;

    private ArrayList<String> mAllTemplatesEncoded;

    SoundSettingsPresenter(DroneDataSource dataSource,
                           SoundSettingsContract.View view,
                           DronePlayer player,
                           Chords chords,
                           NoteHandler noteHandler) {
        mView = view;
        mDataSource = dataSource;
        mPlayer = player;
        mChords = chords;
        mNoteHandler = noteHandler;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadData();

        mPaybackState = PlaybackState.UNMUTED;
        mView.showParentScale(mParentScaleNames[mParentScaleIx]);
        mView.showMode(mModeNames[mModeIx]);

        Voicing toPlay = mChords.makeVoicing();
        mPlayer.start();
        mPlayer.play(Utility.voicingToIntArray(toPlay));

        // Show views
        mView.showParentScale(mParentScaleNames[mParentScaleIx]);
        mView.showMode(mModeNames[mModeIx]);
        mView.showPlugin(mPlugins[mPluginIx].getName());
    }

    @Override
    public void togglePlayback() {
        if (mPaybackState == PlaybackState.MUTED) {
            mPlayer.unmute();
            mPaybackState = PlaybackState.UNMUTED;
            mView.showPlaybackUnmuted();
        }
        else {
            mPlayer.mute();
            mPaybackState = PlaybackState.MUTED;
            mView.showPlaybackMuted();
        }
    }

    @Override
    public void nextParentScale() {
        mParentScaleIx = (mParentScaleIx + 1) % mParentScaleNames.length;
        mModeNames = mDataSource.getModeNames(mParentScaleIx);

        // View
        mView.showMode(mModeNames[mModeIx]);
        mView.showParentScale(mParentScaleNames[mParentScaleIx]);

        // Model
        mNoteHandler.setParentScale(mParentScaleIx);
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mModeIx));
        Voicing toPlay = mChords.makeVoicing();
        mPlayer.play(Utility.voicingToIntArray(toPlay));
    }

    @Override
    public void nextMode() {
        mModeIx = (mModeIx + 1) % mModeNames.length;

        // View
        mView.showMode(mModeNames[mModeIx]);

        // Model
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mModeIx));
        Voicing toPlay = mChords.makeVoicing();
        mPlayer.play(Utility.voicingToIntArray(toPlay));
    }

    @Override
    public void nextPlugin() {
        mPluginIx = (mPluginIx + 1) % mPlugins.length;

        // View
        mView.showPlugin(mPlugins[mPluginIx].getName());

        // Model
        mPlayer.setPlugin(mPlugins[mPluginIx].getPlugin());
    }

    @Override
    public void selectTemplate(int templateIx) {
        mView.showTemplateActive(templateIx);

        VoicingTemplate selectedTemplate =
                VoicingHelper.decodeTemplate(mAllTemplatesEncoded.get(templateIx));
        mChords.setVoicingTemplate(selectedTemplate);
        mPlayer.play(Utility.voicingToIntArray(mChords.makeVoicing()));
        mCurTemplate = selectedTemplate;
    }

    @Override
    public ArrayList<VoicingTemplate> getAllTemplates() {
        mCurTemplateIx = -1;
        ArrayList<VoicingTemplate> toReturn = new ArrayList<>();
        for (int i = 0; i < mAllTemplatesEncoded.size(); i++) {
            VoicingTemplate curTemplate = VoicingHelper.decodeTemplate(mAllTemplatesEncoded.get(i));
            toReturn.add(curTemplate);
            if (mCurTemplate.getName().equals(curTemplate.getName())) {
                mCurTemplateIx = i;
            }
        }
        return toReturn;
    }

    @Override
    public void finish() {
        // Save all prefs
        mDataSource.saveModeIx(mModeIx);
        mDataSource.saveParentScale(mParentScaleIx);
        mDataSource.savePluginIx(mPluginIx);
        mDataSource.saveTemplate(mCurTemplate);

        mPlayer.stop();
    }

    @Override
    public void getCurrentTemplate() {
        if (mCurTemplateIx != -1) {
            mView.showTemplateActive(mCurTemplateIx);
        }
    }

    private void loadData() {
        // Midi Driver
        mPlugins = mDataSource.getPlugins();
        mPlayer.setPlugin(mPlugins[mDataSource.getPluginIx()].getPlugin());
        mPluginIx = mDataSource.getPluginIx();

        // KeyFinder
        mParentScaleIx = mDataSource.getParentScale();
        mNoteHandler.setParentScale(mParentScaleIx);
        mModeIx = mDataSource.getModeIx();
        mParentScaleNames = mDataSource.getParentScaleNames();
        mModeNames = mDataSource.getModeNames(mParentScaleIx);

        // Harmony
        mAllTemplatesEncoded = mDataSource.getAllTemplates();

        mCurTemplate = mDataSource.getTemplate();
        mChords.setVoicingTemplate(mCurTemplate);
        mChords.setKey(0);
        mChords.setModeTemplate(mNoteHandler.getModeTemplate(mModeIx));
    }
}
