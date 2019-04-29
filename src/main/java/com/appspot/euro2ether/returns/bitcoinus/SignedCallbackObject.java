package com.appspot.euro2ether.returns.bitcoinus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;

public class SignedCallbackObject implements Serializable {

    @JsonProperty("data")
    public DepositCallbackData data;

    @JsonProperty("signature")
    public String signature;

    /* --- Constructors */

    public SignedCallbackObject() {
    }

    public SignedCallbackObject(DepositCallbackData data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}


