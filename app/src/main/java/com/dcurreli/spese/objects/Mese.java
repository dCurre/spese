package com.dcurreli.spese.objects;

public class Mese {
    private int id;
    private String nome;// we're using the name as id
    private boolean saldato;

    public Mese(int id, String nome){
        this.id = id;
        this.nome = nome;
        this.saldato = false;
    }

    public Mese(){}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
