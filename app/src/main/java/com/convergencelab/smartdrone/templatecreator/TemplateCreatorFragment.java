package com.convergencelab.smartdrone.templatecreator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private final int[][] TONE_COLUMNS = {
            TONE_COLUMN_ONE,
            TONE_COLUMN_TWO,
            TONE_COLUMN_THREE,
            TONE_COLUMN_FOUR
    };

    private final Button[] toneButtons = new Button[NUM_TONES];

    private TemplateCreatorContract.Presenter mPresenter;

    private EditText mName;


    public static TemplateCreatorFragment newInstance() {
        return new TemplateCreatorFragment();
    }

    public TemplateCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.template_creator_frag, container, false);
        mName = root.findViewById(R.id.template_name_edit_text);
        // Logic
        drawLayout(root);

        return root;
    }

    private void drawLayout(View root) {
        LinearLayout curLayout;
        int totalCount = 0;
        String layoutTemp = "tone_column_";
        for (int columnCount = 0; columnCount < 4; columnCount++) {
            String curLayoutStr = layoutTemp + Integer.toString(columnCount);
            System.out.println(curLayoutStr);
            int resId = getResources().getIdentifier(curLayoutStr, "id", root.getContext().getPackageName());
            curLayout = root.findViewById(resId);

            for (int toneCount = 0; toneCount < TONE_COLUMNS[columnCount].length; toneCount++) {
                int toneDegree = TONE_COLUMNS[columnCount][toneCount];

                final Button curButton = new Button(root.getContext());
                LinearLayout.LayoutParams btnParames = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.voice_button_height), (int) getResources().getDimension(R.dimen.voice_button_height));
//                curButton.setWidth(R.dimen.voice_button_height);
//                curButton.setHeight(R.dimen.voice_button_height);

                // +1 to display base 1 indexing for user.
                curButton.setText(Integer.toString(toneDegree + 1));
                curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
                curButton.setTag(toneDegree);
                curButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int degree = (int) curButton.getTag();
                        mPresenter.toggleToneStatus(degree);
                    }
                });
//                // Tone 0 is played by default.
//                if (toneDegree != 0) {
//                    curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
//                }
//                else {
//                    curButton.setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
//                }
                toneButtons[(int) curButton.getTag()] = curButton;
                totalCount++;
                curLayout.addView(curButton, btnParames);
            }
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
        toneButtons[toShow.getDegree()].setBackground(getResources().getDrawable(R.drawable.active_key_background_active));
    }

    @Override
    public void showToneInactive(Tone toShow) {
        toneButtons[toShow.getDegree()].setBackground(getResources().getDrawable(R.drawable.active_key_background_inactive));
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
