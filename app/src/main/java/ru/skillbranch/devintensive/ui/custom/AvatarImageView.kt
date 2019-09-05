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
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import kotlin.math.min

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
    }

    private var avatarSize: Int = 0
    private var rect: Rect = Rect()
    private var rectF: RectF = RectF()
    private var pathR: Path = Path()
    private lateinit var paintText: Paint
    private lateinit var paintBorder: Paint
    private lateinit var canvas: Canvas
    private var initials: String? = null
    private var borderColor = DEFAULT_BORDER_COLOR
    @Dimension(unit = Dimension.DP)
    private var borderWidth = convertDpToPixels(2f, context)
    private var bitmap: Bitmap? = null
    private val bgColors = arrayOf(
        "#7BC862",
        "#E17076",
        "#FAA774",
        "#6EC9CB",
        "#65AADD",
        "#A695E7",
        "#EE7AAE"
    )


    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable()

        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0) {
            bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)
        }

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    private fun getBitmapFromDrawable(): Bitmap {

        return if (drawable != null && drawable is BitmapDrawable)
            (drawable as BitmapDrawable).bitmap
        else {
            var initials = initials
            if (initials == null)
                initials = "??"
            paintText = Paint(Paint.ANTI_ALIAS_FLAG)
            paintText.color = Color.parseColor(bgColors[initials.hashCode() % bgColors.size])
            paintText.style = Paint.Style.FILL

            val bitmap = Bitmap.createBitmap(layoutParams.width, layoutParams.height, BITMAP_CONFIG)
            canvas = Canvas(bitmap)
            canvas.drawPaint(paintText)
            paintText.textSize = layoutParams.width.toFloat() * 0.4F
            paintText.color = Color.WHITE
            paintText.textAlign = Paint.Align.CENTER
            val textBounds = rect
            paintText.getTextBounds(initials, 0, initials.length, textBounds)
            val backgroundBounds = rectF
            backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())
            val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
            canvas.drawText(initials, backgroundBounds.centerX(), textBottom, paintText)

            bitmap
        }
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
        canvas = Canvas(outputBmp)

        paintBorder = Paint()
        paintBorder.style = Paint.Style.FILL
        paintBorder.strokeWidth = 10f
        paintBorder.isAntiAlias = true
        paintBorder.isFilterBitmap = true
        paintBorder.isDither = true

        rect = Rect(0, 0, smallest, smallest)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paintBorder)
        paintBorder.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        canvas.drawBitmap(bitmap, rect, rect, paintBorder)

        return outputBmp
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Float, color: Int): Bitmap {
        val inCircle = rectF
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F
        inCircle.set(strokeStart, strokeStart, strokeEnd, strokeEnd)

        paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBorder.color = color
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = strokeWidth
        canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, paintBorder)

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

    fun setInitials(initials: String?) {
        this.initials = initials
    }

    fun convertDpToPixels(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}