package com.appspot.euro2ether.inputs;

import com.google.gson.Gson;

import java.io.Serializable;

public class CheckEthereumSignatureRequest implements Serializable {

    private String signedString;
    private String ethereumSignature;
    private String ethereumAddress;

    /* --- constructor */

    public CheckEthereumSignatureRequest() {
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getSignedString() {
        return signedString;
    }

    public void setSignedString(String signedString) {
        this.signedString = signedString;
    }

    public String getEthereumSignature() {
        return ethereumSignature;
    }

    public void setEthereumSignature(String ethereumSignature) {
        this.ethereumSignature = ethereumSignature;
    }

    public String getEthereumAddress() {
        return ethereumAddress;
    }

    public void setEthereumAddress(String ethereumAddress) {
        this.ethereumAddress = ethereumAddress;
    }

}
