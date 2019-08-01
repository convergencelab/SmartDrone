package com.convergencelab.smartdrone.templatecreator;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.convergencelab.smartdrone.R;
import com.convergencelab.smartdrone.soundsettings.SoundSettingsActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

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

    private static final int[] BASS_ROW = { 0, 4 };

    private final int[][] TONE_COLUMNS = {
            TONE_COLUMN_ONE,
            TONE_COLUMN_TWO,
            TONE_COLUMN_THREE,
            TONE_COLUMN_FOUR
    };

    private final LinearLayout[] chordToneItems = new LinearLayout[NUM_TONES];

    private final LinearLayout[] bassToneItems = new LinearLayout[5];

    private TemplateCreatorContract.Presenter mPresenter;

    private TextInputEditText mName;

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
        // Set limit of 20 chars on EditText.
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(MAX_LEN_NAME);
        mName = root.findViewById(R.id.template_name_edit_text);
        mName.setFilters(filterArray);

        drawLayout(root, inflater);

        return root;
    }

    private void drawLayout(final View root, LayoutInflater inflater) {
        LinearLayout curLayout;
        int totalCount = 0;
        String layoutTemp = "tone_column_";

        // Make chord tones
        for (int columnCount = 0; columnCount < 4; columnCount++) {
            String curLayoutStr = layoutTemp + (columnCount);
            System.out.println(curLayoutStr);
            int resId = getResources().getIdentifier(curLayoutStr, "id", root.getContext().getPackageName());
            curLayout = root.findViewById(resId);

            for (int toneCount = 0; toneCount < TONE_COLUMNS[columnCount].length; toneCount++) {
                int toneDegree = TONE_COLUMNS[columnCount][toneCount];

                final LinearLayout toneItem = (LinearLayout) inflater.inflate(R.layout.tone_item_checkbox,
                        (ViewGroup) root, false);

                TextView checkBoxText = toneItem.findViewById(R.id.tone_text);
                // +1 to display base 1 indexing for user.
                checkBoxText.setText(Integer.toString(toneDegree + 1));
                toneItem.setTag(toneDegree);
                toneItem.setOnClickListener(v -> {
                    CheckBox checkBox = (CheckBox) toneItem.getChildAt(0);
                    checkBox.setChecked(!checkBox.isChecked());

                    int degree = (int) toneItem.getTag();
                    mPresenter.toggleChordTone(degree);
                });

                chordToneItems[(int) toneItem.getTag()] = toneItem;
                totalCount++;
                curLayout.addView(toneItem);
            }
        }

        // Make bass tone buttons
        // Todo: come up with some consistent naming convention
        curLayout = root.findViewById(R.id.bass_tone_row);
        for (int toneCount = 0; toneCount < BASS_ROW.length; toneCount++) {
            int toneDegree = BASS_ROW[toneCount];

            final LinearLayout toneItem = (LinearLayout) inflater.inflate(R.layout.tone_item_checkbox,
                    (ViewGroup) root, false);

            TextView tv = (TextView) toneItem.getChildAt(1);
            // +1 to display base 1 indexing for user
            tv.setText("" + (toneDegree + 1));

            toneItem.setTag(toneDegree);
            toneItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) toneItem.getChildAt(0);
                    checkBox.setChecked(!checkBox.isChecked());

                    int degree = (int) toneItem.getTag();
                    mPresenter.toggleBassTone(degree);
                }
            });
            bassToneItems[toneCount] = toneItem;
            curLayout.addView(toneItem);
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
    public void onPause() {
        super.onPause();
        mPresenter.cancel(); // Todo current workaround
    }

    @Override
    public void showEmptyNameError() {
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showIllegalCharacterError() {
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Name cannot contain { } or |", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDuplicateNameError() {
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Name already taken", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyTemplateError() {
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Must select at least one voice.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelTemplateCreator() {
        Objects.requireNonNull(getActivity()).finish(); // Todo: make sure correct
    }

    @Override
    public void setPresenter(TemplateCreatorContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
