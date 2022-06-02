package com.dcurreli.spese.data.repository

import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.entity.User.Companion.toUser
import com.dcurreli.spese.enums.entity.UserFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = DBUtils.getDatabaseReferenceFirestore(TablesEnum.USER)

    suspend fun findAll(): List<User> {
        return db.get().await().documents.mapNotNull { it.toUser() }
    }

    suspend fun findByID(id: String): User? {
        return db.document(id).get().await().toUser()
    }

    suspend fun findAllByIdList(idList: List<String>): List<User> {
        return db.whereIn(UserFieldEnum.ID.value, idList).get().await().documents.mapNotNull { it.toUser() }
    }

    fun insert(user: User) {
        db.document(user.id).set(user)
    }

    fun update(id: String, user : User): User {
        db.document(id).set(user)
        return user
    }

    suspend fun updateByField(id: String, field : String, value : Any): User? {
        db.document(id).update(field, value)
        return findByID(id)
    }


}