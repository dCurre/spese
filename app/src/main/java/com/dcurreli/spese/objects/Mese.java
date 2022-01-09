package com.dcurreli.spese.objects;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dcurreli.spese.utils.GenericUtils;

public class Mese {
    private String id;
    private String nome;// we're using the name as id
    private boolean saldato;
    private Long timestamp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Mese(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.saldato = false;
        this.timestamp = GenericUtils.INSTANCE.dateStringToTimestampSeconds(GenericUtils.INSTANCE.firstDayOfMonth(nome), "yyyy-MM-dd");
    }

    public Mese() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isSaldato() {
        return saldato;
    }

    public void setSaldato(boolean saldato) {
        this.saldato = saldato;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void salda() {
        this.saldato = true;
    }


}
