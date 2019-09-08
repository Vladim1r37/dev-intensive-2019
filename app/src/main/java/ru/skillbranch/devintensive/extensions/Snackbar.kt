package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_profile_constraint.view.*
import ru.skillbranch.devintensive.R

fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
    this.view.setBackgroundColor(colorInt)
    return this
}

fun Snackbar.withDrawableBackground(drawable: Drawable): Snackbar {
    this.view.background = drawable
    return this
}

fun Snackbar.withTextColor(@ColorInt colorInt: Int): Snackbar {
    this.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        .setTextColor(colorInt)
    return this
}

