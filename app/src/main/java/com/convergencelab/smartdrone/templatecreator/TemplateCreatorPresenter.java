package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;


public class TemplateCreatorPresenter implements TemplateCreatorContract.Presenter {

    private final TemplateCreatorDataSource mTemplateCreatorDataSource;

    private final TemplateCreatorContract.View mTemplateCreatorView;

    private static final int NUM_TONES = 14;

    private boolean[] mChordToneIsActive;
    private Tone[] mChordTones;
    private int numActiveChordTones = 0;

    private int numActiveBassTones = 0;

    public TemplateCreatorPresenter(TemplateCreatorDataSource templateCreatorDataSource,
                                    TemplateCreatorContract.View templateCreatorView) {
        mTemplateCreatorDataSource = templateCreatorDataSource;
        mTemplateCreatorView = templateCreatorView;

        mTemplateCreatorView.setPresenter(this);
    }

    @Override
    public void start() {
        mTemplateCreatorDataSource.initialize();
        initTones();
    }

    private void initTones() {
        mChordTones = new Tone[NUM_TONES];
        for (int i = 0; i < NUM_TONES; i++) {
            mChordTones[i] = new Tone(i, Tone.TONE_CHORD);
        }

        mChordToneIsActive = new boolean[NUM_TONES];

        activateTone(mChordTones[0]);
        activateTone(new Tone(0, Tone.TONE_BASS));
    }

    @Override
    public void toggleToneStatus(int toneDegree, int toneType) {
        Tone toToggle;
        if (toneType == Tone.TONE_CHORD) {
            toToggle = mChordTones[toneDegree];
        }
        else {
            toToggle = new Tone(toneDegree, Tone.TONE_BASS);
        }

        // Play or Stop tone.
        if (mChordToneIsActive[toneDegree]) {
            deactivateTone(toToggle);
        }
        else {
            activateTone(toToggle);
        }
    }

    @Override
    public void cancel() {
        mTemplateCreatorDataSource.endPlayback();
        mTemplateCreatorView.cancelTemplateCreator();
    }

    /**
     * Plays tone. Marks tone as active. Updates background on view.
     * @param toPlay tone to play.
     */
    private void activateTone(Tone toPlay) {
        mChordToneIsActive[toPlay.getDegree()] = true;
        mTemplateCreatorDataSource.playTone(toPlay);
        mTemplateCreatorView.showToneActive(toPlay);
        if (toPlay.getCode() == Tone.TONE_CHORD) {
            numActiveChordTones++;
        }
        else if (toPlay.getCode() == Tone.TONE_BASS) {
            numActiveBassTones++;
        }
    }

    /**
     * Stops tone. Marks tone as inactive. Updates background on view.
     * @param toStop tone to stop.
     */
    private void deactivateTone(Tone toStop) {
        mChordToneIsActive[toStop.getDegree()] = false;
        mTemplateCreatorDataSource.stopTone(toStop);
        mTemplateCreatorView.showToneInactive(toStop);
        if (toStop.getCode() == Tone.TONE_CHORD) {
            numActiveChordTones--;
        }
        else if (toStop.getCode() == Tone.TONE_BASS) {
            numActiveBassTones--;
        }
    }

    // Todo: Refactor? make template at start of function instead of last condition
    @Override
    public void saveTemplate(String name) {
        Tone defBassTone = new Tone(0, Tone.TONE_BASS);
        VoicingTemplate template = new VoicingTemplate(name, new Tone[]{defBassTone}, getChordTones());

        // Validate name.
        if (isEmptyName(template.getName())) {
            mTemplateCreatorView.showEmptyNameError();
        }
        else if (isDuplicateName(template.getName())) {
            System.out.println("Name: " + template.getName());
            mTemplateCreatorView.showDuplicateNameError();
        }
        else if (isEmptyTemplate(template.getChordTones())) {
            mTemplateCreatorView.showEmptyTemplateError();
        }
        else if (containsIllegalCharacter(template.getName())) {
            mTemplateCreatorView.showIllegalCharacterError();
        }
        else {
            mTemplateCreatorDataSource.saveTemplate(template);
            mTemplateCreatorDataSource.endPlayback();
            mTemplateCreatorView.cancelTemplateCreator(); // Todo: make better name.
        }
    }

    private Tone[] getChordTones() {
        Tone[] chordTones = new Tone[numActiveChordTones];
        int toneIx = 0;
        for (int i = 0; i < mChordTones.length; i++) {
            if (mChordToneIsActive[i]) {
                chordTones[toneIx] = mChordTones[i];
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
