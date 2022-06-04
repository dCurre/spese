package com.dcapps.spese.data.entity

import android.util.Log
import com.dcapps.spese.enums.entity.ExpenseFieldEnum
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
                    getString(ExpenseFieldEnum.EXPENSE.value)!!,
                    getDouble(ExpenseFieldEnum.AMOUNT.value)!!,
                    getString(ExpenseFieldEnum.EXPENSE_DATE.value)!!,
                    getLong(ExpenseFieldEnum.EXPENSE_DATE_TIMESTAMP.value)!!,
                    getString(ExpenseFieldEnum.BUYER.value)!!,
                    getString(ExpenseFieldEnum.EXPENSE_LIST_ID.value)!!
                )

            } catch (e: Exception) {
                Log.i(TAG, "${e.message}")
                null
            }
        }

            private const val TAG = "Expense"
        }
}