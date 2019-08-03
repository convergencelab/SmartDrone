package com.convergencelabstfx.smartdrone.soundsettings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.models.chords.Chords;
import com.convergencelabstfx.smartdrone.models.chords.ChordsImpl;
import com.convergencelabstfx.smartdrone.models.data.DroneDataSource;
import com.convergencelabstfx.smartdrone.models.data.DroneDataSourceImpl;
import com.convergencelabstfx.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelabstfx.smartdrone.models.droneplayer.DronePlayerImpl;
import com.convergencelabstfx.smartdrone.models.notehandler.NoteHandler;
import com.convergencelabstfx.smartdrone.models.notehandler.NoteHandlerImpl;

public class SoundSettingsActivity extends AppCompatActivity {

    enum IconState {
        MUTED, UNMUTED
    }

    private IconState mState;

    private SoundSettingsPresenter mPresenter;

    private SoundSettingsFragment mView;

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_settings_activity);

        mToolbar = findViewById(R.id.soundsettings_toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Sound Settings");
    }


    @Override
    public void onResume() {
        super.onResume();

        mState = IconState.UNMUTED;

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

                if (mState == IconState.UNMUTED) {
                    item.setIcon(R.drawable.ic_muted);
                    mState = IconState.MUTED;
                }
                else {
                    item.setIcon(R.drawable.ic_unmuted);
                    mState = IconState.UNMUTED;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
