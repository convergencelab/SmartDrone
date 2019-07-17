package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;


public class TemplateCreatorPresenter implements TemplateCreatorContract.Presenter {

    private final TemplateCreatorDataSource mTemplateCreatorDataSource;

    private final TemplateCreatorContract.View mTemplateCreatorView;

    private static final int NUM_TONES = 14;

    private boolean[] mToneIsActive;
    private Tone[] mTones;
    private int numActiveTones = 0;


    public TemplateCreatorPresenter(TemplateCreatorDataSource templateCreatorDataSource,
                                    TemplateCreatorContract.View templateCreatorView) {
        mTemplateCreatorDataSource = templateCreatorDataSource;
        mTemplateCreatorView = templateCreatorView;

        mTemplateCreatorView.setPresenter(this);
    }

    @Override
    public void start() {
        initTones();
        mTemplateCreatorDataSource.initialize();
    }

    private void initTones() {
        mTones = new Tone[NUM_TONES];
        for (int i = 0; i < NUM_TONES; i++) {
            mTones[i] = new Tone(i, Tone.TONE_CHORD);
        }

        mToneIsActive = new boolean[NUM_TONES];
        playTone(mTones[0]);
    }

    @Override
    public void toggleToneStatus(int toneDegree) {
        Tone toToggle = mTones[toneDegree];
        if (mToneIsActive[toneDegree]) {
            stopTone(toToggle);
        }
        else {
            playTone(toToggle);
        }
    }

    @Override
    public void cancel() {
        mTemplateCreatorView.cancelTemplateCreator();
    }

    /**
     * Plays tone. Marks tone as active. Updates background on view.
     * @param toPlay tone to play.
     */
    private void playTone(Tone toPlay) {
        mToneIsActive[toPlay.getDegree()] = true;
        mTemplateCreatorDataSource.playTone(toPlay);
        mTemplateCreatorView.showToneActive(toPlay);
        numActiveTones++;
    }

    /**
     * Stops tone. Marks tone as inactive. Updates background on view.
     * @param toStop tone to stop.
     */
    private void stopTone(Tone toStop) {
        mToneIsActive[toStop.getDegree()] = false;
        mTemplateCreatorDataSource.stopTone(toStop);
        mTemplateCreatorView.showToneInactive(toStop);
        numActiveTones--;
    }

    // Todo: Refactor? make template at start of function instead of last condition
    @Override
    public void saveTemplate(String name) {
        Tone defBassTone = new Tone(0, Tone.TONE_BASS);
        VoicingTemplate template = new VoicingTemplate(name, new Tone[]{defBassTone}, getChordTones());

        // Validate name.
        if (isDuplicateName(template.getName())) {
            mTemplateCreatorView.showDuplicateNameError();
        }
        else if (isEmptyName(template.getName())) {
            mTemplateCreatorView.showEmptyNameError();
        }
        else if (isEmptyTemplate(template.getChordTones())) {
            mTemplateCreatorView.showEmptyTemplateError();
        }
        else if (containsIllegalCharacter(template.getName())) {
            mTemplateCreatorView.showIllegalCharacterError();
        }
        else {
            mTemplateCreatorDataSource.saveTemplate(template);
        }
    }

    private Tone[] getChordTones() {
        Tone[] chordTones = new Tone[numActiveTones];
        int toneIx = 0;
        for (int i = 0; i < mTones.length; i++) {
            if (mToneIsActive[i]) {
                chordTones[toneIx] = mTones[i];
                toneIx++;
            }
        }
        return chordTones;
    }

    private boolean containsIllegalCharacter(String name) {
        return name.contains("{") || name.contains("}") || name.contains("|");
    }

    private boolean isDuplicateName(String name) {
        return mTemplateCreatorDataSource.isDuplicateName(name);
    }

    private boolean isEmptyName(String name) {
        return name.length() == 0;
    }

    private boolean isEmptyTemplate(Tone[] chordTones) {
        return chordTones.length == 0;
    }
}
