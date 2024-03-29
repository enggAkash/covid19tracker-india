package online.engineerakash.covid19india.chartutil

import android.graphics.*
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.utils.Utils

class Fill {
    /**
     * the drawable to be used for filling
     */
    protected var mDrawable: Drawable? = null

    /**
     * the type of fill
     */
    var type = Type.EMPTY

    /**
     * the color that is used for filling
     */
    var color: Int? = null
        private set
    private var mFinalColor: Int? = null
    var gradientColors: IntArray? = null
    var gradientPositions: FloatArray? = null

    /**
     * transparency used for filling
     */
    private var mAlpha = 255

    constructor() {}
    constructor(color: Int) {
        type = Type.COLOR
        this.color = color
        calculateFinalColor()
    }

    constructor(startColor: Int, endColor: Int) {
        type = Type.LINEAR_GRADIENT
        gradientColors = intArrayOf(startColor, endColor)
    }

    constructor(gradientColors: IntArray) {
        type = Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
    }

    constructor(gradientColors: IntArray, gradientPositions: FloatArray) {
        type = Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
        this.gradientPositions = gradientPositions
    }

    constructor(drawable: Drawable) {
        type = Type.DRAWABLE
        mDrawable = drawable
    }

    fun setColor(color: Int) {
        this.color = color
        calculateFinalColor()
    }

    fun setGradientColors(startColor: Int, endColor: Int) {
        gradientColors = intArrayOf(startColor, endColor)
    }

    var alpha: Int
        get() = mAlpha
        set(alpha) {
            mAlpha = alpha
            calculateFinalColor()
        }

    private fun calculateFinalColor() {
        mFinalColor = if (color == null) {
            null
        } else {
            val alpha = Math.floor((color!! shr 24) / 255.0 * (mAlpha / 255.0) * 255.0)
                .toInt()
            alpha shl 24 or (color!! and 0xffffff)
        }
    }

    fun fillRect(
        c: Canvas, paint: Paint,
        left: Float, top: Float, right: Float, bottom: Float,
        gradientDirection: Direction
    ) {
        when (type) {
            Type.EMPTY -> return
            Type.COLOR -> {
                if (mFinalColor == null) return
                if (isClipPathSupported) {
                    val save = c.save()
                    c.clipRect(left, top, right, bottom)
                    c.drawColor(mFinalColor!!)
                    c.restoreToCount(save)
                } else {
                    // save
                    val previous = paint.style
                    val previousColor = paint.color

                    // set
                    paint.style = Paint.Style.FILL
                    paint.color = mFinalColor!!
                    c.drawRect(left, top, right, bottom, paint)

                    // restore
                    paint.color = previousColor
                    paint.style = previous
                }
            }
            Type.LINEAR_GRADIENT -> {
                if (gradientColors == null) return
                val gradient: LinearGradient = LinearGradient(
                    (if (gradientDirection == Direction.RIGHT) right else if (gradientDirection == Direction.LEFT) left else left),
                    (if (gradientDirection == Direction.UP) bottom else if (gradientDirection == Direction.DOWN) top else top),
                    (if (gradientDirection == Direction.RIGHT) left else if (gradientDirection == Direction.LEFT) right else left),
                    (if (gradientDirection == Direction.UP) top else if (gradientDirection == Direction.DOWN) bottom else top),
                    gradientColors!!,
                    gradientPositions,
                    Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawRect(left, top, right, bottom, paint)
            }
            Type.DRAWABLE -> {
                if (mDrawable == null) return
                mDrawable!!.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                mDrawable!!.draw(c)
            }
        }
    }

    fun fillPath(
        c: Canvas, path: Path?, paint: Paint,
        clipRect: RectF?
    ) {
        when (type) {
            Type.EMPTY -> return
            Type.COLOR -> {
                if (mFinalColor == null) return
                if (clipRect != null && isClipPathSupported) {
                    val save = c.save()
                    c.clipPath(path!!)
                    c.drawColor(mFinalColor!!)
                    c.restoreToCount(save)
                } else {
                    // save
                    val previous = paint.style
                    val previousColor = paint.color

                    // set
                    paint.style = Paint.Style.FILL
                    paint.color = mFinalColor!!
                    c.drawPath(path!!, paint)

                    // restore
                    paint.color = previousColor
                    paint.style = previous
                }
            }
            Type.LINEAR_GRADIENT -> {
                if (gradientColors == null) return
                val gradient: LinearGradient = LinearGradient(
                    0.toFloat(),
                    0.toFloat(),
                    c.width.toFloat(),
                    c.height.toFloat(),
                    gradientColors!!,
                    gradientPositions,
                    Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawPath(path!!, paint)
            }
            Type.DRAWABLE -> {
                if (mDrawable == null) return
                ensureClipPathSupported()
                val save = c.save()
                c.clipPath(path!!)
                mDrawable!!.setBounds(
                    clipRect?.left?.toInt() ?: 0,
                    clipRect?.top?.toInt() ?: 0,
                    clipRect?.right?.toInt() ?: c.width,
                    clipRect?.bottom?.toInt() ?: c.height
                )
                mDrawable!!.draw(c)
                c.restoreToCount(save)
            }
        }
    }

    private val isClipPathSupported: Boolean = Utils.getSDKInt() >= 18

    private fun ensureClipPathSupported() {
        if (Utils.getSDKInt() < 18) {
            throw RuntimeException(
                "Fill-drawables not (yet) supported below API level 18, " +
                        "this code was run on API level " + Utils.getSDKInt() + "."
            )
        }
    }

    enum class Type {
        EMPTY, COLOR, LINEAR_GRADIENT, DRAWABLE
    }

    enum class Direction {
        DOWN, UP, RIGHT, LEFT
    }
}