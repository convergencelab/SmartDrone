package com.convergencelab.smartdrone.dronesoundsettings;

import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.data.Plugin;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.example.keyfinder.KeyCollection;

import java.util.ArrayList;

public class DroneSoundSettingsPresenter implements DroneSoundSettingsContract.Presenter {
    /**
     * PlaybackState of midi playback.
     */
    enum PlaybackState {
        MUTED, UNMUTED
    }

    /* Models */

    private DroneSoundSettingsContract.View mView;

    private DroneDataSource mDataSource;

    private DronePlayer mPlayer;

    private Chords mChords;

    private PlaybackState mPaybackState;


    /* Variables */

    private int mPluginIx;

    private int mParentScaleIx;

    private int mModeIx;

    private Plugin[] mPlugins;

    private String[] mParentScaleNames;

    private String[] mModeNames;

    private ArrayList<String> mAllTemplatesEncoded;

    private KeyCollection mKeys;

    DroneSoundSettingsPresenter(DroneDataSource dataSource,
                                DroneSoundSettingsContract.View view,
                                DronePlayer player,
                                Chords chords) {
        mView = view;
        mDataSource = dataSource;
        mPlayer = player;
        mChords = chords;

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        loadData();

        mPaybackState = PlaybackState.UNMUTED;
        mView.showParentScale(mParentScaleNames[mParentScaleIx]);
        mView.showMode(mModeNames[mModeIx]);
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
        mView.showParentScale(mParentScaleNames[mParentScaleIx]);
    }

    @Override
    public void nextMode() {
        mModeIx = (mModeIx + 1) % mModeNames.length;
        mView.showMode(mModeNames[mModeIx]);
    }

    @Override
    public void nextPlugin() {
        mPluginIx = (mPluginIx + 1) % mPlugins.length;
        mView.showPlugin(mPlugins[mPluginIx].getName());
    }

    @Override
    public void selectTemplate() {

    }

    @Override
    public void newTemplate() {

    }

    private void loadData() {
        // Midi Driver
        mPlugins = mDataSource.getPlugins();
        mPlayer.setPlugin(mPlugins[mDataSource.getPluginIx()].getPlugin());
        mPluginIx = mDataSource.getPluginIx();

        mAllTemplatesEncoded = mDataSource.getAllTemplates();

        // KeyFinder Stuff
        mKeys = new KeyCollection();

        mParentScaleIx = mDataSource.getParentScale();
        mModeIx = mDataSource.getModeIx();
        mParentScaleNames = mDataSource.getParentScaleNames();
        mModeNames = mDataSource.getModeNames(mParentScaleIx);

        mChords.setVoicingTemplate(mDataSource.getTemplate());
//        mChords.setKey();

    }
}
