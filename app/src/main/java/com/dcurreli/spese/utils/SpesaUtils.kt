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
import com.dcurreli.spese.adapters.DareAvereAdapter
import com.dcurreli.spese.adapters.SaldoAdapter
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.databinding.EditSpesaDialogBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.DareAvere
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

object SpesaUtils {
    private val className = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.SPESA.value)

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
        val spesaAdapter = SpesaAdapter(context, spesaArray)
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
        val spesaArray = ArrayList<Spesa>()
        val dareAvereArray = ArrayList<DareAvere>()
        val saldoAdapter = SaldoAdapter(context, spesaArray)
        val dareAvereAdapter = DareAvereAdapter(context, dareAvereArray)
        binding.listaSaldoTotale.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoDareAvere.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoTotale.adapter = saldoAdapter
        binding.listaSaldoDareAvere.adapter = dareAvereAdapter

        db.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                spesaArray.clear()
                dareAvereArray.clear()

                 //Ciclo per ottenere spese e totale
                 for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    var exists = false

                    for(i in 0 until spesaArray.size){
                        if(!exists && spesa.pagatore.equals(spesaArray[i].pagatore, true)){
                            spesaArray[i].importo += spesa.importo
                            exists = true
                        }
                    }
                    if(!exists)
                        spesaArray.add(spesa)
                }

                //Ciclo per il calcolo dare avere
                for (spesa in spesaArray) {
                    for (spesaTwo in spesaArray) {
                        if(spesa.pagatore != spesaTwo.pagatore){
                            dareAvereArray.add(DareAvere(spesa.pagatore, spesaTwo.pagatore, ((spesa.importo-spesaTwo.importo)/spesaArray.size)))
                        }
                    }
                }

                //Se ci sono spese non stampo la stringa d'errore, altrimenti la stampo
                if (dataSnapshot.childrenCount > 0){
                    binding.saldatoriNotFound.visibility = View.INVISIBLE
                    binding.titleSaldoTotale.visibility = View.VISIBLE
                    binding.titleDareAvere.visibility = View.VISIBLE

                    //Dare avere può essere vuoto se c'è un solo pagatore
                    if(dareAvereArray.size < 1)
                        binding.titleDareAvere.visibility = View.INVISIBLE

                }
                else {
                    binding.saldatoriNotFound.visibility = View.VISIBLE
                    binding.titleDareAvere.visibility = View.INVISIBLE
                    binding.titleSaldoTotale.visibility = View.INVISIBLE
                }
                dareAvereAdapter.notifyDataSetChanged()
                saldoAdapter.notifyDataSetChanged()
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

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun deleteSpesa(spesa: Spesa) {
        //TODO capire se mi serve cancellare il mese, non penso
        //val dataForQuery = MeseUtils.createDataForQueryFromSpesa(spesa)!!

        //In seguito cancello il mese
        /*db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble())
            .endBefore(dataForQuery.endsAt.toDouble())
            .get().addOnSuccessListener {
                Log.e(TAG, "<< Children count ${it.childrenCount}")
                if (it.exists() && it.childrenCount < 1) {
                    //Essendoci solo una spesa nel mese posso pure cancellare il mese stesso
                    MeseUtils.deleteMese(spesa)
                }
            }.addOnFailureListener {
                Log.e(TAG, "<< Error getting mese", it)
            }

         */
    }

}