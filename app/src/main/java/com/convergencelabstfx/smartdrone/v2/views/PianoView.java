package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private int keyStrokeWidth = 6;
    private int keyCornerRadius;

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
        Log.d(
                "testV",
                "remainder: " + viewWidthRemainder);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWhiteKeys(canvas);
        drawBlackKeys(canvas);
        /*
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
        */
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchedKey = getTouchedKey(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // todo: verify this works
                //       find out how the prebuilt Android views handles touch / click
                //       only going to worry about click handling right now
                for (PianoTouchListener listener : listeners) {
                    listener.onPianoTouch(touchedKey);
                }
                // invalidate() ?
                break;
            case MotionEvent.ACTION_MOVE:
                // todo
                // invalidate() ?
                break;
            case MotionEvent.ACTION_UP:
                for (PianoTouchListener listener : listeners) {
                    listener.onPianoClick(touchedKey);
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

    private int getTouchedKey(float x, float y) {
        // todo: find which key is touched
        return -1;
    }

    private void drawWhiteKeys(Canvas canvas) {
        Log.d("testV", "drawWhiteKeys called");

        GradientDrawable drawable = new GradientDrawable();
        int left = 0;
        // This view divides it's width by 7. So if the width isn't divisible by 7
        // there would be unused space in the view.
        // For this reason, whiteKeyWidth has 1 added to it, and 1 removed from it after the
        // remainder has been added in.

        int offset = 1;
        for (int i = 0; i < NUMBER_OF_WHITE_KEYS; i++) {
            if (i >= viewWidthRemainder) {
                offset = 0;
            }
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(Color.WHITE);
            drawable.setStroke(keyStrokeWidth, Color.BLACK);
            drawable.setCornerRadius(keyCornerRadius);
            drawable.setBounds(left, 0, left + whiteKeyWidth + offset, whiteKeyHeight);
            drawable.draw(canvas);
            left += whiteKeyWidth + offset - keyStrokeWidth;
            if (i < viewWidthRemainder) {
                left += 1;
            }
        }
    }

    private void drawBlackKeys(Canvas canvas) {
        // todo: do something
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
