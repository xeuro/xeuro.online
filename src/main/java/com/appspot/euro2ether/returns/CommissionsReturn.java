package com.appspot.euro2ether.returns;

import com.google.gson.Gson;

import java.io.Serializable;

public class CommissionsReturn implements Serializable {

    private Float commissionIn;
    private Integer minCommissionIn;
    private Float commissionOut;
    private Integer minCommissionOut;
    private Integer minAmountToExchange;

    /* --- Constructor */

    public CommissionsReturn() {
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public Float getCommissionIn() {
        return commissionIn;
    }

    public void setCommissionIn(Float commissionIn) {
        this.commissionIn = commissionIn;
    }

    public Integer getMinCommissionIn() {
        return minCommissionIn;
    }

    public void setMinCommissionIn(Integer minCommissionIn) {
        this.minCommissionIn = minCommissionIn;
    }

    public Float getCommissionOut() {
        return commissionOut;
    }

    public void setCommissionOut(Float commissionOut) {
        this.commissionOut = commissionOut;
    }

    public Integer getMinCommissionOut() {
        return minCommissionOut;
    }

    public void setMinCommissionOut(Integer minCommissionOut) {
        this.minCommissionOut = minCommissionOut;
    }

    public Integer getMinAmountToExchange() {
        return minAmountToExchange;
    }

    public void setMinAmountToExchange(Integer minAmountToExchange) {
        this.minAmountToExchange = minAmountToExchange;
    }

}
