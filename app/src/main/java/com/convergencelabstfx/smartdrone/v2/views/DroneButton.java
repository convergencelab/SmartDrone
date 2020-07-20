package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

public class DroneButton extends ConstraintLayout {

    public DroneButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // todo: add timber to project
        Log.d("testV", "onMeasure");
        final int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("testV", "w: " + w + " ;h: " + h);
        Log.d("testV", "onSizeChanged");
    }
}
