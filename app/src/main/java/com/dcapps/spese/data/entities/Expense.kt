package com.dcapps.spese.data.entities

import android.util.Log
import com.dcapps.spese.enums.entity.ExpenseFieldsEnum
import com.dcapps.spese.utils.GenericUtils.importoAsEur
import com.google.firebase.firestore.DocumentSnapshot
import java.math.BigDecimal
import java.math.RoundingMode

data class Expense(
    val id: String,
    val expense: String,
    val amount: Double,
    val expenseDate: String,
    val expenseDateTimestamp: Long,
    val buyer : String,
    val expenseListID : String
        ) {

    private fun getFormattedAmount(): Double {
        val bd: BigDecimal = BigDecimal.valueOf(amount)
        return bd.setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun amountAsEur(): String {
        return importoAsEur(getFormattedAmount())
    }

    companion object {
        fun DocumentSnapshot.toExpense(): Expense? {
            return try {
                Expense(
                    id,
                    getString(ExpenseFieldsEnum.EXPENSE.value)!!,
                    getDouble(ExpenseFieldsEnum.AMOUNT.value)!!,
                    getString(ExpenseFieldsEnum.EXPENSE_DATE.value)!!,
                    getLong(ExpenseFieldsEnum.EXPENSE_DATE_TIMESTAMP.value)!!,
                    getString(ExpenseFieldsEnum.BUYER.value)!!,
                    getString(ExpenseFieldsEnum.EXPENSE_LIST_ID.value)!!
                )

            } catch (e: Exception) {
                Log.i(TAG, "${e.message}")
                null
            }
        }

            private const val TAG = "Expense"
        }
}