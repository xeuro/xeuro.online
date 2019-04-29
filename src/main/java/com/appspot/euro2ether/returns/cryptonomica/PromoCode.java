package com.appspot.euro2ether.returns.cryptonomica;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;

public class PromoCode implements Serializable {

    @JsonProperty("promoCode")
    public String promoCode;

    @JsonProperty("discountInPercent")
    public Integer discountInPercent;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
