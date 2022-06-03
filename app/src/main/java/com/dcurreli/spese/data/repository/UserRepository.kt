package com.dcurreli.spese.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.entity.User.Companion.toUser
import com.dcurreli.spese.enums.entity.UserFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils

class UserRepository {
    private val db = DBUtils.getFirestoreReference(TablesEnum.USER)
    private val TAG = "UserRepository"

    fun findAll(liveData: MutableLiveData<List<User>>) {
        db.addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.documents.mapNotNull { it.toUser() })
        }
    }

    fun findByID(id: String, liveData: MutableLiveData<User>) {
        db.document(id).addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.toUser())
        }
    }

    fun findAllByIdList(idList: List<String>, liveData: MutableLiveData<List<User>>) {
        db.whereIn(UserFieldEnum.ID.value, idList).addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.documents.mapNotNull { it.toUser() })
        }
    }

    fun insert(user: User) {
        db.document(user.id).set(user)
    }

    fun update(id: String, updateMap: HashMap<String, Any>) {
        db.document(id).update(updateMap)
    }

    fun updateByField(id: String, field : String, value : Any) {
        db.document(id).update(field, value)
    }

    fun delete(id : String) {
        db.document(id).delete()
    }
}