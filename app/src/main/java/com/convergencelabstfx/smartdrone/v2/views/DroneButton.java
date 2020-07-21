package com.convergencelabstfx.smartdrone.v2.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.convergencelabstfx.smartdrone.R;

public class DroneButton extends ConstraintLayout {

    private TextView mTextView;
    private View mRing;
    private ImageView mImageView;

    private String mText = null;
    private Drawable mDrawable = null;
    private int mRingWidth;

    public DroneButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DroneButton,
                0, 0
        );
        parseAttrs(a);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View droneButton = inflater.inflate(R.layout.drone_button, this, false);
        this.addView(droneButton);
        
        mTextView = this.findViewById(R.id.textView);
        mRing = this.findViewById(R.id.ring);
        mImageView = this.findViewById(R.id.image);

        if (mDrawable != null) {
            mDrawable.setTint(Color.BLACK);
            mImageView.setImageDrawable(mDrawable);
        }
        if (mText != null) {
            mTextView.setText(mText);
        }
    }

    public void setText(String text) {
        // todo: animate text
        mTextView.setText(text);
    }

    public void setRingColor(int color) {
        // todo: animate color
        mRing.getBackground().setTint(color);
    }

    public void setIcon(Drawable drawable) {
        // todo: animate drawable
        // todo: i know this is hacky; probably come back later with a better solution
        drawable.setTint(Color.BLACK);
        mImageView.setImageDrawable(drawable);
    }

    private void parseAttrs(TypedArray a) {
        mText = a.getString(R.styleable.DroneButton_db_text);
        mDrawable = a.getDrawable(R.styleable.DroneButton_db_icon);
        mRingWidth = Math.round(a.getDimension(R.styleable.DroneButton_db_ringWidth, -1));
    }

}
