package com.convergencelabstfx.smartdrone.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.convergencelabstfx.smartdrone.R
import timber.log.Timber
import kotlin.math.roundToInt


class VoicingTemplateView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val NUM_CHORD_TONES = 14
        // Even though the size is 5, there are only 2 used bass tones:
        // 0 and 4. The indices in between are not used.
        const val NUM_BASS_TONES = 5
    }

    private val chordDegreeDrawables = ArrayList<GradientDrawable?>(NUM_CHORD_TONES)

    private val bassDegreeDrawables = ArrayList<GradientDrawable?>(NUM_BASS_TONES)

    private val chordDegrees = BooleanArray(NUM_CHORD_TONES)

    private val bassDegrees = BooleanArray(NUM_BASS_TONES)

    private var squareLen: Int = -1

    private var verticalSpacing: Int = -1

    private var cornerRadius: Int = -1

    private var toneSpacing: Int = -1

    private var activeColor: Int = -1

    private var inactiveColor: Int = -1

    private val listeners: MutableList<VoicingTemplateTouchListener> = ArrayList()

    init {
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var isChordTone = false
        var touchedTone = checkChordToneTouch(event!!.x.roundToInt(), event.y.roundToInt())
        if (touchedTone != -1) {
            isChordTone = true
        }
        else {
            touchedTone = checkBassToneTouch(event.x.roundToInt(), event.y.roundToInt())
            if (touchedTone != -1) {
                isChordTone = false
            }
        }

        if (touchedTone != -1 && event.action == MotionEvent.ACTION_UP) {
            for (listener in listeners) {
                listener.onClick(this, touchedTone, isChordTone)
            }
        }
        return true
    }

    fun addListener(listener: VoicingTemplateTouchListener) {
        listeners.add(listener)
    }

    fun activateChordDegree(degree: Int) {
        if (!chordDegrees[degree]) {
            chordDegrees[degree] = true
        }
    }

    fun deactivateChordDegree(degree: Int) {
        if (chordDegrees[degree]) {
            chordDegrees[degree] = false
        }
    }

    fun activateBassDegree(degree: Int) {
        if (!bassDegrees[degree]) {
            bassDegrees[degree] = true
        }
    }

    fun deactivateBassDegree(degree: Int) {
        if (bassDegrees[degree]) {
            bassDegrees[degree] = false
        }
    }

    fun chordDegreeIsActive(degree: Int): Boolean {
        return chordDegrees[degree]
    }

    fun bassDegreeIsActive(degree: Int): Boolean {
        return bassDegrees[degree]
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
        var vOffset = toneSpacing
        var hOffset = toneSpacing
        for (i in 0 until 4) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(inactiveColor)
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables.add(shape)
            vOffset += squareLen + toneSpacing
        }

        vOffset = toneSpacing + squareLen / 2 + toneSpacing / 2
        hOffset = toneSpacing * 2 + squareLen
        for (i in 0 until 3) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(inactiveColor)
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables.add(shape)
            vOffset += squareLen + toneSpacing
        }

        vOffset = toneSpacing
        hOffset = width - toneSpacing - squareLen - toneSpacing - squareLen
        for (i in 0 until 4) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(inactiveColor)
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables.add(shape)
            vOffset += squareLen + toneSpacing
        }

        vOffset = toneSpacing + squareLen / 2 + toneSpacing / 2
        hOffset = width - toneSpacing - squareLen
        for (i in 0 until 3) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(inactiveColor)
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            chordDegreeDrawables.add(shape)
            vOffset += squareLen + toneSpacing
        }

        vOffset = height - squareLen - toneSpacing
        hOffset = width / 2 - squareLen - toneSpacing / 2
        for (i in 0 until 2) {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.setColor(inactiveColor)
            shape.cornerRadius = cornerRadius.toFloat()
            shape.setBounds(hOffset, vOffset, squareLen + hOffset, squareLen + vOffset)
            bassDegreeDrawables.add(shape)
            hOffset += squareLen + toneSpacing
            // todo: quite hacky
            bassDegreeDrawables.add(null)
            bassDegreeDrawables.add(null)
            bassDegreeDrawables.add(null)
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