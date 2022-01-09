package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.adapters.ListaSpeseAdapter
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.objects.ListaSpese
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ListaSpeseUtils {
    private val TAG = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference.child("liste")
    private lateinit var listaSpese: ListaSpese
    private lateinit var currentUser: FirebaseUser
    private lateinit var listaSpeseArray: java.util.ArrayList<ListaSpese>
    @SuppressLint("StaticFieldLeak") private lateinit var listaAdapter: ListaSpeseAdapter
    private var partecipanti : ArrayList<String> = ArrayList()
    fun creaListaSpese(binding: AddListaSpeseBinding) {
        val methodName = "creaListaSpese"
        Log.i(TAG, ">>$methodName")
        val newKey = db.push().key!!
        currentUser = DBUtils.getCurrentUser()!!
        partecipanti.add(currentUser.uid)//Aggiunge user id del partecipante

        //Nuova spesa
        val lista = ListaSpese(
            newKey,
            binding.listaSpeseNomeText.text.toString(),
            partecipanti,
            DBUtils.getCurrentUser()?.uid
        )

        //Creo lista
        db.child(newKey.toString()).setValue(lista)

        Log.i(TAG, "<<$methodName")
    }
    fun clearTextViewFocus(binding: AddListaSpeseBinding) {
        binding.listaSpeseNome.clearFocus()
    }

    fun printListe(context: Context, binding: ActivityMainBinding) {
        binding.listListaSpese.setHasFixedSize(true)
        binding.listListaSpese.layoutManager = LinearLayoutManager(context)

        listaSpeseArray = ArrayList()
        listaAdapter = ListaSpeseAdapter(context, listaSpeseArray)

        binding.listListaSpese.adapter = listaAdapter

        db.orderByChild("partecipanti").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpeseArray.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    listaSpese = snapshot.getValue(ListaSpese::class.java) as ListaSpese
                    if(!listaSpese.partecipanti.isNullOrEmpty() && listaSpese.partecipanti.contains(DBUtils.getCurrentUser()?.uid)){
                        listaSpeseArray.add(listaSpese)
                    }
                }
                listaAdapter.notifyDataSetChanged() //Se tolgo non stampa
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }


}