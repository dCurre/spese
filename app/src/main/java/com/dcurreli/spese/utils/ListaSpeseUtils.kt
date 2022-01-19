package com.dcurreli.spese.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.adapters.ListaSpeseAdapter
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.enum.CategoriaListaEnum
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
    private val TAG by lazy { javaClass.simpleName }
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value)
    private lateinit var listaSpese: ListaSpese
    private lateinit var currentUser: FirebaseUser
    private lateinit var listaSpesePeriodicaArray: ArrayList<ListaSpese>
    private lateinit var listaSpeseEventoArray: ArrayList<ListaSpese>
    @SuppressLint("StaticFieldLeak") private lateinit var listaPeriodicaAdapter: ListaSpeseAdapter
    @SuppressLint("StaticFieldLeak") private lateinit var listaEventoAdapter: ListaSpeseAdapter
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
            DBUtils.getCurrentUser()?.uid,
            binding.listaSpeseCategorieMenu.text.toString()
        )

        //Creo lista
        db.child(newKey).setValue(lista)

        Log.i(TAG, "<<$methodName")
    }

    fun joinListaSpese(binding: AddListaSpeseBinding) {
        val methodName = "creaListaSpese"
        Log.i(TAG, ">>$methodName")
        val newKey = db.push().key!!
        currentUser = DBUtils.getCurrentUser()!!
        partecipanti.add(currentUser.uid)//Aggiunge user id del partecipante

        binding.listaSpeseJoinText.text

        //var listaSpese = db.child((binding.listaSpeseJoinText.text.toString())

        Log.i(TAG, ">>join spesa ${binding.listaSpeseJoinText.text.toString().replace(" ", "")}")

        //Recupero il mese da cancellare
        db.orderByChild("id").equalTo(binding.listaSpeseJoinText.text.toString().replace(" ", "")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val listaSpese = it.children.first().getValue(ListaSpese::class.java) as ListaSpese

                    Log.i(TAG, ">>join spesa ${listaSpese.partecipanti}")

                    if(!listaSpese.partecipanti.contains(currentUser.uid)) {
                        listaSpese.partecipanti.add(currentUser.uid)
                        db.child(binding.listaSpeseJoinText.text.toString().replace(" ", "")).child("partecipanti").setValue(listaSpese.partecipanti)
                    }
                }else{
                    Log.i(TAG, ">>Non esiste una lista ")
                }
            }.addOnFailureListener {
                Log.e(TAG, "<< Error getting mese", it)
            }

        Log.i(TAG, "<<$methodName")
    }

    fun clearTextViewFocus(binding: AddListaSpeseBinding) {
        binding.listaSpeseNome.clearFocus()
        binding.listaSpeseCategorieMenu.clearFocus()
    }

    fun printListe(context: Context, binding: ActivityMainBinding, navController: NavController) {
        binding.listListaSpesePeriodiche.layoutManager = LinearLayoutManager(context)
        binding.listListaSpeseEvento.layoutManager = LinearLayoutManager(context)

        listaSpesePeriodicaArray = ArrayList()
        listaSpeseEventoArray = ArrayList()
        listaPeriodicaAdapter = ListaSpeseAdapter(context, listaSpesePeriodicaArray, binding, navController)
        listaEventoAdapter = ListaSpeseAdapter(context, listaSpeseEventoArray, binding, navController)

        binding.listListaSpesePeriodiche.adapter = listaPeriodicaAdapter
        binding.listListaSpeseEvento.adapter = listaEventoAdapter

        db.orderByChild("partecipanti").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpesePeriodicaArray.clear()
                listaSpeseEventoArray.clear()
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    listaSpese = snapshot.getValue(ListaSpese::class.java) as ListaSpese
                    if(!listaSpese.partecipanti.isNullOrEmpty() && listaSpese.partecipanti.contains(
                            DBUtils.getCurrentUser()?.uid
                        )){
                        //Smisto tra periodica o evento
                        if(listaSpese.categoria.equals(CategoriaListaEnum.PERIODICA.value, ignoreCase = true))
                            listaSpesePeriodicaArray.add(listaSpese)
                        else
                            listaSpeseEventoArray.add(listaSpese)
                    }
                }
                listaPeriodicaAdapter.notifyDataSetChanged() //Se tolgo non stampa
                listaEventoAdapter.notifyDataSetChanged() //Se tolgo non stampa
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
    }
}