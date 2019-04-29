package com.appspot.euro2ether.returns;

import com.google.gson.Gson;
import jnr.ffi.annotations.In;
import org.web3j.tuples.generated.Tuple5;

import java.io.Serializable;
import java.util.Date;

public class MintTokensEventStruct implements Serializable {

    private String mintedBy; // Ethereum address
    private Integer fiatInPaymentId; //
    private Integer value;   //
    private Integer on;    // UnixTime
    private Integer currentTotalSupply;

    public MintTokensEventStruct(String mintedBy) {
        this.mintedBy = mintedBy;
    }

    public MintTokensEventStruct(Tuple5 tuple5) {
        this.mintedBy = String.valueOf(tuple5.getValue1());
        this.fiatInPaymentId = Integer.parseInt(String.valueOf(tuple5.getValue2()));
        this.value = Integer.parseInt(String.valueOf(tuple5.getValue3()));
        this.on = Integer.parseInt(String.valueOf(tuple5.getValue4()));
        this.currentTotalSupply = Integer.parseInt(String.valueOf(tuple5.getValue5()));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getMintedBy() {
        return mintedBy;
    }

    public void setMintedBy(String mintedBy) {
        this.mintedBy = mintedBy;
    }

    public Integer getFiatInPaymentId() {
        return fiatInPaymentId;
    }

    public void setFiatInPaymentId(Integer fiatInPaymentId) {
        this.fiatInPaymentId = fiatInPaymentId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getOn() {
        return on;
    }

    public void setOn(Integer on) {
        this.on = on;
    }

    public Integer getCurrentTotalSupply() {
        return currentTotalSupply;
    }

    public void setCurrentTotalSupply(Integer currentTotalSupply) {
        this.currentTotalSupply = currentTotalSupply;
    }

}
