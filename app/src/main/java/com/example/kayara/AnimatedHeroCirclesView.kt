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

class AnimatedHeroCirclesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    private val density =
        resources.displayMetrics.density

    private val paint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        )

    private var phase =
        0f

    private var animator:
            ValueAnimator? = null

    init {
        setLayerType(
            LAYER_TYPE_SOFTWARE,
            null
        )
    }

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

                duration =
                    8500L

                repeatCount =
                    ValueAnimator.INFINITE

                repeatMode =
                    ValueAnimator.RESTART

                interpolator =
                    LinearInterpolator()

                addUpdateListener {

                    phase =
                        it.animatedValue
                                as Float

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

        /*
         * =====================================================
         * LARGE CIRCLE
         * top-right moving glow
         * =====================================================
         */

        val largeMoveX =
            sin(
                phase *
                        Math.PI *
                        2
            ).toFloat() *
                    22f *
                    density

        val largeMoveY =
            sin(
                phase *
                        Math.PI *
                        4
            ).toFloat() *
                    8f *
                    density

        paint.color =
            Color.argb(
                34,
                93,
                230,
                255
            )

        canvas.drawCircle(
            w -
                    25f * density +
                    largeMoveX,

            -12f * density +
                    largeMoveY,

            88f * density,

            paint
        )

        /*
         * =====================================================
         * MEDIUM CIRCLE
         * moves around right-middle
         * =====================================================
         */

        val mediumMoveX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        1.5
            ).toFloat() *
                    30f *
                    density

        val mediumMoveY =
            sin(
                phase *
                        Math.PI *
                        2 +
                        0.7
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
                    72f * density +
                    mediumMoveX,

            h -
                    20f * density +
                    mediumMoveY,

            48f * density,

            paint
        )

        /*
         * =====================================================
         * SMALL CYAN CIRCLE
         * slow floating circle
         * =====================================================
         */

        val smallMoveX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        2.8
            ).toFloat() *
                    38f *
                    density

        val smallMoveY =
            sin(
                phase *
                        Math.PI *
                        4 +
                        1.2
            ).toFloat() *
                    10f *
                    density

        paint.color =
            Color.argb(
                35,
                71,
                214,
                207
            )

        canvas.drawCircle(
            w -
                    120f * density +
                    smallMoveX,

            42f * density +
                    smallMoveY,

            25f * density,

            paint
        )

        /*
         * =====================================================
         * SMALL WHITE FLOATING DOT
         * =====================================================
         */

        val dotMoveX =
            sin(
                phase *
                        Math.PI *
                        2 +
                        4.5
            ).toFloat() *
                    24f *
                    density

        val dotMoveY =
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
                50,
                255,
                255,
                255
            )

        canvas.drawCircle(
            w -
                    45f * density +
                    dotMoveX,

            h -
                    42f * density +
                    dotMoveY,

            11f * density,

            paint
        )
    }
}