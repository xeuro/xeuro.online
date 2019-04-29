package com.appspot.euro2ether.entities;

import com.appspot.euro2ether.returns.bitcoinus.SendFundsResponse;
import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.Serializable;
import java.util.Date;

/*
 * see:
 * https://github.com/objectify/objectify/wiki/Entities
 * */
@Entity // -> com.appspot.euro2ether.service.OfyService
// @Cache // ->  do not cache this entity !!!!!!!!!!!
public class TokensInEvent implements Serializable {
    @Id
    // tokensInEventsCounter:
    private Long Id; // ..................................................1
    @Index
    private Integer networkId; //.........................................2
    @Index
    private String from; //...............................................3
    @Index
    private Long value; //................................................4
    @Index
    private Long tokensInEventsCounter; //................................5
    @Index
    private Date entityCreated; //........................................6
    @Index
    private String txHash; //.............................................7
    @Index
    private Date entityChanged;//.........................................8
    //
    private String internalNotes;//.......................................9
    @Index
    private Boolean needsManualRevue;//..................................10
    @Index
    private Boolean confirmed;//.........................................11
    @Index
    private User confirmedBy; //.........................................12
    //
    @Index
    private Integer sendFundsTxId;//.....................................13
    //
    private SendFundsResponse sendFundsResponse; //......................14
    //
    private TransactionReceipt burnTokensTxReceipt; //...................15

    /* -- Constructor */

    public TokensInEvent() {
    }

    public TokensInEvent(Long id) {
        Id = id;
    }

    /* ---- methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getTokensInEventsCounter() {
        return tokensInEventsCounter;
    }

    public void setTokensInEventsCounter(Long tokensInEventsCounter) {
        this.tokensInEventsCounter = tokensInEventsCounter;
    }

    public Date getEntityCreated() {
        return entityCreated;
    }

    public void setEntityCreated(Date entityCreated) {
        this.entityCreated = entityCreated;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Date getEntityChanged() {
        return entityChanged;
    }

    public void setEntityChanged(Date entityChanged) {
        this.entityChanged = entityChanged;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public Boolean getNeedsManualRevue() {
        return needsManualRevue;
    }

    public void setNeedsManualRevue(Boolean needsManualRevue) {
        this.needsManualRevue = needsManualRevue;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Integer getSendFundsTxId() {
        return sendFundsTxId;
    }

    public void setSendFundsTxId(Integer sendFundsTxId) {
        this.sendFundsTxId = sendFundsTxId;
    }

    public SendFundsResponse getSendFundsResponse() {
        return sendFundsResponse;
    }

    public void setSendFundsResponse(SendFundsResponse sendFundsResponse) {
        this.sendFundsResponse = sendFundsResponse;
    }

    public TransactionReceipt getBurnTokensTxReceipt() {
        return burnTokensTxReceipt;
    }

    public void setBurnTokensTxReceipt(TransactionReceipt burnTokensTxReceipt) {
        this.burnTokensTxReceipt = burnTokensTxReceipt;
    }

}
