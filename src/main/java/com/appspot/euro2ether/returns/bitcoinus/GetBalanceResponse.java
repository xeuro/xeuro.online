package com.appspot.euro2ether.returns.bitcoinus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;

public class GetBalanceResponse implements Serializable {

    @JsonProperty("status")
    public Integer status;

    @JsonProperty("clientid")
    public Integer clientid;

    @JsonProperty("balances")
    // public Balances balances;
    public HashMap<String, Double> balances;

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
