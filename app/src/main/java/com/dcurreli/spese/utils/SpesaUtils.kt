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
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
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

    fun printSpese(
        binding: LoadSpeseTabSpeseBinding,
        context: Context,
        idListaSpese: String,
        activity: FragmentActivity?
    ) {
        val spesaArray = ArrayList<Spesa>()
        val spesaAdapter = SpesaAdapter(context, spesaArray)
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = spesaAdapter


        db.orderByChild("listaSpesaID").equalTo(idListaSpese).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totaleSpese : BigDecimal = BigDecimal.ZERO
                spesaArray.clear()

                //Ciclo per ottenere spese e totale
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    spesaArray.add(spesa)
                    totaleSpese = totaleSpese.add(spesa.importo.toBigDecimal())
                }

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

                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
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

        //Cancello la spesa
        db.child(spesa.id).removeValue()

        //TODO capire se mi serve cancellare il mese, non penso
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