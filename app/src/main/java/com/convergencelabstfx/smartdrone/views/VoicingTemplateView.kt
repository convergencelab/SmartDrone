package com.convergencelabstfx.smartdrone.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate
import com.convergencelabstfx.smartdrone.R
import timber.log.Timber
import kotlin.math.roundToInt


// todo: some of the code in this class is pretty bad, but it works how it's supposed to
class VoicingTemplateView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val NUM_CHORD_TONES = 14
        // Even though the size is 5, there are only 2 used bass tones:
        // 0 and 4. The indices in between are not used.
        const val NUM_BASS_TONES = 5
        val intPattern = intArrayOf( 6, 4, 2, 0, 5, 3, 1, 13, 11, 9, 7, 12, 10, 8 )
    }

    private val chordDegreeDrawables = Array<GradientDrawable?>(NUM_CHORD_TONES) {null}

    private val bassDegreeDrawables = Array<GradientDrawable?>(NUM_BASS_TONES) {null}

    private val chordDegrees = BooleanArray(NUM_CHORD_TONES)

    private val bassDegrees = BooleanArray(NUM_BASS_TONES)

    private var squareLen: Int = -1

    private var verticalSpacing: Int = -1

    private var cornerRadius: Int = -1

    private var toneSpacing: Int = -1

    private var activeColor: Int = -1

    private var inactiveColor: Int = -1

    var touchListener: VoicingTemplateTouchListener? = null

    init {
//        for (i in 0 until NUM_CHORD_TONES) {
//            chordDegrees.
//        }
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.VoicingTemplateView,
                0,
                0
        )
        parseAttrs(a)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        Timber.i("onDraw called")
        for (drawable in chordDegreeDrawables) {
            drawable?.draw(canvas!!)
        }
        for (drawable in bassDegreeDrawables) {
            drawable?.draw(canvas!!)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Timber.i("onSizeChanged called")
        squareLen = (h - 7 * toneSpacing - verticalSpacing) / 5
        constructView()
    }

    // todo: make it easier to press by checking the space inbetween the tone buttons
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            var touchedTone = checkChordToneTouch(event!!.x.roundToInt(), event.y.roundToInt())

            if (touchedTone != -1) {
                touchListener?.onChordToneClick(this, touchedTone)
            }
            else {
                touchedTone = checkBassToneTouch(event.x.roundToInt(), event.y.roundToInt())
                if (touchedTone != -1) {
                    touchListener?.onBassToneClick(this, touchedTone)
                }
            }
        }
//
//        if (touchedTone != -1 && event.action == MotionEvent.ACTION_UP) {
//            if (isChordTone) {
//                if (chordDegreeIsActive(touchedTone)) {
//                    deactivateChordDegree(touchedTone)
//                }
//                else {
//                    activateChordDegree(touchedTone)
//                }
//            }
//            else {
//                if (bassDegreeIsActive(touchedTone)) {
//                    deactivateBassDegree(touchedTone)
//                }
//                else {
//                    activateBassDegree(touchedTone)
//                }
//            }
//            for (listener in listeners) {
//                listener.onClick(this, touchedTone, isChordTone)
//            }
//        }
        return true
    }

    fun activateChordDegree(degree: Int) {
        if (!chordDegrees[degree]) {
            chordDegrees[degree] = true
            chordDegreeDrawables[degree]?.setColor(activeColor)
            invalidate()
        }
    }

    fun deactivateChordDegree(degree: Int) {
        if (chordDegrees[degree]) {
            chordDegrees[degree] = false
            chordDegreeDrawables[degree]?.setColor(inactiveColor)
            invalidate()
        }
    }

    fun activateBassDegree(degree: Int) {
        if (!bassDegrees[degree]) {
            bassDegrees[degree] = true
            bassDegreeDrawables[degree]?.setColor(activeColor)
            invalidate()
        }
    }

    fun deactivateBassDegree(degree: Int) {
        if (bassDegrees[degree]) {
            bassDegrees[degree] = false
            bassDegreeDrawables[degree]?.setColor(inactiveColor)
            invalidate()
        }
    }

    fun chordDegreeIsActive(degree: Int): Boolean {
        return chordDegrees[degree]
    }

    fun bassDegreeIsActive(degree: Int): Boolean {
        return bassDegrees[degree]
    }

    fun showTemplate(template: VoicingTemplate) {
        this.clear()
        for (degree in template.chordTones) {
            this.activateChordDegree(degree)
        }
        for (degree in template.bassTones) {
            this.activateBassDegree(degree)
        }
    }

    fun clear() {
        for ((ix, drawable) in chordDegreeDrawables.withIndex()) {
            deactivateChordDegree(ix)
        }
        deactivateBassDegree(0)
        deactivateBassDegree(4)
    }

    private fun parseAttrs(a: TypedArray) {
        activeColor = a.getColor(
                R.styleable.VoicingTemplateView_vt_activeColor,
                resources.getColor(R.color.template_activeColor)
        )

        inactiveColor = a.getColor(
                R.styleable.VoicingTemplateView_vt_inactiveColor,
                resources.getColor(R.color.template_inactiveColor)
        )

        toneSpacing = a.getDimension(
                R.styleable.VoicingTemplateView_vt_toneSpacing,
                resources.getDimension(R.dimen.template_toneSpacing)
        ).toInt()

        verticalSpacing = a.getDimension(
                R.styleable.VoicingTemplateView_vt_verticalSpacing,
                resources.getDimension(R.dimen.template_verticalSpacing)
        ).toInt()

        cornerRadius = a.getDimension(
                R.styleable.VoicingTemplateView_vt_cornerRadius,
                resources.getDimension(R.dimen.template_cornerRadius)
        ).toInt()
    }

    // todo: could extract the drawable construction into a function, but hey, who cares
    private fun constructView() {
        var counter = 0
        var vOffset = toneSpacing
        var hOffset = toneSpacing
        for (i in 0..3) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            if (chordDegreeIsActive(intPattern[counter])) {
                shape.setColor(activeColor)
            }
            else {
                shape.setColor(inactiveColor)
            }
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables[intPattern[counter]] = shape
            vOffset += squareLen + toneSpacing
            counter++
        }

        vOffset = toneSpacing + squareLen / 2 + toneSpacing / 2
        hOffset = toneSpacing * 2 + squareLen
        for (i in 0..2) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            if (chordDegreeIsActive(intPattern[counter])) {
                shape.setColor(activeColor)
            }
            else {
                shape.setColor(inactiveColor)
            }
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables[intPattern[counter]] = (shape)
            vOffset += squareLen + toneSpacing
            counter++
        }


        vOffset = toneSpacing
        hOffset = width - toneSpacing - squareLen - toneSpacing - squareLen
        for (i in 0..3) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            if (chordDegreeIsActive(intPattern[counter])) {
                shape.setColor(activeColor)
            }
            else {
                shape.setColor(inactiveColor)
            }
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables[intPattern[counter]] = (shape)
            vOffset += squareLen + toneSpacing
            counter++
        }

        vOffset = toneSpacing + squareLen / 2 + toneSpacing / 2
        hOffset = width - toneSpacing - squareLen
        for (i in 0..2) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            if (chordDegreeIsActive(intPattern[counter])) {
                shape.setColor(activeColor)
            }
            else {
                shape.setColor(inactiveColor)
            }
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables[intPattern[counter]] = (shape)
            vOffset += squareLen + toneSpacing
            counter++
        }

        vOffset = height - squareLen - toneSpacing
        hOffset = width / 2 - squareLen - toneSpacing / 2
        for (i in 0..4) {
            if (i in 1..3) {
                bassDegreeDrawables[i] = null
            }
            else {
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.setColor(inactiveColor)
                shape.cornerRadius = cornerRadius.toFloat()
                shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
                if (bassDegreeIsActive(i)) {
                    shape.setColor(activeColor)
                }
                else {
                    shape.setColor(inactiveColor)
                }
                bassDegreeDrawables[i] = shape
                hOffset += squareLen + toneSpacing
            }
        }
    }

    private fun checkChordToneTouch(x: Int, y: Int): Int {
        for ((ix, drawable) in chordDegreeDrawables.withIndex()) {
            if (x >= drawable?.bounds!!.left && x <= drawable.bounds.right && y >= drawable.bounds.top && y <= drawable.bounds.bottom) {
                return ix
            }
        }
        return -1
    }

    // todo: quite hacky
    private fun checkBassToneTouch(x: Int, y: Int): Int {
//        for ((ix, drawable) in bassDegreeDrawables.withIndex()) {
//            if (x >= drawable?.bounds!!.left && x <= drawable.bounds.right && y >= drawable.bounds.top && y <= drawable.bounds.bottom) {
//                return ix
//            }
//        }
        var drawable = bassDegreeDrawables[0]
        if (x >= drawable?.bounds!!.left && x <= drawable.bounds.right && y >= drawable.bounds.top && y <= drawable.bounds.bottom) {
            return 0
        }
        drawable = bassDegreeDrawables[4]
        if (x >= drawable?.bounds!!.left && x <= drawable!!.bounds.right && y >= drawable.bounds.top && y <= drawable.bounds.bottom) {
            return 4
        }
        return -1
    }



}