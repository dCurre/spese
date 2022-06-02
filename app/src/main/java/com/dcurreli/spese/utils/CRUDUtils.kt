package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.util.Log
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.data.entity.Spesa
import com.dcurreli.spese.enum.TablesEnum
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object CRUDUtils {
    private val className = javaClass.simpleName
    private var db: DatabaseReference = Firebase.database.reference
    private var dbListe = DBUtils.getDatabaseReference(TablesEnum.LISTE)

    fun insert(table : String) {
        //Creo il campo nuovo
        db.child(table).orderByChild("id").addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Ciclo per ottenere spese e totale
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    when(table){
                        TablesEnum.LISTE.value -> {
                            val listaSpese = snapshot.getValue(ListaSpese::class.java) as ListaSpese
                            listaSpese.isSaldato = false
                            db.child(TablesEnum.LISTE.value).child(listaSpese.id).setValue(listaSpese)
                        }
                        TablesEnum.SPESA.value -> {
                            val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                            db.child(TablesEnum.LISTE.value).child(spesa.id).setValue(spesa)
                        }
                        TablesEnum.UTENTE.value -> {}
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
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