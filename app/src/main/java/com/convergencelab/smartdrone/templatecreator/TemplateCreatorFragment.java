package com.convergencelab.smartdrone.templatecreator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.convergencelab.smartdrone.R;
import com.example.keyfinder.Tone;

/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateCreatorFragment extends Fragment implements TemplateCreatorContract.View {

    private static final int NUM_TONES = 14;
    private static final int MAX_LEN_NAME = 20;

    // Todo: probably a better way to do this.
    private static final int[] TONE_COLUMN_ONE = { 6, 4, 2, 0 };
    private static final int[] TONE_COLUMN_TWO = { 5, 3, 1 };
    private static final int[] TONE_COLUMN_THREE = { 13, 11, 9, 7 };
    private static final int[] TONE_COLUMN_FOUR = { 12, 10, 8 };

    private static final int[][] BASS_ROW = {
            { },     // Null
            { 0 },   // Root
            { 4 },   // Fifth
            { 0, 4 } // Perfect Fifth
    };

    private final int[][] TONE_COLUMNS = {
            TONE_COLUMN_ONE,
            TONE_COLUMN_TWO,
            TONE_COLUMN_THREE,
            TONE_COLUMN_FOUR
    };

    private final Button[] chordToneButtons = new Button[NUM_TONES];
    private final Button[] bassToneButtons = new Button[4]; // Todo hardcoded

    private TemplateCreatorContract.Presenter mPresenter;

    private EditText mName;
    private String[] BASS_ROW_NAMES = {
            "-",
            "R",
            "5",
            "P5"
    };


    public static TemplateCreatorFragment newInstance() {
        return new TemplateCreatorFragment();
    }

    public TemplateCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.template_creator_frag, container, false);
        mName = root.findViewById(R.id.template_name_edit_text);
        // Set limit of 20 chars on EditText.
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(MAX_LEN_NAME);
        mName.setFilters(filterArray);

        drawLayout(root);

        return root;
    }

    private void drawLayout(final View root) {
        LinearLayout curLayout;
        int totalCount = 0;
        String layoutTemp = "tone_column_";

        // Make chord tones
        for (int columnCount = 0; columnCount < 4; columnCount++) {
            String curLayoutStr = layoutTemp + Integer.toString(columnCount);
            System.out.println(curLayoutStr);
            int resId = getResources().getIdentifier(curLayoutStr, "id", root.getContext().getPackageName());
            curLayout = root.findViewById(resId);

            for (int toneCount = 0; toneCount < TONE_COLUMNS[columnCount].length; toneCount++) {
                int toneDegree = TONE_COLUMNS[columnCount][toneCount];

                final Button curButton = new Button(root.getContext());
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.voice_button_height), (int) getResources().getDimension(R.dimen.voice_button_height));

                // +1 to display base 1 indexing for user.
                curButton.setText(Integer.toString(toneDegree + 1));
                curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
                curButton.setTag(toneDegree);
                curButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int degree = (int) curButton.getTag();
                        mPresenter.toggleToneStatus(degree, Tone.TONE_CHORD);
                    }
                });
                chordToneButtons[(int) curButton.getTag()] = curButton;
                totalCount++;
                curLayout.addView(curButton, btnParams);
            }
        }

        // Make bass tone buttons
        // Todo: come up with some consistent naming convention
        curLayout = root.findViewById(R.id.bass_tone_row);
        for (int toneCount = 0; toneCount < BASS_ROW.length; toneCount++) {
            String toneDegree = BASS_ROW_NAMES[toneCount];

            final Button curButton = new Button(root.getContext());
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.voice_button_height), (int) getResources().getDimension(R.dimen.voice_button_height));

            // +1 to display base 1 indexing for user.
            if (BASS_ROW[toneCount].length == 2) {
                curButton.setText("P5"); // Todo: fix hardcoded
            }
            else {
                curButton.setText(toneDegree);
            }

            curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
            curButton.setTag(toneCount);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int)curButton.getTag();
                    mPresenter.selectBassTones(tag);
                }
            });
            bassToneButtons[toneCount] = curButton;
            curLayout.addView(curButton, btnParams);
        }


        root.findViewById(R.id.template_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveTemplate(mName.getText().toString());
            }
        });
        root.findViewById(R.id.template_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showToneActive(Tone toShow) {
        chordToneButtons[toShow.getDegree()].setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
    }

    @Override
    public void showToneInactive(Tone toShow) {
        chordToneButtons[toShow.getDegree()].setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
    }

    @Override
    public void showBassTonesActive(int toShow) {
        bassToneButtons[toShow].setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
    }

    @Override
    public void showBassTonesInactive(int toShow) {
        bassToneButtons[toShow].setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
    }

    @Override
    public void showEmptyNameError() {
        Toast.makeText(getActivity().getApplicationContext(), "empty name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showIllegalCharacterError() {
        Toast.makeText(getActivity().getApplicationContext(), "illegal character", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDuplicateNameError() {
        Toast.makeText(getActivity().getApplicationContext(), "duplicate name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyTemplateError() {
        Toast.makeText(getActivity().getApplicationContext(), "empty template", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelTemplateCreator() {
        getActivity().finish(); // Todo: make sure correct
    }

    @Override
    public void setPresenter(TemplateCreatorContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
