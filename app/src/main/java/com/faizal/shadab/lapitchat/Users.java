package com.faizal.shadab.lapitchat;

public class Users {

    public String name;
    public String display_picture;
    public String status;

    public Users(){

    }

    public Users(String name, String display_picture, String status) {
        this.name = name;
        this.display_picture = display_picture;
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplay_picture(String display_picture) {
        this.display_picture = display_picture;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDisplay_picture() {
        return display_picture;
    }

    public String getStatus() {
        return status;
    }
}
