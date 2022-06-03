package com.dcurreli.spese.data.repository

import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.data.entity.Expense
import com.dcurreli.spese.data.entity.Expense.Companion.toExpense
import com.dcurreli.spese.enums.entity.ExpenseFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils

class ExpenseRepository {
    private val db = DBUtils.getFirestoreReference(TablesEnum.EXPENSE)

    fun findAll(liveData: MutableLiveData<List<Expense>>) {
        db.addSnapshotListener { value, _ ->
            liveData.postValue(value!!.documents.mapNotNull { it.toExpense() })
        }
    }

    fun findAllByExpensesListID(id: String, liveData: MutableLiveData<List<Expense>>) {
        db.whereEqualTo(ExpenseFieldEnum.EXPENSE_LIST_ID.value, id).addSnapshotListener { value, e ->
            liveData.postValue(value!!.documents.mapNotNull { it.toExpense() })
        }
    }

    fun findByID(id : String, liveData: MutableLiveData<Expense>){
        db.document(id).addSnapshotListener { value, _ ->
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