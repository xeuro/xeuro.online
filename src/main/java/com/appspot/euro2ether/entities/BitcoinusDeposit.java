package com.appspot.euro2ether.entities;


import com.appspot.euro2ether.returns.bitcoinus.DepositCallbackData;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.google.appengine.api.users.User;

import java.util.ArrayList;
import java.util.Date;

/*
 *
 * "type":"deposit","status":"Processed","currency":"EUR"
 *
 * */

@Entity // -> com.appspot.euro2ether.service.OfyService +
// @Cache // -- may be should not stored in cache to be able view changes faster
public class BitcoinusDeposit {

    /* --- data from Bitcoinus deposit call back */
    @Id
    private Long transactionId; // ("tid") ..............................1
    @Index
    private Integer projectId; // ("pid") ...............................2
    //
    private Integer amount; // ..........................................3
    @Index
    private String bitcoinusReference; // like "3x1176xqo9kmqbadd4q'.....4
    @Index
    private String paymentReference; // like "qo9kmqbadd4q"..............5
    @Index
    private String senderNameFromBitcoinus; //...........................6
    @Index
    private String senderIbanFromBitcoinus; //...........................7

    /* ---- data from PaymentReference entity*/
    @Index
    private String senderEthereumAddress;//..............................8

    /* --- data from Ethereum transaction */
    @Index
    private String mintTokensTxHash;//..................................9
    @Index
    private String transferTokensTxHash;//..............................10
    @Index
    private String mintAndTransferTokensTxHash;//.......................11
    @Index
    private ArrayList<String> allTxHashes;//............................12

    /* ---- Internal records: */
    @Index
    private Date entityCreated;//.......................................13
    @Index
    private Date entityChanged;//.......................................14
    private String internalNotes;//.....................................15
    @Index
    private Boolean needsManualRevue;//.................................15

    //
    private String txError;//...........................................16
    @Index
    private Boolean tokensTransferConfirmed;//..........................17
    @Index
    private User confirmedBy; //........................................18

    /* --- Constructors */

    public BitcoinusDeposit() {
    }

    public BitcoinusDeposit(DepositCallbackData depositCallbackData) {
        this.transactionId = depositCallbackData.tid; //.......1
        this.projectId = depositCallbackData.pid;//.........................2
        if (depositCallbackData.amount != null) {
            this.amount = depositCallbackData.amount.intValue();//.........3
        }
        this.bitcoinusReference = depositCallbackData.reference;//.........4
        this.senderNameFromBitcoinus = depositCallbackData.sender_name;//..53
        this.senderIbanFromBitcoinus = depositCallbackData.sender_iban;//..6
    }

    /* --- Methods */

    public Boolean addToTxHashes(String txHash) {
        return this.allTxHashes.add(txHash);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getBitcoinusReference() {
        return bitcoinusReference;
    }

    public void setBitcoinusReference(String bitcoinusReference) {
        this.bitcoinusReference = bitcoinusReference;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getSenderNameFromBitcoinus() {
        return senderNameFromBitcoinus;
    }

    public void setSenderNameFromBitcoinus(String senderNameFromBitcoinus) {
        this.senderNameFromBitcoinus = senderNameFromBitcoinus;
    }

    public String getSenderIbanFromBitcoinus() {
        return senderIbanFromBitcoinus;
    }

    public void setSenderIbanFromBitcoinus(String senderIbanFromBitcoinus) {
        this.senderIbanFromBitcoinus = senderIbanFromBitcoinus;
    }

    public String getSenderEthereumAddress() {
        return senderEthereumAddress;
    }

    public void setSenderEthereumAddress(String senderEthereumAddress) {
        this.senderEthereumAddress = senderEthereumAddress;
    }

    public String getMintTokensTxHash() {
        return mintTokensTxHash;
    }

    public void setMintTokensTxHash(String mintTokensTxHash) {
        this.mintTokensTxHash = mintTokensTxHash;
    }

    public String getTransferTokensTxHash() {
        return transferTokensTxHash;
    }

    public void setTransferTokensTxHash(String transferTokensTxHash) {
        this.transferTokensTxHash = transferTokensTxHash;
    }

    public String getMintAndTransferTokensTxHash() {
        return mintAndTransferTokensTxHash;
    }

    public void setMintAndTransferTokensTxHash(String mintAndTransferTokensTxHash) {
        this.mintAndTransferTokensTxHash = mintAndTransferTokensTxHash;
    }

    public ArrayList<String> getAllTxHashes() {
        return allTxHashes;
    }

    public void setAllTxHashes(ArrayList<String> allTxHashes) {
        this.allTxHashes = allTxHashes;
    }

    public Date getEntityCreated() {
        return entityCreated;
    }

    public void setEntityCreated(Date entityCreated) {
        this.entityCreated = entityCreated;
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

    public String getTxError() {
        return txError;
    }

    public void setTxError(String txError) {
        this.txError = txError;
    }

    public Boolean getTokensTransferConfirmed() {
        return tokensTransferConfirmed;
    }

    public void setTokensTransferConfirmed(Boolean tokensTransferConfirmed) {
        this.tokensTransferConfirmed = tokensTransferConfirmed;
    }

    public User getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(User confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

}
