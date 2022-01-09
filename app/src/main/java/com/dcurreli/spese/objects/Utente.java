package com.dcurreli.spese.objects;

public class Utente {
    private String user_id;

    public Utente(String uid){
        this.user_id = uid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
