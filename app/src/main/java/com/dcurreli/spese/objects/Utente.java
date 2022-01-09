package com.dcurreli.spese.objects;

public class Utente {
    private String user_id;
    private boolean darkTheme;

    public Utente(String uid){
        this.user_id = uid;
        this.darkTheme = false;
    }
    public Utente(){
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
}
