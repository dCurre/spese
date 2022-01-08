package com.dcurreli.spese.utils

import android.content.Context
import android.util.Log
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ListaSpeseUtils {
    private val TAG = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child("liste")
    private lateinit var currentUser: FirebaseUser
    private var partecipanti : ArrayList<String> = ArrayList()
    fun creaListaSpese(binding: AddListaSpeseBinding) {
        val methodName = "creaListaSpese"
        Log.i(TAG, ">>$methodName")

        db.orderByChild("id").limitToLast(1).get().addOnSuccessListener {
            var newId = 1
            currentUser = DBUtils.getCurrentUser()!!
            partecipanti.add(currentUser.uid)//Aggiunge user id del partecipante

            if (it.exists()) {
                val id: Int = it.children.first().child("id").value.toString().toInt()
                newId = (id + 1)
            }

            //Nuova spesa
            val spesa = ListaSpese(
                newId,
                binding.listaSpeseNomeText.text.toString(),
                partecipanti
            )

            //Creo spesa
            db.child(newId.toString()).setValue(spesa)

            Log.i(TAG, "<<$methodName")
        }.addOnFailureListener {
            Log.e(TAG, "<<$methodName Error getting spesa", it)
        }
    }
    fun printSpese(binding: LoadSpeseBinding, context: Context, dataForQuery: DataForQuery) {}

    fun clearTextViewFocus(binding: AddListaSpeseBinding) {
        binding.listaSpeseNome.clearFocus()
    }

    fun deleteSpesa(spesa: Spesa) {}

    fun printTotaleSpese(binding: LoadSpeseBinding, context: Context, dataForQuery: DataForQuery): Double {
        return 0.0
    }



}