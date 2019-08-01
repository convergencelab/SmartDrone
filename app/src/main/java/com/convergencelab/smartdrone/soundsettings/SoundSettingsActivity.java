package com.convergencelab.smartdrone.soundsettings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.convergencelab.smartdrone.R;
import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.models.chords.ChordsImpl;
import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.data.DroneDataSourceImpl;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayerImpl;
import com.convergencelab.smartdrone.models.notehandler.NoteHandler;
import com.convergencelab.smartdrone.models.notehandler.NoteHandlerImpl;

public class SoundSettingsActivity extends AppCompatActivity {

    private SoundSettingsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        SoundSettingsFragment soundFragment =
                (SoundSettingsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (soundFragment == null) {
            soundFragment = SoundSettingsFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, soundFragment);
            transaction.commit();
        }

        SharedPreferences mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        DroneDataSource dataSource = new DroneDataSourceImpl(mPreferences, true); // Todo: try changing to false later

        NoteHandler noteHandler = new NoteHandlerImpl();

        DronePlayer dronePlayer = new DronePlayerImpl();

        Chords chords = new ChordsImpl();

        mPresenter = new SoundSettingsPresenter(
                dataSource,
                soundFragment,
                dronePlayer,
                chords,
                noteHandler);
    }


}
