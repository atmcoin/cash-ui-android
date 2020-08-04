package com.square.project.base

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat


@AnyRes
fun Context.getThemeResource(@AttrRes  attr: Int): Int {
    return TypedValue().apply { theme.resolveAttribute(attr, this, true) }.resourceId
}

@ColorInt
fun Context.getThemeColor(@AttrRes attr:Int): Int {
    return TypedValue().apply { theme.resolveAttribute(attr, this, true) }.data
}

fun Context.getThemeColorStateList(@AttrRes attr: Int): ColorStateList? {
    return ContextCompat.getColorStateList(this, getThemeResource(attr))
}