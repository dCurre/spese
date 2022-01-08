package com.dcurreli.spese.objects;

import com.dcurreli.spese.utils.GenericUtils;
import com.dcurreli.spese.utils.MeseUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ListaSpese {
    private int id = 1;
    private String nome = null;
    private ArrayList<String> partecipanti = null;
    private String owner;

    public ListaSpese(int id, String nome, ArrayList<String> partecipanti, String owner) {
        this.id = id;
        this.nome = nome;
        this.partecipanti = partecipanti;
        this.owner = owner;
    }

    public ListaSpese() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
