package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.MeseUtils;
import com.dcurreli.spese.utils.SpesaUtils;

public class Spesa {
    private int id;
    private String luogo;
    private double importo;
    private String data;
    private String pagatore;

    public Spesa(int id, String luogo, double importo, String data, String pagatore){
        this.id = id;
        this.luogo = luogo;
        this.importo = importo;
        this.data = data;
        this.pagatore = pagatore;
    }

    public Spesa(){
    }

    public String extractMensilitaAnno(){
        String[] dataSpesa = this.data.split("/");
        return MeseUtils.INSTANCE.getMonthAsText(dataSpesa[1])+ " " + dataSpesa[2];
    }

    public int getId() {
        return id;
    }

    public String idTextAsString() {
        return ""+id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public double getImporto() {
        return importo;
    }
    public String importoAsText() {
        return ""+importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public String getData() { return data; }

    public void setData(String data) {
        this.data = data;
    }

    public String getPagatore() {
        return pagatore;
    }

    public void setPagatore(String pagatore) {
        this.pagatore = pagatore;
    }
}
