package com.appspot.euro2ether.returns;

import com.appspot.euro2ether.entities.AppUser;
import com.appspot.euro2ether.entities.CryptonomicaPGPPublicKeyAPIView;
import com.google.gson.Gson;

import java.io.Serializable;

public class UserData implements Serializable {

    private AppUser appUser;
    private CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView;

    /* --- Constructor */

    public UserData() {
    }

    /* --- Methods*/

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    /* --- Getters and Setters */

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public CryptonomicaPGPPublicKeyAPIView getPgpPublicKeyAPIView() {
        return pgpPublicKeyAPIView;
    }

    public void setPgpPublicKeyAPIView(CryptonomicaPGPPublicKeyAPIView pgpPublicKeyAPIView) {
        this.pgpPublicKeyAPIView = pgpPublicKeyAPIView;
    }

}
