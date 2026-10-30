package com.example.kayara

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class AnimatedStudentHeroCirclesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val density =
        resources.displayMetrics.density

    private var phase =
        0f

    private var animator:
            ValueAnimator? = null


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        startAnimation()
    }


    override fun onDetachedFromWindow() {

        animator?.cancel()
        animator = null

        super.onDetachedFromWindow()
    }


    private fun startAnimation() {

        animator?.cancel()

        animator =
            ValueAnimator.ofFloat(
                0f,
                1f
            ).apply {

                duration = 8500L

                repeatCount =
                    ValueAnimator.INFINITE

                repeatMode =
                    ValueAnimator.RESTART

                interpolator =
                    LinearInterpolator()

                addUpdateListener {

                    phase =
                        it.animatedValue as Float

                    invalidate()
                }

                start()
            }
    }


    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        if (
            width <= 0 ||
            height <= 0
        ) {
            return
        }

        val w =
            width.toFloat()

        val h =
            height.toFloat()


        // LARGE TEAL CIRCLE

        val largeX =
            sin(
                phase *
                        Math.PI *
                        2
            ).toFloat() *
                    22f *
                    density

        val largeY =
            sin(
                phase *
                        Math.PI *
                        4
            ).toFloat() *
                    8f *
                    density


        paint.color =
            Color.argb(
                40,
                105,
                240,
                220
            )


        canvas.drawCircle(
            w -
                    20f * density +
                    largeX,

            -8f * density +
                    largeY,

            88f * density,

            paint
        )


        // MEDIUM WHITE CIRCLE

        val mediumX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        1.4
            ).toFloat() *
                    30f *
                    density

        val mediumY =
            sin(
                phase *
                        Math.PI *
                        2 +
                        0.8
            ).toFloat() *
                    12f *
                    density


        paint.color =
            Color.argb(
                25,
                255,
                255,
                255
            )


        canvas.drawCircle(
            w -
                    68f * density +
                    mediumX,

            h -
                    18f * density +
                    mediumY,

            48f * density,

            paint
        )


        // SMALL AQUA CIRCLE

        val smallX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        2.7
            ).toFloat() *
                    38f *
                    density

        val smallY =
            sin(
                phase *
                        Math.PI *
                        4 +
                        1.1
            ).toFloat() *
                    10f *
                    density


        paint.color =
            Color.argb(
                45,
                76,
                214,
                194
            )


        canvas.drawCircle(
            w -
                    120f * density +
                    smallX,

            42f * density +
                    smallY,

            25f * density,

            paint
        )


        // SMALL WHITE DOT

        val dotX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        4.4
            ).toFloat() *
                    24f *
                    density

        val dotY =
            sin(
                phase *
                        Math.PI *
                        2 +
                        2.0
            ).toFloat() *
                    13f *
                    density


        paint.color =
            Color.argb(
                55,
                255,
                255,
                255
            )


        canvas.drawCircle(
            w -
                    43f * density +
                    dotX,

            h -
                    40f * density +
                    dotY,

            11f * density,

            paint
        )
    }
}