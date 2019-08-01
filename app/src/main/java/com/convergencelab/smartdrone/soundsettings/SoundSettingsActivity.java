package com.convergencelab.smartdrone.soundsettings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

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

    private SoundSettingsFragment mView;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_settings_activity);

        mToolbar = findViewById(R.id.soundsettings_toolbar);
        setSupportActionBar(mToolbar);
    }


    @Override
    public void onResume() {
        super.onResume();

        mView = (SoundSettingsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.contentFrame);

        if (mView != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(mView);
            transaction.commit();
        }


        mView = SoundSettingsFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, mView);
        transaction.commit();


        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DroneDataSource dataSource = new DroneDataSourceImpl(mPreferences, true); // Todo: try changing to false later

        NoteHandler noteHandler = new NoteHandlerImpl();

        DronePlayer dronePlayer = new DronePlayerImpl();

        Chords chords = new ChordsImpl();

        mPresenter = new SoundSettingsPresenter(
                dataSource,
                mView,
                dronePlayer,
                chords,
                noteHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_soundsettings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_new_template:
                // This method stops presenter.
                mView.showTemplateCreatorActivity();
                return true;

            case R.id.action_mute:
                mPresenter.togglePlayback();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
