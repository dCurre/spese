package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val className = javaClass.simpleName

    @SuppressLint("SimpleDateFormat")
    fun formatData(data: Date): String {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        return sdf.format(data)
    }
}