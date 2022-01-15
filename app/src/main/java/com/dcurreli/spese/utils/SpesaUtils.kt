package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

object SpesaUtils {
    private val TAG = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.SPESA.value)

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSepsa(binding: AddSpesaBinding, idLista : String) {
        val methodName = "creaSpesa"
        Log.i(TAG, ">>$methodName")
        val newKey = db.push().key!!

        //Nuova spesa
        val spesa = Spesa(
            newKey,
            binding.spesaSpesaText.text.toString(),
            binding.spesaImporto.text.toString().replace(",", ".").toDouble(),
            binding.spesaData.text.toString(),
            binding.spesaPagatoreText.text.toString(),
            idLista
        )

        //Creo mese, e se va bene
        MeseUtils.creaMese(spesa)
        //Creo spesa
        db.child(newKey).setValue(spesa)

        Log.i(TAG, "<<$methodName")
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun printSpese(binding: LoadSpeseBinding, context: Context, idListaSpese : String) {
        val spesaArray = ArrayList<Spesa>()
        val spesaAdapter = SpesaAdapter(context, spesaArray)
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = spesaAdapter

        db.orderByChild("listaSpesaID").equalTo(idListaSpese).get().addOnSuccessListener {

                //Se non ci sono spese evito di stampare una finestra vuota
                if (it.exists() && it.childrenCount > 0) {
                    var totaleSpese : BigDecimal = BigDecimal.ZERO
                    binding.speseNotFound.visibility = INVISIBLE

                    for( singleIt in it.children){
                        val spesa = singleIt.getValue(Spesa::class.java) as Spesa
                        spesaArray.add(spesa)
                        totaleSpese = totaleSpese.add(spesa.importo.toBigDecimal())
                    }

                    //Stampo pure il totale delle spese
                    binding.listaSpeseHeaderTotaleImporto.text = "${totaleSpese.setScale(2).toString().replace(".",",")}€"
                    spesaAdapter.notifyDataSetChanged() //Refresh della lista

                }else{
                    binding.speseNotFound.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                Log.e(TAG, "<< Error getting mese", it)
            }

               /*
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    spesaArray.clear()
                    var totaleSpese : BigDecimal = BigDecimal.ZERO

                    //Se non ci sono spese evito di stampare una finestra vuota
                    if (dataSnapshot.childrenCount > 0) binding.speseNotFound.visibility =
                        INVISIBLE else binding.speseNotFound.visibility = View.VISIBLE

                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                        spesaArray.add(spesa)
                        totaleSpese = totaleSpese.add(spesa.importo.toBigDecimal())
                    }
                    //Stampo pure il totale delle spese
                    binding.listaSpeseHeaderTotaleImporto.text = "${totaleSpese.setScale(2).toString().replace(".",",")}€"
                    spesaAdapter.notifyDataSetChanged() //Refresh della lista
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }

                 */
    }

    fun clearTextViewFocus(binding: AddSpesaBinding) {
        binding.spesaSpesaText.clearFocus()
        binding.spesaImporto.clearFocus()
        binding.spesaPagatoreText.clearFocus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun deleteSpesa(spesa: Spesa) {
        val dataForQuery = MeseUtils.createDataForQueryFromSpesa(spesa)!!

        //Controllo prima che ci sia solo un elemento per quel mese
        db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble())
            .endBefore(dataForQuery.endsAt.toDouble())
            .get().addOnSuccessListener {
                if (it.exists() && it.childrenCount <= 1) {
                    //Essendoci solo una spesa nel mese posso pure cancellare il mese stesso
                    MeseUtils.deleteMese(spesa)
                }
            }.addOnFailureListener {
                Log.e(TAG, "<< Error getting mese", it)
            }

        //Cancello la spesa
        db.child(spesa.id).removeValue()
    }

    fun printTotaleSpese(binding: LoadSpeseBinding, context: Context, dataForQuery: DataForQuery): Double {
        var totale : Double = 0.0
        val spesaArray = ArrayList<Spesa>()
        val spesaAdapter = SpesaAdapter(context, spesaArray)
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = spesaAdapter

        db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble())
            .endBefore(dataForQuery.endsAt.toDouble())
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    spesaArray.clear()

                    //Se ci sono spese non stampo la stringa d'errore, altrimenti la stampo
                    if (dataSnapshot.childrenCount > 0) binding.speseNotFound.visibility =
                        INVISIBLE else binding.speseNotFound.visibility = View.VISIBLE

                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                        spesaArray.add(spesa)
                    }
                    spesaAdapter.notifyDataSetChanged() //Refresh della lista
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            })

        return totale
    }

}