package com.appspot.euro2ether.returns.bitcoinus;

import com.appspot.euro2ether.service.CryptoUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignedResponceObject implements Serializable {

    @JsonProperty("signature")
    public String signature;

    @JsonProperty("response")
    public String response;

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Boolean verifySignature(String key) throws NoSuchAlgorithmException, InvalidKeyException {

        if (!CryptoUtils.hmacSHA256(response, key).equalsIgnoreCase(signature)) {
            throw new VerifyError("Signature verification failed");
        }
        return Boolean.TRUE;
    }

}


