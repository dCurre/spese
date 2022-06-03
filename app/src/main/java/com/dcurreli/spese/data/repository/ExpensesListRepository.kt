package com.dcurreli.spese.data.repository

import androidx.lifecycle.MutableLiveData
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.entity.ExpensesList.Companion.toExpensesList
import com.dcurreli.spese.enums.entity.ExpensesListFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import com.google.firebase.firestore.Query

class ExpensesListRepository {
    private val db = DBUtils.getFirestoreReference(TablesEnum.EXPENSES_LISTS)

    fun findAll(liveData: MutableLiveData<List<ExpensesList>>) {
        db.addSnapshotListener { value, _ ->
            liveData.postValue(value!!.documents.mapNotNull { it.toExpensesList() })
        }
    }

    fun findByID(id : String, liveData: MutableLiveData<ExpensesList>) {
        db.document(id).addSnapshotListener { value, _ ->
            liveData.postValue(value!!.toExpensesList())
        }
    }

    fun findAllByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean, liveData: MutableLiveData<List<ExpensesList>>) {
        val query =
            if(hidePaidLists)
                db.whereArrayContains(ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, userID)
                    .whereNotEqualTo(ExpensesListFieldEnum.PAID.value, hidePaidLists)
            else
                db.whereArrayContains(ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, userID)

        //Sorting
        query
            .orderBy(ExpensesListFieldEnum.PAID.value, Query.Direction.ASCENDING)
            .orderBy(ExpensesListFieldEnum.TIMESTAMP_INS.value, Query.Direction.DESCENDING)

        //Retrieving data
        query.addSnapshotListener { value, _ ->
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