package com.ajimatech.android.ezpz.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    //creator - used when un-parceling our parcel (creating the object)
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel parcel) {
            return new Contact(parcel);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    public static final String SELECTED_CONTACT = "selected_contact";

    private String fullname;
    private String photoUri;
    private String numberMobile;
    private String facebookId;
    private String numberViber;
    private String numberGVoice;

    public Contact(String fullname) {
        this.fullname = fullname;
    }

    protected Contact(Parcel parcel) {
        fullname = parcel.readString();
        photoUri = parcel.readString();
        numberMobile = parcel.readString();
        facebookId = parcel.readString();
        numberViber = parcel.readString();
        numberGVoice = parcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fullname);
        parcel.writeString(photoUri);
        parcel.writeString(numberMobile);
        parcel.writeString(facebookId);
        parcel.writeString(numberViber);
        parcel.writeString(numberGVoice);
    }
}
