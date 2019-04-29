package com.appspot.euro2ether.returns.bitcoinus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;

public class DepositCallbackData implements Serializable {

    /* data: {"pid":73,"tid":null,"type":"deposit","status":"Processed","currency":"EUR",
    "amount":7.0999999999999996447286321199499070644378662109375,"reference":"0x987test01",
    "sender_name":"Viktor Ageyev","sender_iban":"LT183510000088270907"
    } */

    @JsonProperty("pid")
    public Integer pid; // project ID

    @JsonProperty("tid")
    // public Integer tid; // transaction ID
    public Long tid; // transaction ID

    @JsonProperty("type")
    public String type; // "deposit"

    @JsonProperty("status")
    public String status; // "Processed"

    @JsonProperty("currency")
    public String currency; // "currency":"EUR"

    @JsonProperty("amount")
    public Double amount;

    @JsonProperty("reference")
    public String reference;

    @JsonProperty("sender_name")
    public String sender_name;

    @JsonProperty("sender_iban")
    public String sender_iban;

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

