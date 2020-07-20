package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.convergencelabstfx.smartdrone.R;

public class DroneButton extends ConstraintLayout {

    private TextView mTextView;

    public DroneButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View droneButton = inflater.inflate(R.layout.drone_button, this, false);
        this.addView(droneButton);
        mTextView = this.findViewById(R.id.droneButton_textView);
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

    public void setText(String text) {
        // todo: animate text
        mTextView.setText(text);
    }

    public void setRingColor(int color) {

    }

    public void setIcon(int icon) {

    }

    public void setIcon() {

    }

}
