package com.convergencelabstfx.smartdrone.drone;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.models.signalprocessor.SignalProcessor;
import com.convergencelabstfx.smartdrone.models.chords.Chords;
import com.convergencelabstfx.smartdrone.models.chords.ChordsImpl;
import com.convergencelabstfx.smartdrone.models.data.DroneDataSource;
import com.convergencelabstfx.smartdrone.models.data.DroneDataSourceImpl;
import com.convergencelabstfx.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelabstfx.smartdrone.models.droneplayer.DronePlayerImpl;
import com.convergencelabstfx.smartdrone.models.notehandler.NoteHandler;
import com.convergencelabstfx.smartdrone.models.notehandler.NoteHandlerImpl;
import com.convergencelabstfx.smartdrone.models.signalprocessor.SignalProcessorImpl;

public class DroneActivity extends AppCompatActivity {

    private DronePresenter mDronePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drone);

        DroneFragment droneFragment =
                (DroneFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.contentFrame);

        if (droneFragment == null) {
            droneFragment = DroneFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, droneFragment);
            transaction.commit();
        }

        // Todo: May be a better way to do this.
        SharedPreferences mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        DroneDataSource dataSource = new DroneDataSourceImpl(mPreferences, false);

        NoteHandler noteHandler = new NoteHandlerImpl();

        DronePlayer dronePlayer = new DronePlayerImpl();

        // Needs activity to run on UI thread.
        SignalProcessor signalProcessor = new SignalProcessorImpl(this);

        Chords chords = new ChordsImpl();

        mDronePresenter = new DronePresenter(
                dataSource,
                droneFragment,
                noteHandler,
                dronePlayer,
                signalProcessor,
                chords);
    }
}
