package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.adapters.SaldoCategoryAdapter
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.databinding.EditSpesaDialogBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.SaldoCategory
import com.dcurreli.spese.objects.SaldoSubItem
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.math.BigDecimal

object SpesaUtils {
    private val className = javaClass.simpleName
    private var db = DBUtils.getDatabaseReference(TablesEnum.SPESA)

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSepsa(binding: AddSpesaBinding, idLista : String) {
        val methodName = "creaSpesa"
        Log.i(className, ">>$methodName")
        val newKey = db.push().key!!

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
        db.child(newKey).setValue(spesa)

        Log.i(className, "<<$methodName")
    }

    fun printSpese(
        binding: LoadSpeseTabSpeseBinding,
        context: Context,
        idListaSpese: String,
        activity: FragmentActivity?
    ): SpesaAdapter {
        val spesaArray = ArrayList<Spesa>()
        val arrayTemp = ArrayList<Spesa>()
        val spesaAdapter = SpesaAdapter(spesaArray)
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = spesaAdapter

        db.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
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

                //Aggiorno il sottotitolo della toolbar
                GenericUtils.setupSottotitoloToolbar("Totale: ${totaleSpese.setScale(2).toString().replace(".",",")}€", (activity as AppCompatActivity?))

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
        binding.listaSaldoDareAvere2.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoDareAvere2.adapter = dareAvereAdapter

        db.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
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
                    mapSaldo.forEach { entry ->
                        dareAvereArray.add(
                            SaldoCategory(
                                entry.key,
                                mapSaldo.filterKeys { it.contains(entry.key) }.values.sum(),
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

    fun clearTextViewFocusEditSpesa(binding: EditSpesaDialogBinding) {
        binding.spesaSpesaText.clearFocus()
        binding.spesaImporto.clearFocus()
        binding.spesaPagatoreText.clearFocus()
    }
}