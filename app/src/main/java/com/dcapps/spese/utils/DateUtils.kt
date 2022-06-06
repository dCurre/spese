package com.dcapps.spese.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @SuppressLint("SimpleDateFormat")
    fun formatData(data: Date): String {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        return sdf.format(data)
    }
}