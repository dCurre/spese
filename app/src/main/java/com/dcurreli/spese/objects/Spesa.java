package com.dcurreli.spese.objects;

public class Spesa {

    private String luogo;
    private double importo;
    private String data;
    private String pagatore;

    public Spesa(String luogo, double importo, String data, String pagatore){
        this.luogo = luogo;
        this.importo = importo;
        this.data = data;
        this.pagatore = pagatore;
    }

    public Spesa(){
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
