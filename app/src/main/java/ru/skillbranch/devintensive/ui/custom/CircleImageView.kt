package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
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


    private var initials: String = ""
    private var borderColor = DEFAULT_BORDER_COLOR
    @Dimension(unit = DP)
    private var borderWidth = convertDpToPixels(2f, context)
    private var bitmap: Bitmap? = null

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable() ?: return

        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0) {
            bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)
        }

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        if (bitmap != null)
            return bitmap

        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int): Bitmap {
        return if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = min(bitmap.width, bitmap.height).toFloat()
            val factor = smallest / minSide
            Bitmap.createScaledBitmap(bitmap, (bitmap.width / factor).toInt(), (bitmap.height / factor).toInt(), false)
        } else bitmap
    }

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val cropStartX = (bitmap.width - size) / 2
        val cropStartY = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, size, size)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, BITMAP_CONFIG)
        val canvas = Canvas(outputBmp)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f
        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true

        val rect = Rect(0, 0, smallest, smallest)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        canvas.drawBitmap(bitmap, rect, rect, paint)

        return outputBmp
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Float, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F
        inCircle.set(strokeStart, strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()
        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

    @Dimension
    fun getBorderWidth(): Int = convertPixelsToDp(borderWidth, context).toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = convertDpToPixels(dp.toFloat(), context)
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    fun generateAvatar(initials: String?, theme: Resources.Theme) {
        if (initials == null) setImageResource(R.drawable.ic_avatar)
        else if (this.initials != initials) {
            val image = Bitmap.createBitmap(layoutParams.width, layoutParams.height, BITMAP_CONFIG)
            val color = TypedValue()
            theme.resolveAttribute(R.attr.colorAccent, color, true)
            val canvas = Canvas(image)
            canvas.drawColor(color.data)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = layoutParams.width.toFloat() * 0.4F
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            val textBounds = Rect()
            paint.getTextBounds(initials, 0, initials.length, textBounds)
            val backgroundBounds = RectF()
            backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())
            val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
            canvas.drawText(initials, backgroundBounds.centerX(), textBottom, paint)
            setImageBitmap(image)
            invalidate()

            this.initials = initials
        }
    }

    fun convertDpToPixels(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}