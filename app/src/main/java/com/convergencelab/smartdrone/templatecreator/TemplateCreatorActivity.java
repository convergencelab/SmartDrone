package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.convergencelab.smartdrone.R;

public class TemplateCreatorActivity extends AppCompatActivity {

    private TemplateCreatorPresenter mTemplateCreatorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_creator);

        TemplateCreatorFragment templateCreatorFragment = (TemplateCreatorFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (templateCreatorFragment == null) {
            templateCreatorFragment = TemplateCreatorFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, templateCreatorFragment);
            transaction.commit();
        }

//        mAddEditTaskPresenter = new AddEditTaskPresenter(
//                taskId,
//                Injection.provideTasksRepository(getApplicationContext()),
//                addEditTaskFragment,
//                shouldLoadDataFromRepo);

        SharedPreferences mPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        TemplateCreatorDataSource dataSource = new TemplateCreatorDataSourceImpl(mPreferences);
        mTemplateCreatorPresenter = new TemplateCreatorPresenter(dataSource, templateCreatorFragment);

//        mToneIsActive = new boolean[NUM_TONES]; // Todo: Will have to make it work for bass notes as well
//        mToneIsActive[0] = true; // Todo: clean up,
//
//        initializeTones();
//        drawTones();
    }
//
//    @Override
//    public void showEmptyNameError() {
//    }
//
//    @Override
//    public void showIllegalCharacterError() {
//
//    }
//
//    @Override
//    public void showDuplicateNameError() {
//        Toast.makeText(this, "Name already taken: Please choose another name.", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showEmptyTemplateError() {
//        Toast.makeText(this, "Voicing must have at least one voice.", Toast.LENGTH_LONG).show();
//    }
//
//    private void showDroneSoundSettings(View view) {
//        showDroneSoundSettings();
//    }
//
//    /**
//     * Ends template creator activity and goes back to drone sound activity.
//     */
//    @Override
//    public void showDroneSoundSettings() {
//
//    }
//
//    @Override
//    public void setPresenter(TemplateCreatorContract.Presenter presenter) {
//        mPresenter = presenter;
//    }
//
//    /**
//     * Constructs tone list with object for each tone.
//     */
//    // Todo: move to activity
//    private void initializeTones() {
//        for (int i = 0; i < NUM_TONES; i++) {
//            mTones[i] = new Tone(i, Tone.TONE_CHORD);
//        }
//    }
//
//    // Todo: make better in future.
//    private void drawTones() {
//        LinearLayout curLayout;
//        String layoutTemp = "tone_column_";
//        for (int columnCount = 0; columnCount < 4; columnCount++) {
//            String curLayoutStr = layoutTemp + Integer.toString(columnCount);
//            int resId = getResources().getIdentifier(curLayoutStr, "id", getPackageName());
//            curLayout = findViewById(resId);
//
//            for (int toneCount = 0; toneCount < TONE_COLUMNS[columnCount].length; toneCount++) {
//                int toneDegree = TONE_COLUMNS[columnCount][toneCount];
//
//                final Button curButton = new Button(getApplicationContext());
//                curButton.setWidth(R.dimen.voice_button_height);
//                curButton.setHeight(R.dimen.voice_button_height);
//
//                // +1 to display base 1 indexing for user.
//                curButton.setText(Integer.toString(toneDegree + 1));
//                curButton.setTag(toneDegree);
//                curButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int degree = (int) curButton.getTag();
//                        if (mToneIsActive[degree]) {
//                            mPresenter.stopTone(new Tone(degree, Tone.TONE_CHORD));
//                            mToneIsActive[degree] = false;
//                            curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
//                        }
//                        else {
//                            mPresenter.playTone(new Tone(degree, Tone.TONE_CHORD));
//                            mToneIsActive[degree] = true;
//                            curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
//                        }
//                    }
//                });
//
//                // Tone 0 is played by default.
//                if (toneDegree != 0) {
//                    curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
//                }
//                else {
//                    curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
//                }
//                curLayout.addView(curButton);
//            }
//        }
//    }
//
//    public void saveTemplate(View view) {
//        TextView templateNameView = findViewById(R.id.template_name_edit_text);
//
//        // Todo: this is a hacky solution. Iterates through array twice
//        int numTones = 0;
//        for (boolean active : mToneIsActive) {
//            if (active) {
//                numTones++;
//            }
//        }
//        int[] chordToneIxs = new int[numTones];
//
//        int ctIx = 0;
//        for (int i = 0; i < NUM_TONES; i++) {
//            if (mToneIsActive[i]) {
//                chordToneIxs[ctIx] = i;
//                ctIx++;
//            }
//        }
//
//        mPresenter.saveTemplate(templateNameView.getText().toString(), chordToneIxs);
//    }
}
