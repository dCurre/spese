package com.dcurreli.spese.objects;

import android.net.Uri;

public class Utente {
    private String user_id;
    private String nominativo;
    private String email;
    private String image;
    private boolean darkTheme;
    private boolean nascondiListeSaldate;

    public Utente(String uid, String nominativo, String email, String image){
        this.user_id = uid;
        this.nominativo = nominativo;
        this.email = email;
        this.image = image;
        this.darkTheme = false;
        this.nascondiListeSaldate = false;
    }
    public Utente(){
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public boolean isNascondiListeSaldate() { return nascondiListeSaldate; }

    public void setNascondiListeSaldate(boolean nascondiListeSaldate) { this.nascondiListeSaldate = nascondiListeSaldate; }
}
