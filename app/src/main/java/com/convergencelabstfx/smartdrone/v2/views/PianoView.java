package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.convergencelabstfx.smartdrone.R;
import com.example.keyfinder.MusicTheory;


import java.util.HashMap;

public class PianoView extends ConstraintLayout {

    private LinearLayout whiteKeys;
    private LinearLayout blackKeys;

    private HashMap<Integer, ImageButton> pianoKeyMap = new HashMap<>(MusicTheory.TOTAL_NOTES);

    final float scale = getContext().getResources().getDisplayMetrics().density;

    private boolean[] keyIsPressed = new boolean[12];

    private final boolean[] isWhiteKey = new boolean[]{
            true, false, true, false, true, true, false,
            true, false, true, false, true, false, true
    };

    public PianoView(Context context) {
        super(context);
        init(context);
    }

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PianoView,
                0, 0
        );
        Log.d("test", "H: " + attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height"));
        Log.d("test", "W: " + attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width"));
//        final int whiteKeyWidth = a.getInteger(R.styleable.Pia)
        this.addView(makePianoKeyButton(
                context,
                100,
                100,
                2,
                2,
                Color.BLUE));
        setupPianoLocationMap();
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ImageButton getPianoKeyAt(int ix) {
        return pianoKeyMap.get(ix);
    }

    public void showKeyPressed(int ix) {
        if (!keyIsPressed[ix]) {
            keyIsPressed[ix] = true;
            // Show pressed color
            getPianoKeyAt(ix).setBackgroundResource(R.drawable.piano_key_background_green);
        }
    }

    public void showKeyNotPressed(int ix) {
        if (keyIsPressed[ix]) {
            keyIsPressed[ix] = false;
            // Show regular color
            if (isWhiteKey[ix]) {
                getPianoKeyAt(ix).setBackgroundResource(R.drawable.piano_key_background_white);
            } else {
                getPianoKeyAt(ix).setBackgroundResource(R.drawable.piano_key_background_black);
            }
        }
    }

    public boolean keyIsPressed(int ix) {
        return keyIsPressed[ix];
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
//        final int[] seqBlack = {1, 3, 6, 8, 10};
//        final int[] seqWhite = {0, 2, 4, 5, 7, 9, 11};
//
//        for (int i = 0; i < seqBlack.length; i++) {
//            ImageButton pianoKey = (ImageButton) blackKeys.getChildAt(i);
//            pianoKeyMap.put(seqBlack[i], pianoKey);
//        }
//        for (int i = 0; i < seqWhite.length; i++) {
//            ImageButton pianoKey = (ImageButton) whiteKeys.getChildAt(i);
//            pianoKeyMap.put(seqWhite[i], pianoKey);
//        }
    }

    private View makePianoKeyButton(
            Context context,
            int width,
            int height,
            int strokeWidth,
            int cornerRadius,
            int fillColor) {

        ImageButton pianoKeyButton = new ImageButton(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpsToPx(width), dpsToPx(height));
        pianoKeyButton.setLayoutParams(params);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.GRAY);
        drawable.setStroke(dpsToPx(strokeWidth), Color.BLACK);
        drawable.setCornerRadius(dpsToPx(cornerRadius));
        pianoKeyButton.setBackground(drawable);

        return pianoKeyButton;
    }

    private int dpsToPx(float dps) {
        return (int) (dps * scale + 0.5f);
    }

}
