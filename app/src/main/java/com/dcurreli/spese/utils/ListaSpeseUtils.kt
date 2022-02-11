package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.ListaSpeseAdapter
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.ListaSpese
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object ListaSpeseUtils {
    private val className by lazy { javaClass.simpleName }
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value)
    private lateinit var listaSpese: ListaSpese
    private lateinit var currentUser: FirebaseUser
    private var partecipanti : ArrayList<String> = ArrayList()

    fun creaListaSpese(binding: AddListaSpeseBinding) {
        val methodName = "creaListaSpese"
        Log.i(className, ">>$methodName")
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
        db.child(newKey).setValue(lista)

        Log.i(className, "<<$methodName")
    }

    fun joinListaSpese(
        idLista: String,
        binding: JoinFragmentBinding,
        navController: NavController
    ) {
        val methodName = "creaListaSpese"
        Log.i(className, ">>$methodName")
        currentUser = DBUtils.getCurrentUser()!!
        partecipanti.add(currentUser.uid)//Aggiunge user id del partecipante

        //Recupero il mese da cancellare
        db.orderByChild("id").equalTo(idLista.replace(" ", "")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val listaSpese = it.children.first().getValue(ListaSpese::class.java) as ListaSpese

                    //Se l'utente non è già presente in lista
                    if(!listaSpese.partecipanti.contains(currentUser.uid) ){
                        //Se non ho già raggiunto il numero massimo di utenti
                        if(listaSpese.partecipanti.size < binding.counterMaxUsers.text.toString().toInt()) {
                            listaSpese.partecipanti.add(currentUser.uid)
                            db.child(idLista.replace(" ", "")).child("partecipanti").setValue(listaSpese.partecipanti)
                            GenericUtils.showSnackbarOK("Ti sei aggiunto alla lista : )", binding.root)

                            navController.navigate(R.id.loadSpeseFragment, GenericUtils.createBundleForListaSpese(listaSpese.id, listaSpese.nome))
                        }else{//Se ho raggiunto il massimo di utenti
                            GenericUtils.showSnackbarError("Numero massimo di utenti raggiunto!", binding.root)
                        }
                    }else{//Se l'utente è già presente in lista
                        GenericUtils.showSnackbarError("Fai già parte della lista!", binding.root)
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

    fun printListe(context: Context, binding: HomeFragmentBinding, navController: NavController) {
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.apply {
            layoutManager = GridLayoutManager(context, 3)
        }

        val listaSpeseArray = ArrayList<ListaSpese>()
        val listaSpeseAdapter = ListaSpeseAdapter(context, listaSpeseArray, binding, navController)

        binding.listaSpese.adapter = listaSpeseAdapter

        db.orderByChild("partecipanti").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpeseArray.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    listaSpese = snapshot.getValue(ListaSpese::class.java) as ListaSpese
                    if(!listaSpese.partecipanti.isNullOrEmpty() && listaSpese.partecipanti.contains(DBUtils.getCurrentUser()?.uid)){
                        listaSpeseArray.add(listaSpese)
                    }
                }
                listaSpeseAdapter.notifyDataSetChanged() //Se tolgo non stampa
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
    }


}