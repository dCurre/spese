package com.dcurreli.spese.data.repository

import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class UtenteRepository {
    private val db: DatabaseReference = DBUtils.getDatabaseReference(TablesEnum.UTENTE)
    val userList = ArrayList<User>()

    fun getAll(liveData: MutableLiveData<List<User>>) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    userList.add(snapshot.getValue(User::class.java) as User)
                }
                liveData.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun getUserById(liveData: MutableLiveData<User>, uid: String) {
        db.orderByChild("user_id").equalTo(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.children.first().getValue(User::class.java) as User
                liveData.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

    fun update(id: String, user : User) {
        db.child(id).setValue(user)
    }

    fun getUserListByIdList(liveData: MutableLiveData<List<User>>, uidList: List<String>) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java) as User
                    if(uidList.contains(user.user_id)){
                        userList.add(user)
                    }
                }
                liveData.postValue(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Nothing to do
            }
        })
    }

}