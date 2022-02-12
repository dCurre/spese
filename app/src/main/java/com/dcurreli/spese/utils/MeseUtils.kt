package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dcurreli.spese.adapters.MeseAdapter
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.objects.Mese
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.GenericUtils.dateStringToTimestampSeconds
import com.dcurreli.spese.utils.GenericUtils.firstDayOfMonth
import com.dcurreli.spese.utils.GenericUtils.lastDayOfMonth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

object MeseUtils {
    private val className = javaClass.simpleName
    private lateinit var mese: Mese
    @SuppressLint("StaticFieldLeak") private lateinit var meseAdapter: MeseAdapter
    private lateinit var meseArray: ArrayList<Mese>
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.MESE.value)

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaMese(spesa: Spesa) {
        val methodName = "creaMese"
        Log.i(className, ">>$methodName")

        //Vedo se il mese già esiste
        db.orderByChild("nome").equalTo(spesa.extractMensilitaAnno()).get().addOnSuccessListener {
            //Se non esiste cerco l'id dell'ultimo mese creato
            if (!it.exists()) {
                val newKey = db.push().key!!
                //Creo mese
                val mese = Mese(newKey, spesa.extractMensilitaAnno())
                db.child(newKey).setValue(mese)
            }
        }.addOnFailureListener {
            Log.e(className, "<<$methodName Error getting mese", it)
        }
        Log.i(className, "<<$methodName")
    }

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

    fun getNomeMeseFromSpesaData(spesaData: String): String {
        val arrayData = spesaData.split("/")// 0 giorno, 1 mese, 2 anno

        return "${getMonthAsText(arrayData[1])} ${arrayData[2]}"
    }

    fun getDataFromString(dataString: String, pattern: String): Date {
        val arrayData: List<String> = dataString.split(" ")// 0 giorno, 1 mese, 2 anno

        return GenericUtils.dateStringToDate(
            arrayData[0] + "/" + getMonthAsNumber(arrayData[1]) + "/" + arrayData[2],
            pattern
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createDataForQueryFromSpesa(spesa: Spesa): DataForQuery {
        val pattern = "yyyy-MM-dd"
        val nomeMese = getNomeMeseFromSpesaData(spesa.data)
        val startsAt = "" + dateStringToTimestampSeconds(firstDayOfMonth(nomeMese), pattern)
        val endsAt = "" + dateStringToTimestampSeconds(lastDayOfMonth(nomeMese), pattern)
        return DataForQuery(startsAt.toDouble(), endsAt.toDouble())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createDataForQueryFromMeseAnno(nomeMese: String): DataForQuery {
        val pattern = "yyyy-MM-dd"
        val startsAt = "" + dateStringToTimestampSeconds(firstDayOfMonth(nomeMese), pattern)
        val endsAt = "" + dateStringToTimestampSeconds(lastDayOfMonth(nomeMese), pattern)
        return DataForQuery(startsAt.toDouble(), endsAt.toDouble())
    }

    fun formatData(data: Date): String {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        return sdf.format(data)
    }
}