package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

    // Note: Dimensions are in DIP unless explicitly specified (i.e. ___inPx)
    final private float BLACK_KEY_HEIGHT_RATIO_DEFAULT = 0.7f;
    final private float BLACK_KEY_WIDTH_RATIO_DEFAULT = 0.6f;

    final private int WIDTH_DEFAULT = 300;
    final private int HEIGHT_DEFAULT = 180;

    final private int WHITE_KEY_COLOR_DEFAULT = Color.WHITE;
    final private int BLACK_KEY_COLOR_DEFAULT = Color.GRAY;
    final private int KEY_PRESSED_COLOR_DEFAULT = getResources().getColor(R.color.active);
    final private int KEY_STROKE_COLOR_DEFAULT = Color.BLACK;
    final private int KEY_STROKE_WIDTH_DEFAULT = 2;
    final private int KEY_CORNER_RADIUS_DEFAULT = 1;

    final private ImageButton[] keyButtons = new ImageButton[12];
    final private int[] whiteKeyIxs = new int[]{0, 2, 4, 5, 7, 9, 11};
    final private int[] blackKeyIxs = new int[]{1, 3, 6, 8, 10};

    // TODO: actually implement these
    final private boolean SHOW_BLACK_KEYS_DEFAULT = true;
    final private boolean CENTER_BLACK_KEYS_DEFAULT = true;

    final private float SCALE = getContext().getResources().getDisplayMetrics().density;

    private LinearLayout whiteKeys;
    private LinearLayout blackKeys;

    private int widthInPx;
    private int heightInPx;

    private Context context;

//    private int whiteKeyWidth;
//    private int whiteKeyHeight;
//    private int blackKeyWidth;
//    private int blackKeyHeight;

    private float blackKeyWidthRatio;
    private float blackKeyHeightRatio;

    private int whiteKeyColor;
    private int blackKeyColor;
    private int keyPressedColor;
    private int keyStrokeColor;
    private int keyStrokeWidth;
    private int keyCornerRadius;

    private boolean showBlackKeys;
    private boolean centerBlackKeys;

    private HashMap<Integer, ImageButton> pianoKeyMap = new HashMap<>(MusicTheory.TOTAL_NOTES);

    private boolean[] keyIsPressed = new boolean[MusicTheory.TOTAL_NOTES];

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
        this.context = context;
        setWillNotDraw(false);
        Log.d("testV", "init called");
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PianoView,
                0, 0
        );
        parseAttrs(a);
//        final int whiteKeyWidth = a.getInteger(R.styleable.Pia)
//        this.addView(makePianoKeyButton(
//                context,
//                100,
//                100,
//                2,
//                2,
//                Color.BLUE));
//        setupPianoLocationMap();
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        widthInPx = w;
        heightInPx = h;
        Log.d("testV", "onsizechanged called");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("testV", "ondraw called");
        // Todo: extract hard-coded values
        // Todo: move to on sizeChanged or onMeasure
        final int whiteKeyWidth = widthInPx / 7;
        final int whiteKeyHeight = heightInPx;
        final int whiteKeyWidthRemainder = widthInPx % 7;
        final int blackKeyHeight = (int) (whiteKeyWidth * blackKeyWidthRatio);
        final int blackKeyWidth = (int) (whiteKeyHeight * blackKeyHeightRatio);
        // nothing right now

        int leftMargin = 0;
        ViewGroup.LayoutParams params = this.getLayoutParams();
        for (int i = 0; i < whiteKeyIxs.length; i++) {
            int keyWidth = whiteKeyWidth;
            if (i < whiteKeyWidthRemainder) {
                keyWidth += 1;
            }
            View key = makePianoKeyButton(
                    context,
                    keyWidth,
                    whiteKeyHeight,
                    // Todo: add attribute for border
                    dpsToPx(2),
                    keyCornerRadius,
                    whiteKeyColor);
            leftMargin += dpsToPx(10);
            key.setPadding(leftMargin, 0, 0, 0);
            Log.d("testV", "left padding: " + key.getPaddingLeft());
            this.addView(key);
        }
        Log.d("testV", "broke for loop");
        setWillNotDraw(true);
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
        final int[] seqBlack = {1, 3, 6, 8, 10};
        final int[] seqWhite = {0, 2, 4, 5, 7, 9, 11};

        for (int i = 0; i < seqBlack.length; i++) {
            ImageButton pianoKey = (ImageButton) blackKeys.getChildAt(i);
            pianoKeyMap.put(seqBlack[i], pianoKey);
        }
        for (int i = 0; i < seqWhite.length; i++) {
            ImageButton pianoKey = (ImageButton) whiteKeys.getChildAt(i);
            pianoKeyMap.put(seqWhite[i], pianoKey);
        }
    }

    private View makePianoKeyButton(
            Context context,
            int width,
            int height,
            int strokeWidth,
            int cornerRadius,
            int fillColor) {

        ImageButton pianoKeyButton = new ImageButton(context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        pianoKeyButton.setLayoutParams(params);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.GRAY);
        drawable.setStroke(strokeWidth, Color.BLACK);
        drawable.setCornerRadius(cornerRadius);
        pianoKeyButton.setBackground(drawable);

        return pianoKeyButton;
    }

    private int dpsToPx(float dps) {
        return (int) (dps * SCALE + 0.5f);
    }

    private void parseAttrs(TypedArray attrs) {
        keyCornerRadius = (int) attrs.getDimension(
                R.styleable.PianoView_keyCornerRadius,
                dpsToPx(KEY_CORNER_RADIUS_DEFAULT)
        );
        showBlackKeys = attrs.getBoolean(
                R.styleable.PianoView_showBlackKeys,
                SHOW_BLACK_KEYS_DEFAULT
        );
        centerBlackKeys = attrs.getBoolean(
                R.styleable.PianoView_centerBlackKeys,
                centerBlackKeys
        );
        blackKeyColor = attrs.getColor(
                R.styleable.PianoView_blackKeyColor,
                BLACK_KEY_COLOR_DEFAULT
        );
        whiteKeyColor = attrs.getColor(
                R.styleable.PianoView_whiteKeyColor,
                WHITE_KEY_COLOR_DEFAULT
        );
        keyPressedColor = attrs.getColor(
                R.styleable.PianoView_keyPressedColor,
                KEY_PRESSED_COLOR_DEFAULT
        );
        blackKeyHeightRatio = attrs.getFloat(
                R.styleable.PianoView_blacKKeyLengthRatio,
                BLACK_KEY_HEIGHT_RATIO_DEFAULT
        );
        blackKeyWidthRatio = attrs.getFloat(
                R.styleable.PianoView_blacKKeyWidthRatio,
                BLACK_KEY_WIDTH_RATIO_DEFAULT
        );
    }

}
