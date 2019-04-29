package com.appspot.euro2ether.entities;

import com.google.appengine.api.users.User;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.Date;


@Entity // -> com.appspot.euro2ether.service.OfyService +
// @Cache // -- may be should not stored in cache to be able view changes faster
public class CodeForEthereumAddressVerification implements Serializable {

    @Id
    private String code; // user email
    private String prefix; // can be appended to code like "code to sign: <code>"
    @Index
    private String issuedFor; // google user ID
    @Index
    private String issuedForEmail; //
    @Index
    private Date issuedOn;
    @Index
    private Date verifiedOn;
    @Index
    private String verifiedForEthereumAddress;

    /* --- Constructor */

    public CodeForEthereumAddressVerification() {
    }

    public CodeForEthereumAddressVerification(String code) {
        this.code = code;
    }

    public CodeForEthereumAddressVerification(String code, String prefix, User googleUser, Date issuedOn) {
        this.code = code;
        this.prefix = prefix;
        this.issuedFor = googleUser.getUserId();
        this.issuedForEmail = googleUser.getEmail();
        this.issuedOn = issuedOn;
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* --- Getters and Setters */

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getIssuedFor() {
        return issuedFor;
    }

    public void setIssuedFor(String issuedFor) {
        this.issuedFor = issuedFor;
    }

    public String getIssuedForEmail() {
        return issuedForEmail;
    }

    public void setIssuedForEmail(String issuedForEmail) {
        this.issuedForEmail = issuedForEmail;
    }

    public Date getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Date issuedOn) {
        this.issuedOn = issuedOn;
    }

    public Date getVerifiedOn() {
        return verifiedOn;
    }

    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }

    public String getVerifiedForEthereumAddress() {
        return verifiedForEthereumAddress;
    }

    public void setVerifiedForEthereumAddress(String verifiedForEthereumAddress) {
        this.verifiedForEthereumAddress = verifiedForEthereumAddress;
    }

}
