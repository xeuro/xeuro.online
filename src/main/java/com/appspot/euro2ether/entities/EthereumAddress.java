package com.appspot.euro2ether.entities;

import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.Date;

@Entity // -> com.appspot.euro2ether.service.OfyService
// @Cache // -- may be should not stored in cache to be able view changes faster
public class EthereumAddress implements Serializable {

    @Id
    private String ethereumAddress;
    @Index
    private String paymentReference;
    @Index
    private String IBAN;
    //
    private String ibanAccountOwnerName;
    @Index
    private Date createdOn;
    @Index
    private String userId;
    @Index
    private String userEmail;
    @Index
    private String userName;
    @Index
    private Date verifiedOn;
    @Index
    private Date changedOn;

    /* --- Constructor */

    public EthereumAddress() {
    }

    public EthereumAddress(String ethereumAddress, User googleUser, String paymentReference, Date createdOn) {
        this.ethereumAddress = ethereumAddress;
        this.paymentReference = paymentReference;
        this.userId = googleUser.getUserId();
        this.userEmail = googleUser.getEmail();
        this.createdOn = createdOn;
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getEthereumAddress() {
        return ethereumAddress;
    }

    public void setEthereumAddress(String ethereumAddress) {
        this.ethereumAddress = ethereumAddress;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getIbanAccountOwnerName() {
        return ibanAccountOwnerName;
    }

    public void setIbanAccountOwnerName(String ibanAccountOwnerName) {
        this.ibanAccountOwnerName = ibanAccountOwnerName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

}
