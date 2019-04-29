package com.appspot.euro2ether.returns.bitcoinus;

import com.appspot.euro2ether.service.objects.BitcoinusTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;

public class SendFundsResponse implements Serializable {

    @JsonProperty("id")
    public Integer id;

    @JsonProperty("user_id")
    public Integer user_id;

    @JsonProperty("type")
    public String type; // "withdrawal"

    @JsonProperty("amount_src")
    public Double amount_src;

    @JsonProperty("amount_dst")
    public Double amount_dst;

    @JsonProperty("currency")
    public String currency;

    @JsonProperty("fee")
    public Double fee;

    @JsonProperty("status")
    public String status; // "Pending"

    @JsonProperty("timestamp")
    // public String timestamp;
    public BitcoinusTimestamp timestamp;

    @JsonProperty("message")
    public String message;

    public SendFundsResponse() {
        //
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BitcoinusTimestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BitcoinusTimestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
