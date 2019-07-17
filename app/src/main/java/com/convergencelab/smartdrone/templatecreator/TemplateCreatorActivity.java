package com.convergencelab.smartdrone.templatecreator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.convergencelab.smartdrone.R;
import com.example.keyfinder.Tone;

public class TemplateCreatorActivity extends AppCompatActivity
        implements TemplateCreatorContract.View {

    private static final int NUM_TONES = 14;
    private static final int MAX_LEN_NAME = 20;

    private boolean[] mToneIsActive;
    private Tone[] mTones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_creator);

        mToneIsActive = new boolean[NUM_TONES]; // Todo: Will have to make it work for bass notes as well
        mToneIsActive[0] = true; // Todo: clean up,

        initializeTones();

    }

    @Override
    public void showEmptyNameError() {

    }

    @Override
    public void showIllegalCharacterError() {

    }

    @Override
    public void showDuplicateNameError() {

    }

    @Override
    public void showEmptyTemplateError() {

    }

    /**
     * Ends template creator activity and goes back to drone sound activity.
     */
    @Override
    public void showDroneSoundSettings() {

    }

    @Override
    public void setPresenter(TemplateCreatorContract.Presenter presenter) {

    }

    /**
     * Constructs tone list with object for each tone.
     */
    // Todo: move to activity
    private void initializeTones() {
        for (int i = 0; i < NUM_TONES; i++) {
            mTones[i] = new Tone(i, Tone.TONE_CHORD);
        }
    }
}
