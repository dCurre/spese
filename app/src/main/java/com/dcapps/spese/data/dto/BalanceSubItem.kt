package com.dcapps.spese.data.dto

import com.dcapps.spese.utils.GenericUtils.importoAsEur
import java.math.BigDecimal
import java.math.RoundingMode

data class BalanceSubItem(
    val buyer: String,
    val receiver: String,
    val amountToPay: Double
    ) {

    fun getAmountToPayFixed(): Double {
        return if (amountToPay >= 0)
            BigDecimal.valueOf(amountToPay).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        else
            0.00
    }

    fun getAmountToPayAsEur(): String {
        return importoAsEur(getAmountToPayFixed())
    }
}