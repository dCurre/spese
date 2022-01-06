package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.objects.Spesa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object SpesaUtils {
    private val TAG = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child("spesa")

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSepsa(binding: AddSpesaBinding) {
        val methodName = "creaSpesa"
        Log.i(TAG, ">>$methodName")
        db.orderByChild("id").limitToLast(1).get().addOnSuccessListener {
            var newId = 1

            if(it.exists()) {
                val id: Int =  it.children.first().child("id").value.toString().toInt()
                newId = (id + 1)
            }
            //Nuova spesa
            val spesa = Spesa(
                newId,
                binding.spesaSpesaText.text.toString(),
                binding.spesaImporto.text.toString().replace(",",".").toDouble(),
                binding.spesaData.text.toString(),
                binding.spesaPagatoreText.text.toString()
            )

            //Creo mese, e se va bene
            MeseUtils.creaMese(spesa)
            //Creo spesa
            db.child(newId.toString()).setValue(spesa)

            Log.i(TAG, "<<$methodName")
        }.addOnFailureListener{
            Log.e(TAG, "<<$methodName Error getting spesa", it)
        }
    }

   fun printSpesa(recyclerView : RecyclerView, context : Context, dataForQuery: DataForQuery){
       recyclerView.layoutManager = LinearLayoutManager(context)
       val spesaArray = ArrayList<Spesa>()
       val spesaAdapter = SpesaAdapter(context, spesaArray)
       recyclerView.adapter = spesaAdapter

       db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble()).endBefore(dataForQuery.endsAt.toDouble()).addValueEventListener(object : ValueEventListener {
           @SuppressLint("NotifyDataSetChanged")
           override fun onDataChange(dataSnapshot: DataSnapshot){
               spesaArray.clear()
               for (snapshot : DataSnapshot in dataSnapshot.children){
                   val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                   spesaArray.add(spesa)
               }
               spesaAdapter.notifyDataSetChanged() //Refresh della lista
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
    fun deleteSpesa(spesa : Spesa) {
        var dataForQuery = MeseUtils.createDataForQueryFromSpesa(spesa)

        //Controllo prima che ci sia solo un elemento per quel mese --> poi cancello il mese e in seguito l'elemento
        db.orderByChild("timestamp").startAfter(dataForQuery!!.startsAt.toDouble()).endBefore(dataForQuery!!.endsAt.toDouble()).get().addOnSuccessListener {
                if(it.exists() && it.childrenCount <= 1){
                    MeseUtils.deleteMese(spesa)
            }
        }.addOnFailureListener{
            Log.e(TAG, "<< Error getting mese", it)
        }

        //Provvedo alla cancellazione della spesa
        db.child(spesa.id.toString()).removeValue()

    }

}