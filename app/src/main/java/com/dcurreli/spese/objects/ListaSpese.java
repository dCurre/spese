package com.dcurreli.spese.objects;

import androidx.annotation.NonNull;

import com.dcurreli.spese.utils.GenericUtils;
import com.dcurreli.spese.utils.MeseUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ListaSpese {
    private String id = null;
    private String nome = null;
    private ArrayList<String> partecipanti = null;
    private String owner;
    private String categoria;

    public ListaSpese(String id, String nome, ArrayList<String> partecipanti, String owner, String categoria) {
        this.id = id;
        this.nome = nome;
        this.partecipanti = partecipanti;
        this.owner = owner;
        this.categoria = categoria;
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

    @NonNull
    @Override
    public String toString() {
        return this.nome;
    }
}
