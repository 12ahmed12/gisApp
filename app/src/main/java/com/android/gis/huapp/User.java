package com.android.gis.huapp;

/**
 * Created by spider on 07/06/2016.
 */
public class User {
    String id;
    String name;
    String Username;
    String Password;
    String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    String Regist_date;
    String LastLogin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastLogin() {
        return LastLogin;
    }

    public void setLastLogin(String lastLogin) {
        LastLogin = lastLogin;
    }

    public String getRegist_date() {
        return Regist_date;
    }

    public void setRegist_date(String regist_date) {
        Regist_date = regist_date;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
