package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Timestamp
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*

object GenericUtils {
    private val className = javaClass.simpleName
    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dateStringToTimestampSeconds(data: String, pattern: String): Long {
        return dateToTimestampSeconds(SimpleDateFormat(pattern).parse(data))
    }

    fun dateToTimestampSeconds(date: Date?): Long {
        return Timestamp(date!!).seconds
    }

    fun dateStringToDate(dataString: String, pattern: String): Date? {
        return SimpleDateFormat(pattern).parse(dataString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun lastDayOfMonth(dataItem: String): String {
        val arrayData: List<String> = dataItem.split(" ")// 0 giorno, 1 mese, 2 anno
        return "" + YearMonth.of(
            arrayData[1].toInt(),
            DateUtils.getMonthAsNumber(arrayData[0]).toInt()
        ).atEndOfMonth()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun firstDayOfMonth(dataItem: String): String {
        val arrayData: List<String> = dataItem.split(" ")// 0 giorno, 1 mese, 2 anno
        return "" + YearMonth.of(
            arrayData[1].toInt(),
            DateUtils.getMonthAsNumber(arrayData[0]).toInt()
        ).atDay(1)
    }

    // Gestisco preferenze tema scuro/chiaro
    fun onOffDarkTheme(bool: Boolean) {
        when (bool) {
            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    // Gestisco preferenze tema scuro/chiaro
    fun onOffSaldato(db: DatabaseReference, idLista : String, bool: Boolean) {
        when (bool) {
            true -> db.child(idLista).child("saldato").setValue(true)
            false -> db.child(idLista).child("saldato").setValue(false)
        }
    }

    fun setupSottotitoloToolbar(message: String?, activity: AppCompatActivity?){
        (activity)?.supportActionBar?.subtitle = message
    }

    fun clearSottotitoloToolbar(activity: AppCompatActivity?){
        setupSottotitoloToolbar(null, activity)
    }

    fun importoAsEur(double : Double): String {
        return "${String.format("%.2f", double)}€".replace(".", ",")
    }


    fun createBundleForListaSpese(idLista: String, nomeLista: String?): Bundle {
        //Utile per creare un bundle per load spese fragment
        val bundle = Bundle()
        bundle.putString("idLista", idLista)
        bundle.putString("nomeLista", nomeLista)
        return bundle
    }

    fun createBundleForListaSpese(arguments: Bundle?): Bundle {
        //Utile per creare un bundle per load spese fragment
        val bundle = Bundle()
        bundle.putString("idLista", arguments?.getString("idLista"))
        bundle.putString("nomeLista", arguments?.getString("nomeLista"))
        return bundle
    }

    fun setupSwitch(@SuppressLint("UseSwitchCompatOrMaterialCode") switch: Switch, isActive: Boolean){
        when (isActive) {
            true -> switch.isChecked = true
            false -> switch.isChecked = false
        }
    }
}