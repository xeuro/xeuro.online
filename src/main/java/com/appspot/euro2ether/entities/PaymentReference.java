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
public class PaymentReference implements Serializable {

    @Id
    private String paymentReference;
    @Index
    // (!!!) payment reference connected to Ethereum address
    // if ETH address change > change payment reference
    private String ethereumAddress;
    //
    @Index
    private Date createdOn;
    @Index
    private String userId;
    @Index
    private String userEmail;

    /* --- Constructor */

    public PaymentReference() {
    }

    public PaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public PaymentReference(String paymentReference, String ethereumAddress, User googleUser, Date createdOn) {
        this.paymentReference = paymentReference;
        this.ethereumAddress = ethereumAddress;
        this.createdOn = createdOn;
        this.userId = googleUser.getUserId();
        this.userEmail = googleUser.getEmail();
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getEthereumAddress() {
        return ethereumAddress;
    }

    public void setEthereumAddress(String ethereumAddress) {
        this.ethereumAddress = ethereumAddress;
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

}
