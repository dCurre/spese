package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.GenericUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SaldoSubItem {
    private String pagatore;
    private String daPagareA;
    private Double importoDaPagare;

    public SaldoSubItem(String pagatore, String daPagareA, Double importoDaPagare){
        this.pagatore = pagatore;
        this.daPagareA = daPagareA;
        this.importoDaPagare = importoDaPagare;
    }

    public SaldoSubItem() {
    }

    public String getPagatore() {
        return pagatore;
    }

    public void setPagatore(String pagatore) {
        this.pagatore = pagatore;
    }

    public String getDaPagareA() {
        return daPagareA;
    }

    public void setDaPagareA(String daPagareA) {
        this.daPagareA = daPagareA;
    }

    public Double getImportoDaPagare() {
        return this.importoDaPagare;
    }

    public void setImportoDaPagare(Double importoDaPagare) {
        this.importoDaPagare = importoDaPagare;
    }

    public Double getImportoFixed() {
        return (this.importoDaPagare >= 0)
            ? BigDecimal.valueOf(importoDaPagare).setScale(2, RoundingMode.HALF_EVEN).doubleValue()
            : 0.00;
    }

    public String getImportoDaPagareFixedAsEur() {
        return GenericUtils.INSTANCE.importoAsEur(getImportoFixed()).replace(".",",");
    }

}
