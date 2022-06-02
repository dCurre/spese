package com.dcurreli.spese.data.repository

import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.DBUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class SpesaRepository {
    private val db: DatabaseReference = DBUtils.getDatabaseReference(TablesEnum.SPESA)
    val spesaList = ArrayList<Spesa>()

    fun getAll(liveData: MutableLiveData<List<Spesa>>) {

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                spesaList.clear()

                for (snapshot in dataSnapshot.children) {
                    spesaList.add(snapshot.getValue(Spesa::class.java) as Spesa)
                }
                liveData.postValue(spesaList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun findByListaSpesaID(liveData: MutableLiveData<List<Spesa>>, id: String) {

        db.orderByChild("listaSpesaID").equalTo(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                spesaList.clear()

                for (snapshot in dataSnapshot.children) {
                    spesaList.add(snapshot.getValue(Spesa::class.java) as Spesa)
                }
                liveData.postValue(spesaList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun update(spesa : Spesa) {
        val spesaUpdated: MutableMap<String, Any> = HashMap()
        spesaUpdated["spesa"] = spesa.spesa.trim()
        spesaUpdated["importo"] = spesa.importo
        spesaUpdated["data"] = spesa.data
        spesaUpdated["pagatore"] = spesa.pagatore
        spesaUpdated["timestamp"] = spesa.timestamp
        db.child(spesa.id).updateChildren(spesaUpdated)
    }

    fun insert(spesa : Spesa) {
        db.child(spesa.id).setValue(spesa)
    }

    fun delete(id: String) {
        db.child(id).removeValue()
    }

    fun deleteList(spesaList: List<Spesa>) {
        for(spesa in spesaList){
            delete(spesa.id)
        }
    }
}