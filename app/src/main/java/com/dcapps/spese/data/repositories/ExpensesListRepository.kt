package com.dcapps.spese.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dcapps.spese.data.entities.ExpensesList
import com.dcapps.spese.data.entities.ExpensesList.Companion.toExpensesList
import com.dcapps.spese.enums.entity.ExpensesListFieldsEnum
import com.dcapps.spese.enums.table.TablesEnum
import com.dcapps.spese.utils.DBUtils
import com.google.firebase.firestore.Query

class ExpensesListRepository {
    private val db = DBUtils.getFirestoreReference(TablesEnum.EXPENSES_LISTS)
    private val TAG = "ExpensesListRepository"

    fun findAll(liveData: MutableLiveData<List<ExpensesList>>) {
        db.addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findAll, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.documents.mapNotNull { it.toExpensesList() })
        }
    }

    fun findByID(id : String, liveData: MutableLiveData<ExpensesList>) {
        db.document(id).addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.toExpensesList())
        }
    }

    fun findAllByUserID(userID: String, liveData: MutableLiveData<List<ExpensesList>>) {
        db.whereArrayContains(ExpensesListFieldsEnum.PARTECIPANTS.value, userID)
            .orderBy(ExpensesListFieldsEnum.TIMESTAMP_INS.value, Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null){
                    Log.e(TAG, "Error in findAllByUserIDAndIsPaid, ${e.message}")
                    return@addSnapshotListener
                }
                liveData.postValue(value!!.documents.mapNotNull { it.toExpensesList() })
            }
    }

    fun findAllByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean, liveData: MutableLiveData<List<ExpensesList>>) {
        val query =
            if(hidePaidLists)
                db.whereArrayContains(ExpensesListFieldsEnum.PARTECIPANTS.value, userID)
                    .whereNotEqualTo(ExpensesListFieldsEnum.PAID.value, hidePaidLists)
            else
                db.whereArrayContains(ExpensesListFieldsEnum.PARTECIPANTS.value, userID)

        //Sorting and retrieving data
        query.orderBy(ExpensesListFieldsEnum.PAID.value, Query.Direction.ASCENDING)
            .orderBy(ExpensesListFieldsEnum.TIMESTAMP_INS.value, Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->
                if (e != null){
                    Log.e(TAG, "Error in findAllByUserIDAndIsPaid, ${e.message}")
                    return@addSnapshotListener
                }
                liveData.postValue(value!!.documents.mapNotNull { it.toExpensesList() })
        }
    }

    fun insert(expensesList : ExpensesList) {
        db.document(expensesList.id!!).set(expensesList)
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