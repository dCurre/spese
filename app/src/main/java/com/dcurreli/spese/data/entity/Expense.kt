package com.dcurreli.spese.data.entity

import android.util.Log
import com.dcurreli.spese.enums.entity.ExpenseFieldEnum
import com.dcurreli.spese.utils.GenericUtils.dateStringToTimestampSeconds
import com.dcurreli.spese.utils.GenericUtils.importoAsEur
import com.google.firebase.firestore.DocumentSnapshot
import java.math.BigDecimal
import java.math.RoundingMode

data class Expense(
    val id: String,
    val expense: String,
    private val amount: Double,
    val expenseDate: String,
    val expenseDateTimestamp: Long = dateStringToTimestampSeconds(expenseDate, "dd/MM/yyyy"),
    val buyer : String,
    val expenseListID : String
        ) {

    fun getAmount(): Double {
        val bd: BigDecimal = BigDecimal.valueOf(amount)
        return bd.setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun getAmountAsEur(): String {
        return importoAsEur(getAmount())
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