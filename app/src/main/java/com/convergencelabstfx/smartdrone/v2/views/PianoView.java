package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.convergencelabstfx.smartdrone.R;
import com.example.keyfinder.MusicTheory;

import java.util.ArrayList;
import java.util.List;

public class PianoView extends View {

    final private int NUMBER_OF_WHITE_KEYS = 7;
    final private int NUMBER_OF_BLACK_KEYS = 5;

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


    final private int[] whiteKeyIxs = new int[]{0, 2, 4, 5, 7, 9, 11};
    final private int[] blackKeyIxs = new int[]{1, 3, 6, 8, 10};

    // todo: remove this eventually
    final private float SCALE = getContext().getResources().getDisplayMetrics().density;

    // todo: remove reference, pass as parameter instead
    private Context context;

    private List<PianoTouchListener> listeners = new ArrayList<>();

    private int viewWidthRemainder;
    private int whiteKeyWidth;
    private int whiteKeyHeight;
    private int blackKeyWidth;
    private int blackKeyHeight;

    private float blackKeyWidthRatio;
    private float blackKeyHeightRatio;

    private int whiteKeyColor;
    private int blackKeyColor;
    private int keyPressedColor;
    private int keyStrokeColor;

    // todo: fix black key off center bug; more noticeable with larger border
    private int keyStrokeWidth = 6;
    private int keyCornerRadius;

    private int initTouchedKey;
    private boolean hasStayedOnInitKey;

    private GradientDrawable[] pianoKeys = new GradientDrawable[NUMBER_OF_WHITE_KEYS + NUMBER_OF_BLACK_KEYS];

    private boolean showBlackKeys;
    private boolean centerBlackKeys;

    private boolean[] keyIsPressed = new boolean[MusicTheory.TOTAL_NOTES];

    private final boolean[] isWhiteKey = new boolean[]{
            true, false, true, false, true, true, false,
            true, false, true, false, true, false, true
    };

    public PianoView(Context context) {
        super(context);

    }

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PianoView,
                0, 0
        );
        parseAttrs(a);
        // a.recycle(); // todo : add this in at some point ? find out what it does
    }

    public PianoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // todo: extract magic number 6
        whiteKeyWidth = (getMeasuredWidth() + 6 * keyStrokeWidth) / NUMBER_OF_WHITE_KEYS;
        whiteKeyHeight = getMeasuredHeight();
        blackKeyWidth = (int) (whiteKeyWidth * blackKeyWidthRatio);
        blackKeyHeight = (int) (whiteKeyHeight * blackKeyHeightRatio);
        // todo: extract magic numbers
        viewWidthRemainder = getMeasuredWidth() - (whiteKeyWidth * 7 - keyStrokeWidth * 6);
//        Log.d("testV", "remainder: " + viewWidthRemainder);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteKeys(canvas);
        drawBlackKeys(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int curTouchedKey = getTouchedKey(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initTouchedKey = curTouchedKey;
                hasStayedOnInitKey = true;
                for (PianoTouchListener listener : listeners) {
                    listener.onPianoTouch(curTouchedKey);
                }
                // invalidate() ?
                break;
            case MotionEvent.ACTION_MOVE:
                if (curTouchedKey != initTouchedKey) {
                    hasStayedOnInitKey = false;
                }
                // invalidate() ?
                break;
            case MotionEvent.ACTION_UP:
                if (hasStayedOnInitKey) {
                    for (PianoTouchListener listener : listeners) {
                        listener.onPianoClick(curTouchedKey);
                    }
                }
                break;
        }
        return true;
    }

    public void addPianoTouchListener(PianoTouchListener listener) {
        listeners.add(listener);
    }

    public void removePianoTouchListener(PianoTouchListener listener) {
        // todo: test; make sure it works correctly
        listeners.remove(listener);
    }

    public void showKeyPressed(int ix) {
        if (!keyIsPressed[ix]) {
            keyIsPressed[ix] = true;
            // TODO
            // Show pressed color
        }
    }

    public void showKeyNotPressed(int ix) {
        if (keyIsPressed[ix]) {
            keyIsPressed[ix] = false;
            // Show regular color
            if (isWhiteKey[ix]) {
                // todo
            } else {
                // todo
            }
        }
    }

    public boolean keyIsPressed(int ix) {
        return keyIsPressed[ix];
    }

    // todo: change these to ints
    // todo: the borders overlap on the middle white keys,
    //       so implement the correct touch geometry (stroke / 2)
    //       (currently biased towards the left keys, they get the whole border)
    private int getTouchedKey(float x, float y) {
//        Log.d("testV", "x: " + x + "; y: " + y);
        // todo: use a 'round' function instead of casting
        final int touchX = (int) x;
        final int touchY = (int) y;
        for (int ix : blackKeyIxs) {
            if (coordsInPianoKey(touchX, touchY, pianoKeys[ix])) {
                return ix;
            }
        }
        for (int ix : whiteKeyIxs) {
            if (coordsInPianoKey(touchX, touchY, pianoKeys[ix])) {
                return ix;
            }
        }
        return -1;
    }

    private boolean coordsInPianoKey(int x, int y, GradientDrawable key) {
        Rect keyBounds = key.getBounds();
        return x >= keyBounds.left && x <= keyBounds.right && y >= keyBounds.top && y <= keyBounds.bottom;
    }

    private void drawWhiteKeys(Canvas canvas) {
        int left = 0;
        // todo: update the math in this comment
        // This view divides it's width by 7. So if the width isn't divisible by 7
        // there would be unused space in the view.
        // For this reason, whiteKeyWidth has 1 added to it, and 1 removed from it after the
        // remainder has been added in.

        whiteKeyWidth++;
        for (int i = 0; i < NUMBER_OF_WHITE_KEYS; i++) {
            if (i == viewWidthRemainder) {
                whiteKeyWidth--;
            }
            final GradientDrawable pianoKey = makePianoKey(Color.WHITE, keyStrokeWidth, Color.BLACK, keyCornerRadius);
            pianoKey.setBounds(left, 0, left + whiteKeyWidth, whiteKeyHeight);
            pianoKey.draw(canvas);
            left += whiteKeyWidth - keyStrokeWidth;
            pianoKeys[whiteKeyIxs[i]] = pianoKey;
        }
    }

    // todo: investigate / fix issue with black keys being off center due to pixel remainder
    //       not really a pressing issue
    private void drawBlackKeys(Canvas canvas) {
        /* Method 1 */
        /*
        int left = (whiteKeyWidth / 2) + ((whiteKeyWidth - blackKeyWidth) / 2) - keyStrokeWidth / 2;
        for (int i = 0; i < NUMBER_OF_BLACK_KEYS; i++) {
            // There is a gap between the 2nd and 3rd (base 1 indexing) black key on a piano.
            if (i == 2) {
                left += whiteKeyWidth - keyStrokeWidth;
            }
            final GradientDrawable pianoKey = makePianoKey(Color.DKGRAY, keyStrokeWidth, Color.BLACK, keyCornerRadius);
            pianoKey.setBounds(left, 0, left + blackKeyWidth, blackKeyHeight);
            pianoKey.draw(canvas);
            left += whiteKeyWidth - keyStrokeWidth;
            pianoKeys[blackKeyIxs[i]] = pianoKey;
        }
         */

        /* Method 2 */
        for (int i = 0; i < NUMBER_OF_BLACK_KEYS; i++) {
            GradientDrawable whiteKey = pianoKeys[blackKeyIxs[i] - 1];
            final int left = whiteKey.getBounds().right - (blackKeyWidth / 2) - (keyStrokeWidth / 2);
            final GradientDrawable pianoKey = makePianoKey(Color.DKGRAY, keyStrokeWidth, Color.BLACK, keyCornerRadius);
            pianoKey.setBounds(left, 0, left + blackKeyWidth, blackKeyHeight);
            pianoKey.draw(canvas);
            pianoKeys[blackKeyIxs[i]] = pianoKey;
        }
    }

    private GradientDrawable makePianoKey(
            int fillColor,
            int strokeWidth,
            int strokeColor,
            int cornerRadius
    ) {
        final GradientDrawable pianoKey = new GradientDrawable();
        pianoKey.setShape(GradientDrawable.RECTANGLE);
        pianoKey.setColor(fillColor);
        pianoKey.setStroke(strokeWidth, strokeColor);
        pianoKey.setCornerRadius(cornerRadius);
        return pianoKey;
    }

    private int dpsToPx(float dps) {
        return (int) (dps * SCALE + 0.5f);
    }

    // todo: pass context as parameter instead of storing as local variable
    private void parseAttrs(TypedArray attrs) {
        keyCornerRadius = (int) attrs.getDimension(
                R.styleable.PianoView_keyCornerRadius,
                dpsToPx(KEY_CORNER_RADIUS_DEFAULT)
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
