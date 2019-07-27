package com.convergencelab.smartdrone.drone;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.convergencelab.smartdrone.R;
import com.convergencelab.smartdrone.models.signalprocessor.SignalProcessor;
import com.convergencelab.smartdrone.models.chords.Chords;
import com.convergencelab.smartdrone.models.chords.ChordsImpl;
import com.convergencelab.smartdrone.models.data.DroneDataSource;
import com.convergencelab.smartdrone.models.data.DroneDataSourceImpl;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayer;
import com.convergencelab.smartdrone.models.droneplayer.DronePlayerImpl;
import com.convergencelab.smartdrone.models.notehandler.NoteHandler;
import com.convergencelab.smartdrone.models.notehandler.NoteHandlerImpl;
import com.convergencelab.smartdrone.models.signalprocessor.SignalProcessorImpl;

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
        DroneDataSource dataSource = new DroneDataSourceImpl(mPreferences);

        // Putting logic in presenter
        NoteHandler noteHandler = new NoteHandlerImpl(
                /* dataSource.getParentScale(),
                dataSource.getNoteLengthFilter() */);

        DronePlayer dronePlayer = new DronePlayerImpl(/* dataSource.getPluginIx() */);

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
