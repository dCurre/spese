package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.GenericUtils;
import com.dcurreli.spese.utils.MeseUtils;

import java.text.ParseException;

public class Spesa {
    private String id;
    private String spesa;
    private double importo;
    private String data;
    private Long timestamp;
    private String pagatore;

    public Spesa(String id, String spesa, double importo, String data, String pagatore){
        this.id = id;
        this.spesa = spesa;
        this.importo = importo;
        this.data = data;
        this.timestamp = GenericUtils.INSTANCE.dateStringToTimestampSeconds(data, "dd/MM/yyyy");
        this.pagatore = pagatore;
    }

    public Spesa() {
    }

    public String extractMensilitaAnno() {
        String[] dataSpesa = this.data.split("/");
        return MeseUtils.INSTANCE.getMonthAsText(dataSpesa[1]) + " " + dataSpesa[2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String idTextAsString() {
        return "" + id;
    }

    public String getSpesa() {
        return spesa;
    }

    public void setSpesa(String spesa) {
        this.spesa = spesa;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public String importoAsTextEuro() {
        return "" + importo + "€";
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPagatore() {
        return pagatore;
    }

    public void setPagatore(String pagatore) {
        this.pagatore = pagatore;
    }
}
