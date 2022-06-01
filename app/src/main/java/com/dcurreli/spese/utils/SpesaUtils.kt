package com.dcurreli.spese.utils

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.Spesa
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object SpesaUtils {
    private val className = javaClass.simpleName
    private var dbSpesa = DBUtils.getDatabaseReference(TablesEnum.SPESA)
    val db = Firebase.firestore.collection("Utenti")

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSpesa(binding: AddSpesaBinding, idLista : String) {
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

        //Creo spesa
        dbSpesa.child(newKey).setValue(spesa)

        Log.i(className, "<<$methodName")
    }

    fun clearTextViewFocusAddSpesa(binding: AddSpesaBinding) {
        binding.spesaSpesaText.clearFocus()
        binding.spesaImporto.clearFocus()
        binding.spesaPagatoreText.clearFocus()
    }

    fun clearTextViewFocusEditSpesa(view: View) {
        val spesa = view.findViewById<TextInputEditText>(R.id.edit_spesa_text)
        val importo = view.findViewById<TextInputEditText>(R.id.edit_spesa_importo)
        val pagatore = view.findViewById<TextInputEditText>(R.id.edit_spesa_pagatore_text)

        spesa.clearFocus()
        importo.clearFocus()
        pagatore.clearFocus()
    }
}