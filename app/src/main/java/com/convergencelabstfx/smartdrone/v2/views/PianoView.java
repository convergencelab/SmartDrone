package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.convergencelabstfx.smartdrone.R;

import java.util.HashMap;

class PianoView extends ConstraintLayout {

    private HashMap<Integer, ImageButton> pianoKeyMap;
    private LinearLayout whiteKeys;
    private LinearLayout blackKeys;

    public PianoView(Context context) {
        super(context);
        init(context);
    }

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ImageButton getPianoKeyAt(int ix) {
        return pianoKeyMap.get(ix);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.piano_view, this);
        whiteKeys = findViewById(R.id.whiteKeys);
        blackKeys = findViewById(R.id.blackKeys);
        setupPianoLocationMap();
    }

    private void setupPianoLocationMap() {
        // If you look at 'piano_view.xml', the keys are contained in 2 linear layouts;
        // one for the white keys, and the other for the black keys.
        // Because of this, the coordinates of the piano keys are not sequential
        // and actually have a sort of staggered ordering.
        //
        //     ACTUAL |   (   1   3       6   8   10   ) -> black keys
        //     ORDER  |   ( 0   2   4   5   7   9   11 ) -> white keys
        //
        // For this reason, the locations are mapped so that the index of the pitch class
        // map will give you back the corresponding key button.
        final int[] seqBlack = { 1, 3, 6, 8, 10 };
        final int[] seqWhite = { 0, 2, 4, 5, 7, 9, 11 };
        pianoKeyMap = new HashMap<>(seqWhite.length + seqBlack.length);
        for (int i = 0; i < seqBlack.length; i++) {
            ImageButton pianoKey = (ImageButton) blackKeys.getChildAt(i);
            pianoKeyMap.put(seqBlack[i], pianoKey);
        }
        for (int i = 0; i < seqWhite.length; i++) {
            ImageButton pianoKey = (ImageButton) whiteKeys.getChildAt(i);
            pianoKeyMap.put(seqWhite[i], pianoKey);
        }
    }

}
