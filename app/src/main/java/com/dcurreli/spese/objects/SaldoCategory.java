package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.GenericUtils;

import java.util.ArrayList;

public class SaldoCategory {
    private String pagatore;
    private Double importoPagato;
    private ArrayList<SaldoSubItem> pagatoreImportoDaAvere;

    public SaldoCategory(String pagatore, Double importoPagato, ArrayList<SaldoSubItem> pagatoreImportoDaAvere){
        this.pagatore = pagatore;
        this.importoPagato = importoPagato;
        this.pagatoreImportoDaAvere = pagatoreImportoDaAvere;
    }

    public String getPagatore() {
        return pagatore;
    }

    public void setPagatore(String pagatore) {
        this.pagatore = pagatore;
    }

    public ArrayList<SaldoSubItem> getPagatoreImportoDaAvere() {
        return pagatoreImportoDaAvere;
    }

    public void setPagatoreImportoDaAvere(ArrayList<SaldoSubItem> pagatoreImportoDaAvere) {
        this.pagatoreImportoDaAvere = pagatoreImportoDaAvere;
    }

    public Double getImportoPagato() {
        return this.importoPagato;
    }

    public void setImportoPagato(Double importoPagato) {
        this.importoPagato = importoPagato;
    }

    public String getImportoPagatoAsEur() {
        return GenericUtils.INSTANCE.importoAsEur(getImportoPagato()).replace(".",",");
    }

}
