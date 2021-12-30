package com.dcurreli.spese.utils

import android.util.Log
import com.dcurreli.spese.objects.Mese
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DatabaseReference

class MeseUtils {
    companion object Static {
        protected val TAG = javaClass.simpleName

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
    }
}