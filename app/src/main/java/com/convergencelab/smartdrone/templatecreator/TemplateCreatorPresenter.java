package com.convergencelab.smartdrone.templatecreator;

import com.example.keyfinder.Tone;
import com.example.keyfinder.VoicingTemplate;


public class TemplateCreatorPresenter implements TemplateCreatorContract.Presenter {

    private final TemplateCreatorDataSource mTemplateCreatorDataSource;

    private final TemplateCreatorContract.View mTemplateCreatorView;

    private static final int NUM_CHORD_TONES = 14;
    private static final int NUM_BASS_TONES = 5;

    private boolean[] mChordToneIsActive;
    private Tone[] mChordTones;
    private int numActiveChordTones = 0;

    private boolean[] mBassToneIsActive;
    private Tone[] mBassTones;
    private int numActiveBassTones = 0;

//    private int prevIx;

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
        // Initialize chord tones
        mChordTones = new Tone[NUM_CHORD_TONES];
        for (int i = 0; i < NUM_CHORD_TONES; i++) {
            mChordTones[i] = new Tone(i, Tone.TONE_CHORD);
        }
        mChordToneIsActive = new boolean[NUM_CHORD_TONES];

        // Initialize bass tones
        mBassTones = new Tone[NUM_BASS_TONES];
        for (int i = 0; i < NUM_BASS_TONES; i++) {
            mBassTones[i] = new Tone(i, Tone.TONE_BASS);
        }
        mBassToneIsActive = new boolean[NUM_BASS_TONES];
    }

    @Override
    public void toggleChordTone(int degree) {
        Tone toToggle = mChordTones[degree];

        // Play or Stop tone.
        if (mChordToneIsActive[degree]) {
            deactivateTone(toToggle);
        }
        else {
            activateTone(toToggle);
        }
    }

    @Override
    public void toggleBassTone(int degree) {
        Tone toToggle = mBassTones[degree];

        // Play or Stop tone.
        if (mBassToneIsActive[degree]) {
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
        mTemplateCreatorDataSource.playTone(toPlay);
        if (toPlay.getCode() == Tone.TONE_CHORD) {
            mChordToneIsActive[toPlay.getDegree()] = true;
            numActiveChordTones++;
        }
        else if (toPlay.getCode() == Tone.TONE_BASS) {
            mBassToneIsActive[toPlay.getDegree()] = true;
            numActiveBassTones++;
        }
    }

    /**
     * Stops tone. Marks tone as inactive. Updates background on view.
     * @param toStop tone to handleActivityChange.
     */
    private void deactivateTone(Tone toStop) {
        mTemplateCreatorDataSource.stopTone(toStop);
        if (toStop.getCode() == Tone.TONE_CHORD) {
            mChordToneIsActive[toStop.getDegree()] = false;
            numActiveChordTones--;
        }
        else if (toStop.getCode() == Tone.TONE_BASS) {
            mBassToneIsActive[toStop.getDegree()] = false;
            numActiveBassTones--;
        }
    }

    // Todo: Refactor? make template at start of function instead of last condition
    @Override
    public void saveTemplate(String name) {
        VoicingTemplate template = new VoicingTemplate(name, getBassTones(), getChordTones());

        // Validate name.
        if (isEmptyName(template.getName())) {
            mTemplateCreatorView.showEmptyNameError();
        }
        else if (isDuplicateName(template.getName())) {
            System.out.println("Name: " + template.getName());
            mTemplateCreatorView.showDuplicateNameError();
        }
        else if (isEmptyTemplate(template)) {
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

    private Tone[] getBassTones() {
        Tone[] bassTones = new Tone[numActiveBassTones];
        int toneIx = 0;
        for (int i = 0; i < mBassTones.length; i++) {
            if (mBassToneIsActive[i]) {
                bassTones[toneIx] = mBassTones[i];
                toneIx++;
            }
        }
        return bassTones;
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

    private boolean isEmptyTemplate(VoicingTemplate toCheck) {
        return toCheck.numVoices() == 0;
    }
}
