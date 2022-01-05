package com.dcurreli.spese.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

object GenericUtils {
    private val TAG = javaClass.simpleName
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dateStringToTimestampSeconds(data : String, pattern: String) : Long {
        return dateToTimestampSeconds(SimpleDateFormat(pattern).parse(data))
    }

    fun dateToTimestampSeconds(date : Date) : Long {
        return Timestamp(date).seconds
    }

    fun dateStringToDate(dataString : String, pattern : String) : Date {
        return SimpleDateFormat(pattern).parse(dataString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun lastDayOfMonth(dataItem: String): String {
        var arrayData : List<String> = dataItem.split(" ")// 0 giorno, 1 mese, 2 anno
        return ""+ YearMonth.of(arrayData[1].toInt(), MeseUtils.getMonthAsNumber(arrayData[0]).toString().toInt()).atEndOfMonth()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun firstDayOfMonth(dataItem: String): String {
        var arrayData : List<String> = dataItem.split(" ")// 0 giorno, 1 mese, 2 anno
        return ""+ YearMonth.of(arrayData[1].toInt(), MeseUtils.getMonthAsNumber(arrayData[0]).toString().toInt()).atDay(1)
    }

    fun showSnackbarError(message: String, binding: AddSpesaBinding) {
        val snackbar = Snackbar.make(binding.addSpesaConstraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.rgb(180,0,0))
        snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

    fun showSnackbarOK(message: String, binding: AddSpesaBinding) {
        val snackbar = Snackbar.make(binding.addSpesaConstraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.rgb(0,128,0))
        snackbar.setAnchorView(R.id.bottom_nav)
        snackbar.show()
    }

}