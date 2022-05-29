package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.SaldoCategoryAdapter
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.objects.SaldoCategory
import com.dcurreli.spese.objects.SaldoSubItem
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.GenericUtils.dateStringToTimestampSeconds
import com.dcurreli.spese.utils.GenericUtils.importoAsEur
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal


object SpesaUtils {
    private val className = javaClass.simpleName
    private var dbSpesa = DBUtils.getDatabaseReference(TablesEnum.SPESA)
    private var dbListaSpese = DBUtils.getDatabaseReference(TablesEnum.LISTE)

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSpesa(binding: AddSpesaBinding, idLista : String) {
        val methodName = "creaSpesa"
        Log.i(className, ">>$methodName")
        val newKey = dbSpesa.push().key!!

        //Nuova spesa
        val spesa = Spesa(
            newKey,
            binding.spesaSpesaText.text.toString().trim(),
            binding.spesaImporto.text.toString().trim().replace(",", ".").toDouble(),
            binding.spesaData.text.toString().trim(),
            binding.spesaPagatoreText.text.toString().trim(),
            idLista
        )

        //Creo spesa
        dbSpesa.child(newKey).setValue(spesa)

        Log.i(className, "<<$methodName")
    }

    fun updateSpesa(id: String, newSpesa : String, newImporto : String, newData : String, newPagatore : String) {
        val hopperRef: DatabaseReference = dbSpesa.child(id)
        val spesaUpdate: MutableMap<String, Any> = HashMap()
        spesaUpdate["spesa"] = newSpesa.trim()
        spesaUpdate["importo"] = newImporto.trim().replace(",", ".").toDouble()
        spesaUpdate["data"] = newData.trim()
        spesaUpdate["pagatore"] = newPagatore.trim()
        spesaUpdate["timestamp"] = dateStringToTimestampSeconds(newData, "dd/MM/yyyy")
        hopperRef.updateChildren(spesaUpdate)
    }

    fun printSpese(
        binding: LoadSpeseTabSpeseBinding,
        context: Context,
        idListaSpese: String,
    ): SpesaAdapter {
        val spesaArray = ArrayList<Spesa>()
        val arrayTemp = ArrayList<Spesa>()
        val spesaAdapter = SpesaAdapter(spesaArray)
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = spesaAdapter

        dbSpesa.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totaleSpese : BigDecimal = BigDecimal.ZERO
                spesaArray.clear()
                arrayTemp.clear()

                //Ciclo per ottenere spese e totale
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    arrayTemp.add(spesa)
                    totaleSpese = totaleSpese.add(spesa.importo.toBigDecimal())
                }

                //Riordino la lista temporanea e la carico sulla lista da stampare
                spesaArray.addAll(arrayTemp.sortedBy { it.timestamp }.toCollection(ArrayList()))
                //spesaArray.reverse()

                //Se ci sono spese non stampo la stringa d'errore, altrimenti la stampo
                if (dataSnapshot.childrenCount > 0) {
                    binding.speseNotFound.visibility = View.INVISIBLE
                }
                else
                    binding.speseNotFound.visibility = View.VISIBLE

                spesaAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })

        return spesaAdapter
    }

    fun printSaldo(
        binding: LoadSpeseTabSaldoBinding,
        context: Context,
        idListaSpese: String
    ) {

        val mapSaldo = mutableMapOf<String, Double>()
        val dareAvereArray = ArrayList<SaldoCategory>()
        val dareAvereSUBITEMSArray = ArrayList<SaldoSubItem>()
        val dareAvereAdapter = SaldoCategoryAdapter(context, dareAvereArray)
        binding.listaSaldoCategory.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoCategory.adapter = dareAvereAdapter

        dbSpesa.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dareAvereArray.clear()
                dareAvereSUBITEMSArray.clear()

                if (dataSnapshot.childrenCount <= 0){
                    binding.saldoNotPrintable.visibility = View.VISIBLE //MOSTRO LO SFONDO D'ERRORE
                    binding.totaleListaSpese.visibility = View.INVISIBLE //NASCONDO SCRITTA TOTALE
                }
                else {
                    binding.saldoNotPrintable.visibility = View.INVISIBLE //NASCONDO LO SFONDO D'ERRORE
                    binding.totaleListaSpese.visibility = View.VISIBLE //MOSTRO SCRITTA TOTALE

                    //Riempio una mappa di <Pagatore, ImportiPagati>
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val spesa = snapshot.getValue(Spesa::class.java) as Spesa

                        //Se la mappa non contiene il pagatore lo aggiungo, altrimenti sommo all'importo che già aveva
                        if(mapSaldo.containsKey(spesa.pagatore)) {
                            mapSaldo[spesa.pagatore] = spesa.importo + mapSaldo[spesa.pagatore]!!
                        } else {
                            mapSaldo[spesa.pagatore] = spesa.importo
                        }
                    }

                    //AGGIORNO IL TOTALE
                    dbListaSpese.child(idListaSpese).get().addOnSuccessListener {
                        val listaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                        setupTotale(binding.totaleListaSpese, mapSaldo.values.sum(),listaSpese.isSaldato, context)
                    }

                    /* Riempio la lista di subitem e calcolo l'importo corretto dovuto
                        FORMULA -> (A - B) / C

                        A -> Spesa ricevente
                        B -> Spesa pagatore
                        C -> Size mappa, ovvero tutti gli utenti che hanno partecipato alle spese*/
                    mapSaldo.forEach { pagatore ->
                        mapSaldo.filterKeys { it != pagatore.key }.forEach { ricevente ->
                            dareAvereSUBITEMSArray.add(
                                SaldoSubItem(
                                    pagatore.key,
                                    ricevente.key,
                                    //(mapSaldo.filterKeys { !it.contains(entry.key) }.values.sum() - entry.value) / mapSaldo.size
                                    (ricevente.value - pagatore.value) / mapSaldo.size
                                )
                            )}
                        }

                    /* Scorro nuovamente la mappa, per ogni pagatore aggiungo all'array di DareAvere:
                        1. Pagatore
                        2. Somma pagata dal pagatore
                        3. Lista di debiti con altri utenti (filtrata dalla mappa per chiave pagatore) */
                    mapSaldo.toSortedMap().forEach { entry ->
                        dareAvereArray.add(
                            SaldoCategory(
                                entry.key,
                                entry.value,
                                dareAvereSUBITEMSArray.filter { it.pagatore.equals(entry.key, ignoreCase = true) } as java.util.ArrayList<SaldoSubItem>?
                            )
                        )
                    }
                }

                dareAvereAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
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

    private fun setupTotale(totaleListaSpese: TextView, importoTotale: Double, isSaldato: Boolean, context: Context) {
        if(isSaldato){
            totaleListaSpese.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        } else {
            totaleListaSpese.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        }

        totaleListaSpese.text = importoAsEur(importoTotale)
    }
}