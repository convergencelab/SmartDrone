package com.convergencelabstfx.smartdrone.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.convergencelabstfx.smartdrone.R;

// todo: don't look at this class if you have a weak stomach
// todo: show press 'wash' effect
public class DroneButton extends ConstraintLayout {

    private static final int DURATION_MEDIUM = 200;
    private static final float INVISIBLE = 0f;
    private static final float VISIBLE = 1f;

    private TextView mTextView;
    private View mRing;
    private ImageView mImageView;

    private String mText = null;
    private Drawable mDrawable = null;
    private int mRingWidth;

    private Animation mFadeIn;
    private Animation mFadeOut;

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

        mFadeIn = new AlphaAnimation(INVISIBLE, VISIBLE);
        mFadeIn.setDuration(DURATION_MEDIUM);

        mFadeOut = new AlphaAnimation(VISIBLE, INVISIBLE);
        mFadeOut.setDuration(DURATION_MEDIUM);

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

    public void setText(String text, int textSize) {
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { /* Not used. */ }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (textSize != -1) {
                    mTextView.setTextSize(textSize);
                }
                mTextView.setText(text);
                mTextView.startAnimation(mFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { /* Not used. */ }
        });
        mTextView.startAnimation(mFadeOut);
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
