package com.dcurreli.spese.utils

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

object SpesaUtils {
    private val TAG = javaClass.simpleName
    private lateinit var spesa : Spesa
    private lateinit var spesaAdapter : SpesaAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    fun creaSepsa(db: DatabaseReference, binding: AddSpesaBinding) {
        val methodName: String = "creaSpesa"
        Log.i(TAG, ">>$methodName")
        db.child("spesa").orderByChild("id").limitToLast(1).get().addOnSuccessListener {
            var newId: Int = 1

            if(it.exists()) {
                var id: Int =  it.children.first().child("id").value.toString().toInt()
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
            MeseUtils.creaMese(db, spesa)
            //Creo spesa
            db.child("spesa").child(newId.toString()).setValue(spesa)

            Log.i(TAG, "<<$methodName")
        }.addOnFailureListener{
            Log.e(TAG, "<<$methodName Error getting spesa", it)
        }
    }

   fun printSpesa(db : DatabaseReference, recyclerView : RecyclerView, context : Context, spesaArray : ArrayList<Spesa>, dataForQuery: DataForQuery){
       recyclerView.setHasFixedSize(true)
       recyclerView.layoutManager = LinearLayoutManager(context)
       spesaAdapter = SpesaAdapter(context, spesaArray)

       recyclerView.adapter = spesaAdapter

       db.orderByChild("timestamp").startAfter(dataForQuery.startsAt.toDouble()).endBefore(dataForQuery.endsAt.toDouble()).addValueEventListener(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot){
               for (snapshot : DataSnapshot in dataSnapshot.children){
                   spesa = snapshot.getValue(Spesa::class.java) as Spesa
                   spesaArray.add(spesa)
               }
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

}