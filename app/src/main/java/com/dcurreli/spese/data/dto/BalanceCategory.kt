package com.dcurreli.spese.data.dto

import com.dcurreli.spese.utils.GenericUtils.importoAsEur

data class BalanceCategory(
    val buyer: String,
    val paidAmount: Double,
    val amountsToReceive: ArrayList<BalanceSubItem>
    ) {

    fun getPaidAmountAsEUR(): String {
        return importoAsEur(paidAmount)
    }
}