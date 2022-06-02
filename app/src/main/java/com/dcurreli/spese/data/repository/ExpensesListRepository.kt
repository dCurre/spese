package com.dcurreli.spese.data.repository

import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.entity.ExpensesList.Companion.toExpensesList
import com.dcurreli.spese.enums.entity.ExpensesListFieldEnum
import com.dcurreli.spese.enums.table.TablesEnum
import com.dcurreli.spese.utils.DBUtils
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ExpensesListRepository {
    private val db = DBUtils.getDatabaseReferenceFirestore(TablesEnum.EXPENSES_LISTS)

    suspend fun findAll(): List<ExpensesList> {
        return db.get().await().documents.mapNotNull { it.toExpensesList() }
    }

    suspend fun findByID(id : String): ExpensesList? {
        return db.document(id).get().await().toExpensesList()
    }

    suspend fun findByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean): List<ExpensesList> {
        val query =
            if(hidePaidLists)
                db.whereArrayContains(ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, userID)
                  .whereNotEqualTo(ExpensesListFieldEnum.PAID.value, hidePaidLists)
            else
                db.whereArrayContains(ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value, userID)

        return query
            .orderBy(ExpensesListFieldEnum.PAID.value, Query.Direction.ASCENDING)
            .orderBy(ExpensesListFieldEnum.TIMESTAMP_INS.value, Query.Direction.DESCENDING)
            .get().await().documents.mapNotNull { it.toExpensesList() }
    }

    fun insert(expensesList : ExpensesList) {
        db.document(expensesList.id!!).set(expensesList)
    }

    fun update(id: String, expensesList : ExpensesList): ExpensesList {
        db.document(id).set(expensesList)
        return expensesList
    }

    suspend fun updateByField(id: String, field : String, value : Any): ExpensesList? {
        db.document(id).update(field, value)
        return findByID(id)
    }

    fun delete(id : String) {
        db.document(id).delete()
    }

}