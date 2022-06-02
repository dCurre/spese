package com.dcurreli.spese.data.repository

import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.entity.User.Companion.toUser
import com.dcurreli.spese.enums.entity.UserFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = DBUtils.getDatabaseReferenceFirestore(TablesEnum.USER)

    suspend fun getAll(): List<User> {
        return db.get().await().documents.mapNotNull { it.toUser() }
    }

    suspend fun getUserById(uid: String): User? {
        return db.document(uid).get().await().toUser()
    }

    suspend fun getUserListByIdList(uidList: List<String>): List<User> {
        return db.whereEqualTo(UserFieldEnum.ID.value, uidList).get().await().documents.mapNotNull { it.toUser() }
/*
        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()

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
        */
    }

    fun insert(user: User) {
        db.document(user.id).set(user)
    }

    fun update(id: String, user : User) {
        db.document(id).set(user)
    }

    fun updateByField(id: String, field : String, value : Any) {
        db.document(id).update(field, value)
    }


}