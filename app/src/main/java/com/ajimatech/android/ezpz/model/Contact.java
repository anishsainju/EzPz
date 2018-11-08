package com.ajimatech.android.ezpz.model;

public class Contact {

    private String fullname;
    private String photoUri;
    private String numberMobile;
    private String facebookId;
    private String numberViber;
    private String numberGVoice;

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

    public String getNumberMobile() {
        return numberMobile;
    }

    public void setNumberMobile(String numberMobile) {
        this.numberMobile = numberMobile;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getNumberViber() {
        return numberViber;
    }

    public void setNumberViber(String numberViber) {
        this.numberViber = numberViber;
    }

    public String getNumberGVoice() {
        return numberGVoice;
    }

    public void setNumberGVoice(String numberGVoice) {
        this.numberGVoice = numberGVoice;
    }
}
