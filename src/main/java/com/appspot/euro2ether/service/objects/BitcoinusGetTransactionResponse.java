package com.appspot.euro2ether.service.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;

/*
"response": {
    "id": 2653,
    "user_id": 1077,
    "type": "deposit",
    "amount_src": 0.1000000000000000055511151231257827021181583404541015625,
    "amount_dst": "0.1",
    "currency_src": "ETH",
    "currency_dst": "ETH",
    "fee": "0",
    "rate": "1",
    "reference": null,
    "status": "Processed",
    "external_id": null,
    "desc": null,
    "timestamp": {
      "date": "2019-03-15 11:40:42.000000",
      "timezone_type": 3,
      "timezone": "Europe/Vilnius"
    }
* */

// last change: 2019-04-07
public class BitcoinusGetTransactionResponse implements Serializable {

    @JsonProperty("id")
    public Integer id; // transaction ID > Integer works better in Jackson

    @JsonProperty("user_id")
    public Integer user_id; // > Integer works better in Jackson

    @JsonProperty("type")
    public String type;

    @JsonProperty("amount_src")
    public Double amount_src;

    @JsonProperty("amount_dst")
    public Double amount_dst;

    @JsonProperty("currency_src")
    public String currency_src;

    @JsonProperty("currency_dst")
    public String currency_dst;

    @JsonProperty("fee")
    public Double fee;

    @JsonProperty("rate")
    public Double rate;

    @JsonProperty("reference")
    public String reference;

    @JsonProperty("status")
    public String status;

    @JsonProperty("external_id")
    public String external_id;

    @JsonProperty("desc")
    public String desc;

    @JsonProperty("timestamp")
    BitcoinusTimestamp timestamp;

    /* --- Constructor */

    public BitcoinusGetTransactionResponse() {
    }

    /* --- Methods */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount_src() {
        return amount_src;
    }

    public void setAmount_src(Double amount_src) {
        this.amount_src = amount_src;
    }

    public Double getAmount_dst() {
        return amount_dst;
    }

    public void setAmount_dst(Double amount_dst) {
        this.amount_dst = amount_dst;
    }

    public String getCurrency_src() {
        return currency_src;
    }

    public void setCurrency_src(String currency_src) {
        this.currency_src = currency_src;
    }

    public String getCurrency_dst() {
        return currency_dst;
    }

    public void setCurrency_dst(String currency_dst) {
        this.currency_dst = currency_dst;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BitcoinusTimestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BitcoinusTimestamp timestamp) {
        this.timestamp = timestamp;
    }

}
