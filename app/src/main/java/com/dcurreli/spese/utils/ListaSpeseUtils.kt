package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.util.Log
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object ListaSpeseUtils {
    private val className by lazy { javaClass.simpleName }
    private var dbListe = DBUtils.getDatabaseReference(TablesEnum.LISTE)
    private var currentUser: FirebaseUser = DBUtils.getLoggedUser()
    private var partecipanti : ArrayList<String> = ArrayList()

    fun clearTextViewFocus(binding: AddListaSpeseBinding) {
        binding.listaSpeseNome.clearFocus()
    }

    //Update di un campo, utile quando creo campi nuovi
    fun updateField(field: String, value: Long){
        dbListe.orderByChild("id").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    dbListe.child((snapshot.getValue(ListaSpese::class.java) as ListaSpese).id)
                        .child(field) //CAMPO DA AGGIORNARE
                        .setValue(value)//VALORE DA ASSEGNARE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
    }
}