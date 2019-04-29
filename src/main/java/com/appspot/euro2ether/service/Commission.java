package com.appspot.euro2ether.service;

import com.google.gson.Gson;

import java.util.logging.Logger;

public class Commission {
    private static final Logger LOG = Logger.getLogger(Commission.class.getName());
    private static final Integer fraction = 1000; //
    static Integer minCommission; // EUR
    private Integer commission; // 10 = 1%
    private Integer amount;
    private Integer commissionSum;
    private Integer amountFinalSum;

    public Commission(Integer commission, Integer minCommission) {
        this.commission = commission;
        this.minCommission = minCommission;
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void calculateExchangeCommission(final Integer amount) {

        this.commissionSum = amount * commission / fraction;
        this.amountFinalSum = amount - commissionSum;

        /* --- calculate amount less commission:  */

        if (commissionSum < minCommission) {
            commissionSum = minCommission;
            amountFinalSum = amount - commissionSum;
        }

        if (amountFinalSum < 0) {
            amountFinalSum = 0;
        }

        /* --- calculate real commission sum:  */

        commissionSum = amount - amountFinalSum;
        if (commissionSum < 0) {
            commissionSum = 0;
        }

        this.amount = amount;

        // LOG.warning(this.toString());

    }

    /* ---- Getters and Setters */


    public Integer getAmount() {
        return amount;
    }

    public Integer getCommissionSum() {
        return commissionSum;
    }

    public Integer getAmountFinalSum() {
        return amountFinalSum;
    }

}

