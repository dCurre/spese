package com.dcapps.spese.data.entity

import android.util.Log
import com.dcapps.spese.enums.entity.ExpensesListFieldsEnum
import com.google.firebase.firestore.DocumentSnapshot

data class ExpensesList(
    val id: String?,
    val name: String?,
    val partecipatingUsersID: ArrayList<String>?,
    val owner: String?,
    val paid: Boolean,
    val timestampIns: Long?
        ) {
    constructor() : this(null, null, null, null, false, null)

    companion object {
        fun DocumentSnapshot.toExpensesList(): ExpensesList? {
            return try {
                ExpensesList(
                    id,
                    getString(ExpensesListFieldsEnum.NAME.value)!!,
                    get(ExpensesListFieldsEnum.PARTECIPATING_USERS_ID.value) as ArrayList<String>,
                    getString(ExpensesListFieldsEnum.OWNER.value)!!,
                    getBoolean(ExpensesListFieldsEnum.PAID.value)!!,
                    getLong(ExpensesListFieldsEnum.TIMESTAMP_INS.value)!!
                )

            } catch (e: Exception) {
                Log.i(TAG, "${e.message}")
                null
            }
        }

        private const val TAG = "ExpensesList"
    }

}