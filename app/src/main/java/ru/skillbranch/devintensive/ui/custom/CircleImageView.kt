package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.graphics.drawable.toBitmap
import ru.skillbranch.devintensive.R
import java.lang.Exception
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    }

    val justPaint = Paint()

    private var borderColor = DEFAULT_BORDER_COLOR
    @Dimension(unit = DP)
    private var borderWidth = convertDpToPixels(2f, context)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            a.recycle()
        }
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = borderColor
        strokeWidth = borderWidth
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.drawOval(0f, 0f, width.toFloat(), height.toFloat(), justPaint.apply {
            shader = BitmapShader(drawable.toBitmap(width, height), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        })
        if (borderWidth > 0) {
            canvas?.drawOval(
                borderWidth * 0.5f,
                borderWidth * 0.5f,
                width.toFloat() - borderWidth * 0.5f,
                height.toFloat() - borderWidth * 0.5f,
                borderPaint
            )
        }
    }

    @Dimension
    fun getBorderWidth(): Int = convertPixelsToDp(borderWidth, context).toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = convertDpToPixels(dp.toFloat(), context)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = colorId
        borderPaint.color = borderColor
        invalidate()
    }

    fun generateAvatar(initials: String, theme: Resources.Theme) {
        this.post {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = width.toFloat() * 0.4f
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            val baseline = height * 0.5f - (paint.descent() + paint.ascent()) * 0.5f
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            @ColorInt val color = typedValue.data
            canvas.drawColor(color)
            canvas.drawText(initials, width.toFloat()/2, baseline, paint)
            setImageBitmap(image)
        }
    }

    fun convertDpToPixels(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}