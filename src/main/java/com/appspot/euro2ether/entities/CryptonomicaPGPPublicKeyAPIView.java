package com.appspot.euro2ether.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;
import java.util.Date;

/**
 * Key representation that can be sent when requested using API for partner services
 */
@Entity // -> service.OfyService +
@Cache //
public class CryptonomicaPGPPublicKeyAPIView implements Serializable {

    @Id
    @JsonProperty("fingerprint")
    private String fingerprint; //.........................1
    @Index
    @JsonProperty("cryptonomicaUserId")
    private String cryptonomicaUserId; //..................2
    @JsonProperty("keyID")
    private String keyID; //...............................3
    @JsonProperty("userID")
    private String userID; //..............................4
    @JsonProperty("firstName")
    private String firstName; //...........................5
    @JsonProperty("lastName")
    private String lastName; //............................6
    @Index
    @JsonProperty("userEmail")
    private String userEmail; //...........................7
    @JsonProperty("created")
    private Date created; //...............................9
    @JsonProperty("exp")
    private Date exp; //..................................10
    @JsonProperty("bitStrength")
    private Integer bitStrength; //.......................11
    @JsonProperty("asciiArmored")
    private String asciiArmored; //.......................12
    @JsonProperty("nationality")
    private String nationality; //........................13
    @JsonProperty("verifiedOffline")
    private Boolean verifiedOffline; //...................15
    @JsonProperty("verifiedOnline")
    private Boolean verifiedOnline; //....................16
    @JsonProperty("revoked")
    private Boolean revoked; //...........................17
    @JsonProperty("revokedOn")
    private Date revokedOn; //............................18
    @JsonProperty("revokedBy")
    private String revokedBy; //..........................19
    @JsonProperty("revocationNotes")
    private String revocationNotes; //....................20
    @JsonProperty("birthdate")
    private String birthdate; //..........................21

    // PGPPublicKeyData has also: @Parent private Key<CryptonomicaUser> cryptonomicaUserKey;
    // we have cryptonomicaUserId + webSafeString and not need it

    /* ----- Constructors: */
    public CryptonomicaPGPPublicKeyAPIView() {
    }

    /* --- Methods */

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    /* ----- Getters and Setters: */

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getCryptonomicaUserId() {
        return cryptonomicaUserId;
    }

    public void setCryptonomicaUserId(String cryptonomicaUserId) {
        this.cryptonomicaUserId = cryptonomicaUserId;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Integer getBitStrength() {
        return bitStrength;
    }

    public void setBitStrength(Integer bitStrength) {
        this.bitStrength = bitStrength;
    }

    public String getAsciiArmored() {
        return asciiArmored;
    }

    public void setAsciiArmored(String asciiArmored) {
        this.asciiArmored = asciiArmored;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Boolean getVerifiedOffline() {
        return verifiedOffline;
    }

    public void setVerifiedOffline(Boolean verifiedOffline) {
        this.verifiedOffline = verifiedOffline;
    }

    public Boolean getVerifiedOnline() {
        return verifiedOnline;
    }

    public void setVerifiedOnline(Boolean verifiedOnline) {
        this.verifiedOnline = verifiedOnline;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Date getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(Date revokedOn) {
        this.revokedOn = revokedOn;
    }

    public String getRevokedBy() {
        return revokedBy;
    }

    public void setRevokedBy(String revokedBy) {
        this.revokedBy = revokedBy;
    }

    public String getRevocationNotes() {
        return revocationNotes;
    }

    public void setRevocationNotes(String revocationNotes) {
        this.revocationNotes = revocationNotes;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

}
