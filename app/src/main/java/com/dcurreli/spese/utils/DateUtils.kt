package com.dcurreli.spese.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val className = javaClass.simpleName

    @RequiresApi(Build.VERSION_CODES.O)

    fun getMonthAsText(mese: String): String {
        return when (mese) {
            "1", "01" -> "Gennaio"
            "2", "02" -> "Febbraio"
            "3", "03" -> "Marzo"
            "4", "04" -> "Aprile"
            "5", "05" -> "Maggio"
            "6", "06" -> "Giugno"
            "7", "07" -> "Luglio"
            "8", "08" -> "Agosto"
            "9", "09" -> "Settembre"
            "10" -> "Ottobre"
            "11" -> "Novembre"
            "12" -> "Dicembre"
            else -> "null"
        }
    }

    fun getMonthAsNumber(mese: String): String {
        return when (mese) {
            "Gennaio" -> "01"
            "Febbraio" -> "02"
            "Marzo" -> "03"
            "Aprile" -> "04"
            "Maggio" -> "05"
            "Giugno" -> "06"
            "Luglio" -> "07"
            "Agosto" -> "08"
            "Settembre" -> "09"
            "Ottobre" -> "10"
            "Novembre" -> "11"
            "Dicembre" -> "12"
            else -> "null"
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    fun deleteMese(spesa: Spesa) {
        val dataForQuery = createDataForQueryFromSpesa(spesa)

        //Recupero il mese da cancellare
        db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble())
            .endBefore(dataForQuery.endsAt.toDouble()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val mese = it.children.first().getValue(Mese::class.java) as Mese
                    db.child(mese.id.toString()).removeValue() //Cancello
                }
            }.addOnFailureListener {
                Log.e(className, "<< Error getting mese", it)
            }
    }*/

    fun formatData(data: Date): String {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        return sdf.format(data)
    }
}