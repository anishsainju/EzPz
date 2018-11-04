package com.ajimatech.android.ezpz.model;

public class Contact {

    private String fullname;
    private String photoUri;
    private String numberUS2US;
    private String facebookId;

    public Contact(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getNumberUS2US() {
        return numberUS2US;
    }

    public void setNumberUS2US(String numberUS2US) {
        this.numberUS2US = numberUS2US;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
