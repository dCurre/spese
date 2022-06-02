package com.dcurreli.spese.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dcurreli.spese.data.entity.Spesa
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object SpesaUtils {
    private val className = javaClass.simpleName
    val db = Firebase.firestore.collection("Utenti")

    @RequiresApi(Build.VERSION_CODES.O)
    fun fireStoreCreaSpesa(binding: AddSpesaBinding, idLista : String) {
        val methodName = "creaSpesa"
        Log.i(className, ">>$methodName")

        val newKey = db.document().id

        //Nuova spesa
        val spesa = Spesa(
            newKey,
            binding.spesaSpesaText.text.toString().trim(),
            binding.spesaImporto.text.toString().trim().replace(",", ".").toDouble(),
            binding.spesaData.text.toString().trim(),
            binding.spesaPagatoreText.text.toString().trim(),
            idLista
        )

        db.document(newKey).set(spesa)

        Log.i(className, "<<$methodName")
    }

}