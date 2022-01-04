package com.dcurreli.spese.utils

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.adapters.MeseAdapter
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.objects.Mese
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

object MeseUtils {
    private val TAG = javaClass.simpleName
    private lateinit var mese: Mese
    private lateinit var meseAdapter : MeseAdapter
    private lateinit var meseArray : ArrayList<Mese>
    private lateinit var db: DatabaseReference

    fun creaMese(db: DatabaseReference, spesa: Spesa, ) {
        val methodName: String = "creaMese"
        Log.i(TAG, ">>$methodName")

        //Vedo se il mese già esiste
        db.child("mese").orderByChild("nome").equalTo(spesa.extractMensilitaAnno()).get().addOnSuccessListener {
            //Se non esiste cerco l'id dell'ultimo mese creato
            if(!it.exists()) {
                db.child("mese").orderByChild("id").limitToLast(1).get().addOnSuccessListener {
                    var newId: Int = 1

                    //se ho ottenuto qualcosa setto l'id a vecchioID + 1
                    if(it.exists()) {
                        var id: Int? =  it.children.first().child("id").value.toString().toInt()
                        if (id != null) {
                            newId = (id + 1)
                        }
                    }

                    //Creo mese
                    val mese = Mese(newId,spesa.extractMensilitaAnno())
                    db.child("mese").child(newId.toString()).setValue(mese)

                }.addOnFailureListener{
                    Log.e(TAG, "<<$methodName Error getting existing month", it)
                }
            }
        }.addOnFailureListener{
            Log.e(TAG, "<<$methodName Error getting mese", it)
        }
    }

    fun printMese(context: Context,binding: ActivityMainBinding,navController: NavController){
        binding.listaMese.setHasFixedSize(true)
        binding.listaMese.layoutManager = LinearLayoutManager(context)
        meseArray = ArrayList()
        meseAdapter = MeseAdapter(context, meseArray, binding,navController)

        binding.listaMese.adapter = meseAdapter

        db = Firebase.database.reference.child("mese")
        db.orderByChild("id").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                meseArray.clear()
                for (snapshot : DataSnapshot in dataSnapshot.children.reversed()){
                    mese = snapshot.getValue(Mese::class.java) as Mese
                    meseArray.add(mese)
                }
                meseAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun getMonthAsText(mese: String): String? {
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

    fun getMonthAsNumber(mese: String): String? {
        return when(mese){
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

    fun getDataFromString(dataString : String, pattern: String): Date {
        var arrayData : List<String> = dataString.split(" ")// 0 giorno, 1 mese, 2 anno

        //Log.i(TAG, "Returning: ${arrayData[0] +"/"+getMonthAsNumber(arrayData[1])+"/"+ arrayData[2]}")
        return GenericUtils.dateStringToDate(arrayData[0] +"/"+getMonthAsNumber(arrayData[1])+"/"+ arrayData[2], pattern)
    }
}