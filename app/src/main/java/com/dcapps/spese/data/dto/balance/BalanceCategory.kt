package com.dcapps.spese.data.dto.balance

import com.dcapps.spese.utils.GenericUtils.importoAsEur

data class BalanceCategory(
    val buyer: String,
    val paidAmount: Double,
    val amountsToReceive: ArrayList<BalanceSubItem>
    ) {

    fun getPaidAmountAsEUR(): String {
        return importoAsEur(paidAmount)
    }
}