package com.dcurreli.spese.data.entity

import android.util.Log
import com.dcurreli.spese.enums.entity.ExpensesListFieldEnum
import com.google.firebase.firestore.DocumentSnapshot

data class ExpensesList(
    val id: String?,
    val name: String?,
    val partecipatingUsersID: ArrayList<String>?,
    val owner: String?,
    val paid: Boolean,
    val timestampIns: Long?
        ) {

    companion object {
        fun DocumentSnapshot.toExpensesList(): ExpensesList? {
            return try {
                ExpensesList(
                    id,
                    getString(ExpensesListFieldEnum.NAME.value)!!,
                    get(ExpensesListFieldEnum.PARTECIPATING_USERS_ID.value) as ArrayList<String>,
                    getString(ExpensesListFieldEnum.OWNER.value)!!,
                    getBoolean(ExpensesListFieldEnum.PAID.value)!!,
                    getLong(ExpensesListFieldEnum.TIMESTAMP_INS.value)!!
                )

            } catch (e: Exception) {
                Log.i(TAG, "${e.message}")
                null
            }
        }

        private const val TAG = "ExpensesList"
    }

}