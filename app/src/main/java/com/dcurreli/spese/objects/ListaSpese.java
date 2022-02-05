package com.dcurreli.spese.objects;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ListaSpese {
    private String id = null;
    private String nome = null;
    private ArrayList<String> partecipanti = null;
    private String owner;
    private String categoria;
    private boolean saldato;

    public ListaSpese(String id, String nome, ArrayList<String> partecipanti, String owner, String categoria) {
        this.id = id;
        this.nome = nome;
        this.partecipanti = partecipanti;
        this.owner = owner;
        this.categoria = categoria;
        this.saldato = false;
    }

    public ListaSpese() {
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

    public ArrayList<String> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(ArrayList<String> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public int sizeListaPartecipanti(){
        return partecipanti.size();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isSaldato() {
        return saldato;
    }

    public void setSaldato(boolean saldato) {
        this.saldato = saldato;
    }
}
