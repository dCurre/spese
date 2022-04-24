package com.dcurreli.spese.objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dcurreli.spese.utils.GenericUtils;
import com.dcurreli.spese.utils.DateUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Spesa {
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference().getRoot().child("spesa");
    private String id;
    private String spesa;
    private double importo;
    private String data;
    private Long timestamp;
    private String pagatore;
    private String listaSpesaID;

    public Spesa(String id, String spesa, double importo, String data, String pagatore, String listaSpesaID){
        this.id = id;
        this.spesa = spesa;
        this.importo = importo;
        this.data = data;
        this.timestamp = GenericUtils.INSTANCE.dateStringToTimestampSeconds(data, "dd/MM/yyyy");
        this.pagatore = pagatore;
        this.listaSpesaID = listaSpesaID;
    }

    public Spesa() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String extractMensilitaAnno() {
        String[] dataSpesa = this.data.split("/");
        return DateUtils.INSTANCE.getMonthAsText(dataSpesa[1]) + " " + dataSpesa[2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String idAsString() {
        return "" + id;
    }

    public String getSpesa() {
        return spesa;
    }

    public void setSpesa(String spesa) {
        this.spesa = spesa;
    }

    public double getImporto() {
        BigDecimal bd = BigDecimal.valueOf(importo);
        return bd.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public String importoAsTextEuro() {
        return GenericUtils.INSTANCE.importoAsEur(getImporto()).replace(".",",");
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

    public String getListaSpesaID() { return listaSpesaID; }

    public void setListaSpesaID(String listaSpesaID) { this.listaSpesaID = listaSpesaID; }

    public void delete(){
        db.child(this.id).removeValue();
    }
}
