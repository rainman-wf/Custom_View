package ru.netology.customview.utils

import android.content.Context
import kotlin.math.ceil

fun db(context: Context, db: Float): Int =
    ceil(context.resources.displayMetrics.density * db).toInt()