package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.GenericUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DareAvere {
    private String dare;
    private String avere;
    private Double importo;

    public DareAvere(String dare, String avere, Double importo){
        this.dare = dare;
        this.avere = avere;
        this.importo = importo;
    }

    public DareAvere() {
    }

    public String getDare() {
        return dare;
    }

    public void setDare(String dare) {
        this.dare = dare;
    }

    public String getAvere() {
        return avere;
    }

    public void setAvere(String avere) {
        this.avere = avere;
    }

    public Double getImporto() {
        if(this.importo < 0){
            BigDecimal bd = BigDecimal.valueOf(importo*(-1));
            return bd.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        }else
            return 0.00;
    }

    public String getImportoAsEur() {
        return GenericUtils.INSTANCE.importoAsEur(getImporto()).replace(".",",");
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }
}
