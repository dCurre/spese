package com.dcapps.spese.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dcapps.spese.data.entities.Expense
import com.dcapps.spese.data.entities.Expense.Companion.toExpense
import com.dcapps.spese.enums.entity.ExpenseFieldsEnum
import com.dcapps.spese.enums.table.TablesEnum
import com.dcapps.spese.utils.DBUtils
import com.google.firebase.firestore.Query

class ExpenseRepository {
    private val db = DBUtils.getFirestoreReference(TablesEnum.EXPENSE)
    private val TAG = "ExpenseRepository"

    fun findAll(liveData: MutableLiveData<List<Expense>>) {
        db.addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.documents.mapNotNull { it.toExpense() })
        }
    }

    fun findAllByExpensesListID(id: String, liveData: MutableLiveData<List<Expense>>) {
        db.whereEqualTo(ExpenseFieldsEnum.EXPENSE_LIST_ID.value, id)
            .orderBy(ExpenseFieldsEnum.EXPENSE_DATE_TIMESTAMP.value, Query.Direction.ASCENDING)
            .addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.documents.mapNotNull { it.toExpense() })
        }
    }

    fun findByID(id : String, liveData: MutableLiveData<Expense>){
        db.document(id).addSnapshotListener { value, e ->
            if (e != null){
                Log.e(TAG, "Error in findByID, ${e.message}")
                return@addSnapshotListener
            }
            liveData.postValue(value!!.toExpense())
        }
    }

    fun insert(expense : Expense) {
        db.document(expense.id).set(expense)
    }

    fun update(id: String, updateMap: HashMap<String, Any>) {
        db.document(id).update(updateMap)
    }

    fun delete(id: String) {
        db.document(id).delete()
    }

    fun deleteList(expenseList: List<Expense>) {
        for(expense in expenseList){
            delete(expense.id)
        }
    }
}