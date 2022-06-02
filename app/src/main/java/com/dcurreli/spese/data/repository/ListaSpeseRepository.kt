package com.dcurreli.spese.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ListaSpeseRepository {
    private val db: DatabaseReference = DBUtils.getDatabaseReference(TablesEnum.LISTE)
    var listaSpeseList = ArrayList<ListaSpese>()

    fun findAll(liveData: MutableLiveData<List<ListaSpese>>) {

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpeseList.clear()

                for (snapshot in dataSnapshot.children) {
                    listaSpeseList.add(snapshot.getValue(ListaSpese::class.java) as ListaSpese)
                }
                liveData.postValue(listaSpeseList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun findByUserID(liveData: MutableLiveData<List<ListaSpese>>, user: User) {

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpeseList.clear()

                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val listaSpese = snapshot.getValue(ListaSpese::class.java) as ListaSpese

                    if(!listaSpese.partecipanti.isNullOrEmpty() && listaSpese.partecipanti.contains(user.user_id)){
                        //Se non nascondo liste saldate stampo tutto, altrimenti stampo solo quelle non saldate
                        if (!user.isNascondiListeSaldate || (user.isNascondiListeSaldate && !listaSpese.isSaldato)) {
                            listaSpeseList.add(listaSpese)
                        }
                    }
                }
                listaSpeseList = listaSpeseList.sortedBy { it.timestamp }.toCollection(ArrayList())
                listaSpeseList.reverse()
                liveData.postValue(listaSpeseList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun findById(liveData: MutableLiveData<ListaSpese>, id : String) {
        db.orderByChild("id").equalTo(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listaSpeseList.clear()

                try{
                    liveData.postValue(dataSnapshot.children.first().getValue(ListaSpese::class.java) as ListaSpese)
                } catch (e : Exception){
                    Log.e("getListaSpeseById", "${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun update(id: String, listaSpese : ListaSpese) {
        db.child(id).setValue(listaSpese)
    }

    fun updateByField(id: String, field : String, value : Any) {
        db.child(id).child(field).setValue(value)
    }

    fun insert(listaSpese : ListaSpese) {
        db.child(listaSpese.id).setValue(listaSpese)
    }

    fun delete(id : String) {
        db.child(id).removeValue()
    }

}