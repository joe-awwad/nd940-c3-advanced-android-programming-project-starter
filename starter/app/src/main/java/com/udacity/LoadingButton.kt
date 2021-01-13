package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

/**
 * Custom loading button view.
 *
 * Text centering code is adapted from https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
 */
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator.ofFloat(0f, 100f)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Downloading -> {
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.end()
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private val textHeight: Float = paint.descent() - paint.ascent()
    private val textOffset: Float = textHeight / 2 - paint.descent()
    private var textBounds = RectF()
    private var circleBounds = RectF()

    private val downloadText = context.getText(R.string.button_name)
    private val loadingText = context.getText(R.string.button_loading)

    private var mainColor = 0
    private var loadingColor = 0
    private var textColor = 0
    private var circleColor = 0

    private var loadingWidth = 0f
    private var sweepAngle = 0f

    private var radius = 30f
    private val circleOffset = 50f

    init {
        isClickable = true


        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            mainColor = getColor(R.styleable.LoadingButton_mainColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        valueAnimator.duration = 1000
        valueAnimator.repeatCount = 3600
        valueAnimator.repeatMode = ValueAnimator.REVERSE

        valueAnimator.addUpdateListener {
            loadingWidth = widthSize.toFloat() * valueAnimator.animatedFraction
            sweepAngle = 360f * valueAnimator.animatedFraction
            invalidate()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                loadingWidth = 0f
                invalidate()
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Completed -> drawButtonInCompletedState(canvas)
            ButtonState.Downloading -> drawButtonInDownloadingState(canvas)
        }
    }

    fun onDownloadStartedEvent() {
        isEnabled = false
        buttonState = ButtonState.Downloading
    }

    fun onDownloadCompletedEvent() {
        isEnabled = true
        buttonState = ButtonState.Completed
    }

    private fun drawButtonInCompletedState(canvas: Canvas?) {
        paint.color = mainColor
        canvas?.drawRect(loadingWidth, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawButtonText(downloadText)
    }

    private fun drawButtonInDownloadingState(canvas: Canvas?) {
        // Draw loading color
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, loadingWidth, heightSize.toFloat(), paint)

        // Draw main color
        paint.color = mainColor
        canvas?.drawRect(loadingWidth, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        // Draw button text
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawButtonText(loadingText)

        // Draw arc
        paint.color = circleColor
        canvas?.drawArc(circleBounds, 0f, sweepAngle, false, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        textBounds = RectF(0f, 0f, widthSize.toFloat(), heightSize.toFloat())

        val bounds = Rect()
        paint.getTextBounds(loadingText.toString(), 0, loadingText.length, bounds)

        val xA = textBounds.centerX() + bounds.width() / 2 + circleOffset
        val yA = textBounds.centerY() - bounds.height() / 2

        val xB = xA + 2 * radius
        val yB = textBounds.centerY() + radius

        circleBounds = RectF(xA, yA, xB, yB)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        onDownloadStartedEvent()

        return true
    }

    private fun Canvas.drawButtonText(text: CharSequence) {
        drawText(
            text.toString(),
            textBounds.centerX(),
            textBounds.centerY() + textOffset,
            paint
        )
    }
}