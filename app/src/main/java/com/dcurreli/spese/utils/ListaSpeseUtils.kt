package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.navigation.NavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object ListaSpeseUtils {
    private val className by lazy { javaClass.simpleName }
    private var dbListe = DBUtils.getDatabaseReference(TablesEnum.LISTE)
    private var currentUser: FirebaseUser = DBUtils.getLoggedUser()!!
    private var partecipanti : ArrayList<String> = ArrayList()

    fun joinListaSpese(
        idLista: String,
        binding: JoinFragmentBinding,
        navController: NavController
    ) {
        val methodName = "creaListaSpese"
        Log.i(className, ">>$methodName")
        partecipanti.add(currentUser.uid)//Aggiunge user id del partecipante

        //Recupero il mese da cancellare
        dbListe.orderByChild("id").equalTo(idLista.replace(" ", "")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val listaSpese = it.children.first().getValue(ListaSpese::class.java) as ListaSpese

                    //Se l'utente non è già presente in lista
                    if(!listaSpese.partecipanti.contains(currentUser.uid) ){
                        //Se non ho già raggiunto il numero massimo di utenti
                        if(listaSpese.partecipanti.size < binding.counterMaxUsers.text.toString().toInt()) {
                            listaSpese.partecipanti.add(currentUser.uid)
                            dbListe.child(idLista.replace(" ", "")).child("partecipanti").setValue(listaSpese.partecipanti)
                            SnackbarUtils.showSnackbarOK("Ti sei aggiunto alla lista : )", binding.root)

                            navController.navigate(R.id.loadSpeseFragment, GenericUtils.createBundleForListaSpese(listaSpese.id, listaSpese.nome))
                        }else{//Se ho raggiunto il massimo di utenti
                            SnackbarUtils.showSnackbarError("Numero massimo di utenti raggiunto!", binding.root)
                        }
                    }else{//Se l'utente è già presente in lista
                        SnackbarUtils.showSnackbarError("Fai già parte della lista!", binding.root)
                    }
                }else{
                    Log.i(className, ">>Non esiste una lista ")
                }
            }.addOnFailureListener {
                Log.e(className, "<< Error getting mese", it)
            }

        Log.i(className, "<<$methodName")
    }

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