package com.dcurreli.spese.utils

import android.util.Log
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DatabaseReference

class SpesaUtils {
    companion object Static {
        protected val TAG = javaClass.simpleName

        fun creaSepsa(db: DatabaseReference, luogo: String, importo: String, data: String, pagatore: String ) {
            val methodName: String = "creaSpesa"
            Log.i(TAG, ">>$methodName")
            db.child("spesa").orderByChild("id").limitToLast(1).get().addOnSuccessListener {
                var newId: Int = 1

                if(it.exists()) {
                    var id: Int? =  it.children.first().child("id").value.toString().toInt()
                    if (id != null) {
                        newId = (id + 1)
                    }
                }
                //Nuova spesa
                val spesa = Spesa(newId,luogo,importo.toDouble(),data,pagatore)

                //Creo spesa
                db.child("spesa").child(newId.toString()).setValue(spesa)
                //Creo mese
                MeseUtils.creaMese(db, spesa)


                Log.i(TAG, "<<$methodName")
            }.addOnFailureListener{
                Log.e(TAG, "<<$methodName Error getting spesa", it)
            }
        }
    }
}