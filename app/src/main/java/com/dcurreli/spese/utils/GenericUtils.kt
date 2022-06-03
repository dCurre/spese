package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.enums.bundle.BundleArgumentEnum
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object GenericUtils {
    private val className = javaClass.simpleName

    fun clearTextViewFocus(binding: AddListaSpeseBinding) {
        binding.listaSpeseNome.clearFocus()
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateStringToTimestampSeconds(data: String): Long {
        return dateToTimestampSeconds(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data))
    }

    fun dateToTimestampSeconds(date: Date?): Long {
        return Timestamp(date!!).seconds
    }

    // Gestisco preferenze tema scuro/chiaro
    fun onOffDarkTheme(bool: Boolean) {
        AppCompatDelegate.setDefaultNightMode( if (bool) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun importoAsEur(double : Double): String {
        return "${String.format("%.2f", double)}€".replace(".", ",")
    }

    fun clearSottotitoloToolbar(activity: AppCompatActivity?){
        setupSottotitoloToolbar(null, activity)
    }

    private fun setupSottotitoloToolbar(message: String?, activity: AppCompatActivity?){
        (activity)?.supportActionBar?.subtitle = message
    }

    fun createBundleForListaSpese(idLista: String, nomeLista: String?, owner: String?): Bundle {
        //Utile per creare un bundle per load spese fragment
        val bundle = Bundle()
        bundle.putString(BundleArgumentEnum.EXPENSES_LIST_ID.value, idLista)
        bundle.putString(BundleArgumentEnum.EXPENSES_LIST_NAME.value, nomeLista)
        bundle.putString(BundleArgumentEnum.EXPENSES_LIST_OWNER.value, owner)
        return bundle
    }

    fun setupSwitch(@SuppressLint("UseSwitchCompatOrMaterialCode") switch: Switch, isActive: Boolean){
        switch.isChecked = isActive
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